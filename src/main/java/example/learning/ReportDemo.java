package example.learning;

import java.net.http.HttpClient;
import java.nio.file.Path;

public final class ReportDemo {
    public static void main(String[] args) throws Exception {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) throw new IllegalStateException("INFRAI_API_KEY is required");
        String to = System.getenv().getOrDefault("DEMO_EMAIL_TO", "course-operations@example.com");
        LearningReportService service = new LearningReportService(new PdfReport(), new ModerationQueue(),
                new InfraiEmailClient(HttpClient.newHttpClient(), key));
        String messageId = service.complete(to, new LiveEvent("event-17", "Fractions", 92),
                new PlayerAsset("asset-8", "Fraction Quest", false), Path.of("progress-report.pdf"));
        System.out.println("sent learning report: " + messageId);
    }
}
