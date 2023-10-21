package ml.docilealligator.infinityforreddit.events;

public class ChangeMuteNSFWVideoEvent {
    public final boolean muteNSFWVideo;

    public ChangeMuteNSFWVideoEvent(boolean muteNSFWVideo) {
        this.muteNSFWVideo = muteNSFWVideo;
    }
}
