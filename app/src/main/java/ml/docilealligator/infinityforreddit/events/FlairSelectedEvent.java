package ml.docilealligator.infinityforreddit.events;

import ml.docilealligator.infinityforreddit.Flair;

public class FlairSelectedEvent {
    public final long viewPostDetailFragmentId;
    public final Flair flair;

    public FlairSelectedEvent(long viewPostDetailFragmentId, Flair flair) {
        this.viewPostDetailFragmentId = viewPostDetailFragmentId;
        this.flair = flair;
    }
}
