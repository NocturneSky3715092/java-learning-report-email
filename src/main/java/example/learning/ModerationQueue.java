package example.learning;

import java.util.ArrayList;
import java.util.List;

public final class ModerationQueue {
    private final List<PlayerAsset> waiting = new ArrayList<>();

    public void add(PlayerAsset asset) {
        waiting.add(asset);
    }

    public List<PlayerAsset> waiting() {
        return List.copyOf(waiting);
    }
}
