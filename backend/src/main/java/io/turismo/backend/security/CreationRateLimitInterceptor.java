package io.turismo.backend.security;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.BucketExceptions.BucketExecutionException;
import io.github.bucket4j.ConsumptionProbe;
import io.lettuce.core.RedisException;
import io.turismo.backend.model.User;
import io.turismo.backend.ratelimit.RateLimitResponses;
import io.turismo.backend.ratelimit.TokenBucketRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Slf4j
public class CreationRateLimitInterceptor extends OncePerRequestFilter {

    private static final PathPatternRequestMatcher.Builder PATHS = PathPatternRequestMatcher.withDefaults();
    private static final String TOURIST_SPOT_PHOTOS = "/api/v1/tourist-spots/{touristSpotId}/photos";
    private static final String ACTIVITY_PHOTOS = "/api/v1/activities/{activityId}/photos";
    private static final OrRequestMatcher UPLOADS = new OrRequestMatcher(
            PATHS.matcher(HttpMethod.POST, TOURIST_SPOT_PHOTOS),
            PATHS.matcher(HttpMethod.POST, ACTIVITY_PHOTOS),
            PATHS.matcher(HttpMethod.PUT, ACTIVITY_PHOTOS));

    private static final OrRequestMatcher CREATIONS = new OrRequestMatcher(
            PATHS.matcher(HttpMethod.POST, "/api/v1/warns/spot/{touristSpotId}"),
            PATHS.matcher(HttpMethod.POST, "/api/v1/tourist-spots"),
            PATHS.matcher(HttpMethod.POST, "/api/v1/tourist-spots/{touristSpotId}/activities"));

    private static final RequestMatcher SPOTS_TO_MAP = PATHS.matcher(HttpMethod.GET, "/api/v1/spots-to-map/");

    private record Policy(String action, RequestMatcher matcher, BucketConfiguration configuration,
                          boolean requiresSpotManager) {}

    private final TokenBucketRateLimiter rateLimiter;
    private final JsonMapper jsonMapper;
    private final List<Policy> policies;

    public CreationRateLimitInterceptor(
            TokenBucketRateLimiter rateLimiter,
            JsonMapper jsonMapper,
            long uploadCapacity,
            long creationCapacity,
            long mapCapacity
    ) {
        this.rateLimiter = rateLimiter;
        this.jsonMapper = jsonMapper;
        this.policies = List.of(
                policy("PHOTO_UPLOAD", UPLOADS, uploadCapacity, true),
                policy("CREATION", CREATIONS, creationCapacity, true),
                policy("SPOTS_TO_MAP", SPOTS_TO_MAP, mapCapacity, false));
    }

    private static Policy policy(String action, RequestMatcher matcher, long capacity,
                                 boolean requiresSpotManager) {
        var configuration = BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(capacity).refillIntervally(capacity, Duration.ofMinutes(1)))
                .build();
        return new Policy(action, matcher, configuration, requiresSpotManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        var policy = policies.stream().filter(rule -> rule.matcher().matches(request)).findFirst().orElse(null);
        if (policy == null) {
            chain.doFilter(request, response);
            return;
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof User user)
                || (policy.requiresSpotManager() && auth.getAuthorities().stream()
                        .noneMatch(role -> "ROLE_SPOTMANAGER".equals(role.getAuthority())))) {
            chain.doFilter(request, response);
            return;
        }

        String key = "turismo:ratelimit:v1:" + policy.action() + ":user:" + user.getId();
        ConsumptionProbe probe;
        try {
            probe = rateLimiter.consume(key, policy.configuration());
        } catch (DataAccessException | RedisException | BucketExecutionException e) {
            log.warn("Rate limit check unavailable for {}: {}", policy.action(), e.toString());
            RateLimitResponses.unavailable(response, jsonMapper);
            return;
        }

        if (RateLimitResponses.allow(response, jsonMapper, probe)) {
            chain.doFilter(request, response);
        }
    }
}
