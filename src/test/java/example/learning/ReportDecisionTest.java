package example.learning;

import java.net.http.HttpClient;
import java.nio.file.Path;

public final class ReportDecisionTest {
    public static void main(String[] args) throws Exception {
        ModerationQueue queue = new ModerationQueue();
        LearningReportService service = new LearningReportService(new PdfReport(), queue,
                new InfraiEmailClient(HttpClient.newHttpClient(), "test-key"));
        PlayerAsset asset = new PlayerAsset("asset-1", "Number Line", true);
        if (!asset.needsReview()) throw new AssertionError("review decision should be visible");
        queue.add(asset);
        if (queue.waiting().size() != 1) throw new AssertionError("flagged asset should enter moderation");
        PlayerAsset published = new PlayerAsset("asset-2", "Times Tables", false);
        if (published.needsReview()) throw new AssertionError("published asset should stay out of moderation");
        System.out.println("report decision test passed");
    }
}
