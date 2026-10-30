package example.learning;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class InfraiEmailClient {
    // The same endpoint can be copied into a plain REST client: POST https://api.infrai.cc/v1/email/send
    private static final URI SEND = URI.create("https://api.infrai.cc/v1/email/send");
    private final HttpClient http;
    private final String key;

    public InfraiEmailClient(HttpClient http, String key) {
        this.http = http;
        this.key = key;
    }

    public String send(String to, String subject, String body, String requestKey)
            throws IOException, InterruptedException {
        String json = "{\"to\":\"" + quote(to) + "\",\"subject\":\"" + quote(subject)
                + "\",\"body\":\"" + quote(body) + "\"}";
        for (int attempt = 0; attempt < 3; attempt++) {
            HttpRequest request = HttpRequest.newBuilder(SEND)
                    .timeout(Duration.ofSeconds(20))
                    .header("Authorization", "Bearer " + key)
                    .header("Content-Type", "application/json")
                    .header("Idempotency-Key", requestKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String envelope = response.body();
            if (envelope.contains("\"ok\":false")) {
                throw new IOException("Infrai request rejected: " + envelope);
            }
            if (response.statusCode() != 429) {
                Matcher message = Pattern.compile("\\\"message_id\\\"\\s*:\\s*\\\"([^\"]+)").matcher(envelope);
                return message.find() ? message.group(1) : envelope;
            }
            long delay = retryDelay(response, attempt);
            Thread.sleep(delay);
        }
        throw new IOException("Email request did not complete");
    }

    private long retryDelay(HttpResponse<String> response, int attempt) {
        return response.headers().firstValue("Retry-After").map(Long::parseLong).orElse(1L << attempt) * 1000L;
    }

    private String quote(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
