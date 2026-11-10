package io.turismo.backend.ratelimit;

import io.github.bucket4j.ConsumptionProbe;
import io.turismo.backend.dto.ApiError;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public final class RateLimitResponses {

    public static final String REMAINING_HEADER = "X-Rate-Limit-Remaining";
    public static final String RETRY_HEADER = "X-Rate-Limit-Retry-After-Seconds";

    private RateLimitResponses() {}

    public static boolean allow(HttpServletResponse response, JsonMapper jsonMapper, ConsumptionProbe probe)
            throws IOException {
        response.setHeader(REMAINING_HEADER, Long.toString(probe.getRemainingTokens()));
        if (probe.isConsumed()) {
            return true;
        }

        long seconds = Math.max(1, Math.ceilDiv(probe.getNanosToWaitForRefill(), 1_000_000_000L));
        response.setHeader(HttpHeaders.RETRY_AFTER, Long.toString(seconds));
        response.setHeader(RETRY_HEADER, Long.toString(seconds));
        writeError(response, jsonMapper, HttpStatus.TOO_MANY_REQUESTS,
                "Limite de requisições excedido. Tente novamente em " + seconds + " segundos.");
        return false;
    }

    public static void unavailable(HttpServletResponse response, JsonMapper jsonMapper) throws IOException {
        writeError(response, jsonMapper, HttpStatus.SERVICE_UNAVAILABLE, "Serviço temporariamente indisponível.");
    }

    private static void writeError(HttpServletResponse response, JsonMapper jsonMapper, HttpStatus status,
                                   String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        jsonMapper.writeValue(response.getOutputStream(),
                new ApiError(LocalDateTime.now(), status.value(), status.name(), List.of(message)));
    }
}
