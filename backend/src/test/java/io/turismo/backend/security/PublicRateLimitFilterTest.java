package io.turismo.backend.security;

import io.github.bucket4j.Bucket;
import io.lettuce.core.RedisConnectionException;
import io.github.bucket4j.BucketConfiguration;
import io.turismo.backend.ratelimit.TokenBucketRateLimiter;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class PublicRateLimitFilterTest {

    private static final String IP = "192.0.2.1";
    private static final String LOGIN = "/api/v1/auth/login";
    private static final String REFRESH = "/api/v1/auth/refresh";
    private static final String TOURISTS = "/api/v1/tourists";
    private static final String GUIDES = "/api/v1/tour-guides";

    private final JsonMapper json = JsonMapper.builder().build();
    private final TokenBucketRateLimiter service = mock(TokenBucketRateLimiter.class);
    private final PublicRateLimitFilter filter = new PublicRateLimitFilter(service, json, 1, 1);

    @BeforeEach
    void setUp() {
        var buckets = new HashMap<String, Bucket>();
        when(service.consume(any(), any())).thenAnswer(call -> {
            String key = call.getArgument(0);
            BucketConfiguration configuration = call.getArgument(1);
            return buckets.computeIfAbsent(key, ignored -> Bucket.builder()
                    .addLimit(configuration.getBandwidths()[0])
                    .build()).tryConsumeAndReturnRemaining(1);
        });
    }

    @Test
    void refreshQuotaIsIsolatedByIp() throws Exception {
        assertThat(request("POST", REFRESH, IP).getStatus()).isEqualTo(204);
        var blocked = request("POST", REFRESH, IP);
        assertThat(blocked.getStatus()).isEqualTo(429);
        assertThat(blocked.getHeader("Retry-After")).isEqualTo(blocked.getHeader(PublicRateLimitFilter.RETRY_HEADER));
        assertThat(Long.parseLong(blocked.getHeader("Retry-After"))).isBetween(1L, 60L);
        assertThat(blocked.getHeader(PublicRateLimitFilter.REMAINING_HEADER)).isEqualTo("0");
        assertThat(json.readTree(blocked.getContentAsByteArray()).get("code").asInt()).isEqualTo(429);
        assertThat(request("POST", REFRESH, "192.0.2.2").getStatus()).isEqualTo(204);
    }

    @Test
    void registrationRoutesShareAQuota() throws Exception {
        assertThat(request("POST", TOURISTS, IP).getStatus()).isEqualTo(204);
        assertThat(request("POST", GUIDES, IP).getStatus()).isEqualTo(429);
    }

    @Test
    void otherRoutesAndMethodsDoNotUseTheLimiter() throws Exception {
        assertThat(request("GET", TOURISTS, IP).getStatus()).isEqualTo(204);
        assertThat(request("POST", LOGIN, IP).getStatus()).isEqualTo(204);
        assertThat(request("POST", "/api/v1/auth/logout", IP).getStatus()).isEqualTo(204);
        verifyNoInteractions(service);
    }

    @Test
    void redisFailureReturnsServiceUnavailable() throws Exception {
        doThrow(new RedisConnectionException("unavailable"))
                .when(service).consume(eq("turismo:ratelimit:v1:REFRESH:" + IP), any());
        var response = request("POST", REFRESH, IP);
        assertThat(response.getStatus()).isEqualTo(503);
        assertThat(response.getHeader("Retry-After")).isNull();
        assertThat(json.readTree(response.getContentAsByteArray()).get("code").asInt()).isEqualTo(503);
    }

    private MockHttpServletResponse request(String method, String path, String ip) throws ServletException, IOException {
        var request = new MockHttpServletRequest(method, path);
        request.setRemoteAddr(ip);
        request.addHeader("X-Forwarded-For", "198.51.100.1");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> ((MockHttpServletResponse) res).setStatus(204));
        return response;
    }
}
