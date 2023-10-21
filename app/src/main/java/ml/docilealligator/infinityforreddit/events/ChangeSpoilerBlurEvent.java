package ml.docilealligator.infinityforreddit.events;

public class ChangeSpoilerBlurEvent {
    public final boolean needBlurSpoiler;

    public ChangeSpoilerBlurEvent(boolean needBlurSpoiler) {
        this.needBlurSpoiler = needBlurSpoiler;
    }
}
