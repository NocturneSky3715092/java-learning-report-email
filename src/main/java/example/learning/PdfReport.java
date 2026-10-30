package example.learning;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PdfReport {
    public Path write(Path target, LiveEvent event, PlayerAsset asset) throws IOException {
        String text = "Learning report\nLesson: " + event.lesson() + "\nScore: " + event.score()
                + "\nAsset: " + asset.title() + "\n";
        String pdf = "%PDF-1.4\n1 0 obj\n<< /Type /Catalog >>\nendobj\n% " + text;
        return Files.writeString(target, pdf, StandardCharsets.ISO_8859_1);
    }

    public String summary(LiveEvent event, PlayerAsset asset) {
        return "Lesson " + event.lesson() + " scored " + event.score() + "; asset " + asset.title() + ".";
    }
}
