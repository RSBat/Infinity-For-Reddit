package ml.docilealligator.infinityforreddit.events;

public class ChangeAutoplayNsfwVideosEvent {
    public final boolean autoplayNsfwVideos;

    public ChangeAutoplayNsfwVideosEvent(boolean autoplayNsfwVideos) {
        this.autoplayNsfwVideos = autoplayNsfwVideos;
    }
}
