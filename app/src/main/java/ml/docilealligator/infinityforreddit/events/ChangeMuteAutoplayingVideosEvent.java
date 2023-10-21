package ml.docilealligator.infinityforreddit.events;

public class ChangeMuteAutoplayingVideosEvent {
    public final boolean muteAutoplayingVideos;

    public ChangeMuteAutoplayingVideosEvent(boolean muteAutoplayingVideos) {
        this.muteAutoplayingVideos = muteAutoplayingVideos;
    }
}
