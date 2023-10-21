package ml.docilealligator.infinityforreddit.events;

public class ChangeNSFWEvent {
    public final boolean nsfw;

    public ChangeNSFWEvent(boolean nsfw) {
        this.nsfw = nsfw;
    }
}
