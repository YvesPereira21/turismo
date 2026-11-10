package io.turismo.backend.security;

import io.github.bucket4j.BucketExceptions.BucketExecutionException;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.ConsumptionProbe;
import io.lettuce.core.RedisException;
import io.turismo.backend.dto.ApiError;
import io.turismo.backend.ratelimit.TokenBucketRateLimiter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class PublicRateLimitFilter extends OncePerRequestFilter {

    public static final String REMAINING_HEADER = "X-Rate-Limit-Remaining";
    public static final String RETRY_HEADER = "X-Rate-Limit-Retry-After-Seconds";

    private static final PathPatternRequestMatcher.Builder PATHS = PathPatternRequestMatcher.withDefaults();
    private static final PathPatternRequestMatcher REFRESH = PATHS.matcher(HttpMethod.POST, "/api/v1/auth/refresh");
    private static final PathPatternRequestMatcher TOURIST = PATHS.matcher(HttpMethod.POST, "/api/v1/tourists");
    private static final PathPatternRequestMatcher GUIDE = PATHS.matcher(HttpMethod.POST, "/api/v1/tour-guides");

    private record Policy(String name, BucketConfiguration configuration) {}

    private final TokenBucketRateLimiter rateLimiter;
    private final JsonMapper jsonMapper;
    private final Policy refreshPolicy;
    private final Policy registrationPolicy;

    public PublicRateLimitFilter(TokenBucketRateLimiter rateLimiter, JsonMapper jsonMapper,
                                 long refreshCapacity, long registrationCapacity) {
        this.rateLimiter = rateLimiter;
        this.jsonMapper = jsonMapper;
        this.refreshPolicy = policy("REFRESH", refreshCapacity, Duration.ofMinutes(1));
        this.registrationPolicy = policy("REGISTRATION", registrationCapacity, Duration.ofMinutes(15));
    }

    private static Policy policy(String name, long capacity, Duration refillPeriod) {
        var configuration = BucketConfiguration.builder()
                .addLimit(limit -> limit.capacity(capacity).refillIntervally(capacity, refillPeriod))
                .build();
        return new Policy(name, configuration);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Policy policy = policyFor(request);
        if (policy == null) {
            chain.doFilter(request, response);
            return;
        }

        String key = "turismo:ratelimit:v1:" + policy.name() + ":" + request.getRemoteAddr();
        ConsumptionProbe probe;
        try {
            probe = rateLimiter.consume(key, policy.configuration());
        } catch (DataAccessException | RedisException | BucketExecutionException e) {
            log.warn("Rate limit check unavailable for {}: {}", policy.name(), e.toString());
            writeError(response, HttpStatus.SERVICE_UNAVAILABLE, "Serviço temporariamente indisponível.");
            return;
        }

        response.setHeader(REMAINING_HEADER, Long.toString(probe.getRemainingTokens()));
        if (probe.isConsumed()) {
            chain.doFilter(request, response);
            return;
        }

        long seconds = Math.max(1, Math.ceilDiv(probe.getNanosToWaitForRefill(), 1_000_000_000L));
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(seconds));
        response.setHeader(RETRY_HEADER, Long.toString(seconds));
        writeError(response, HttpStatus.TOO_MANY_REQUESTS,
                "Limite de requisições excedido. Tente novamente em " + seconds + " segundos.");
    }

    private Policy policyFor(HttpServletRequest request) {
        if (REFRESH.matches(request)) {
            return refreshPolicy;
        }
        if (TOURIST.matches(request) || GUIDE.matches(request)) {
            return registrationPolicy;
        }
        return null;
    }

    private void writeError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        jsonMapper.writeValue(response.getOutputStream(),
                new ApiError(LocalDateTime.now(), status.value(), status.name(), List.of(message)));
    }
}
