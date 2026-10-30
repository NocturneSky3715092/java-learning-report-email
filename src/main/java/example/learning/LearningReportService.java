package example.learning;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public final class LearningReportService {
    private final PdfReport pdfReport;
    private final ModerationQueue moderationQueue;
    private final InfraiEmailClient emailClient;

    public LearningReportService(PdfReport pdfReport, ModerationQueue moderationQueue, InfraiEmailClient emailClient) {
        this.pdfReport = pdfReport;
        this.moderationQueue = moderationQueue;
        this.emailClient = emailClient;
    }

    public String complete(String email, LiveEvent event, PlayerAsset asset, Path reportPath)
            throws IOException, InterruptedException {
        if (asset.needsReview()) {
            moderationQueue.add(asset);
        }
        pdfReport.write(reportPath, event, asset);
        return emailClient.send(email, "Learning report: " + event.lesson(),
                pdfReport.summary(event, asset) + " PDF report: " + reportPath.getFileName(),
                "learning-report-" + UUID.randomUUID());
    }
}
