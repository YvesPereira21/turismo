package io.turismo.backend.security;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.lettuce.core.RedisConnectionException;
import io.turismo.backend.model.User;
import io.turismo.backend.model.enums.UserRole;
import io.turismo.backend.ratelimit.TokenBucketRateLimiter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class CreationRateLimitInterceptorTest {

    private static final String SPOT = "/api/v1/tourist-spots/" + UUID.randomUUID() + "/photos";
    private static final String ACTIVITY = "/api/v1/activities/" + UUID.randomUUID() + "/photos";

    private final JsonMapper jsonMapper = JsonMapper.builder().build();
    private final TokenBucketRateLimiter rateLimiter = mock(TokenBucketRateLimiter.class);
    private final CreationRateLimitInterceptor filter = new CreationRateLimitInterceptor(rateLimiter, jsonMapper, 2);

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void uploadRoutesShareAQuotaPerUser() throws Exception {
        var buckets = new HashMap<String, Bucket>();
        when(rateLimiter.consume(any(), any())).thenAnswer(call -> {
            String key = call.getArgument(0);
            BucketConfiguration configuration = call.getArgument(1);
            return buckets.computeIfAbsent(key, ignored -> Bucket.builder()
                    .addLimit(configuration.getBandwidths()[0])
                    .build()).tryConsumeAndReturnRemaining(1);
        });

        authenticate(UserRole.SPOTMANAGER);
        assertThat(request("POST", SPOT).getStatus()).isEqualTo(204);
        assertThat(request("POST", ACTIVITY).getStatus()).isEqualTo(204);

        var blocked = request("PUT", ACTIVITY);
        assertThat(blocked.getStatus()).isEqualTo(429);
        assertThat(Long.parseLong(blocked.getHeader("Retry-After"))).isBetween(1L, 60L);
        assertThat(jsonMapper.readTree(blocked.getContentAsByteArray()).get("code").asInt()).isEqualTo(429);

        authenticate(UserRole.SPOTMANAGER);
        assertThat(request("PUT", ACTIVITY).getStatus()).isEqualTo(204);
    }

    @Test
    void unrelatedRequestsAndUsersWithoutUploadAccessDoNotConsumeQuota() throws Exception {
        assertThat(request("POST", SPOT).getStatus()).isEqualTo(204);
        authenticate(UserRole.TOURIST);
        assertThat(request("POST", SPOT).getStatus()).isEqualTo(204);
        authenticate(UserRole.SPOTMANAGER);
        assertThat(request("PUT", SPOT).getStatus()).isEqualTo(204);
        assertThat(request("GET", ACTIVITY).getStatus()).isEqualTo(204);
        verifyNoInteractions(rateLimiter);
    }

    @Test
    void redisFailureReturnsServiceUnavailable() throws Exception {
        authenticate(UserRole.SPOTMANAGER);
        when(rateLimiter.consume(any(), any())).thenThrow(new RedisConnectionException("unavailable"));
        var response = request("POST", ACTIVITY);
        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(jsonMapper.readTree(response.getContentAsByteArray()).get("code").asInt()).isEqualTo(503);
    }

    private static void authenticate(UserRole role) {
        var user = User.builder().id(UUID.randomUUID()).role(role).build();
        SecurityContextHolder.getContext().setAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(user, null, user.getAuthorities()));
    }

    private MockHttpServletResponse request(String method, String path) throws Exception {
        var response = new MockHttpServletResponse();
        filter.doFilter(new MockHttpServletRequest(method, path), response,
                (req, res) -> ((MockHttpServletResponse) res).setStatus(204));
        return response;
    }
}
