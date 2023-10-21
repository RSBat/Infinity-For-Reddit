package ml.docilealligator.infinityforreddit.events;

public class ChangeVideoAutoplayEvent {
    public final String autoplay;

    public ChangeVideoAutoplayEvent(String autoplay) {
        this.autoplay = autoplay;
    }
}
