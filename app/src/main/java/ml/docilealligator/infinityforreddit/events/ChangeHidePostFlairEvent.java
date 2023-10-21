package ml.docilealligator.infinityforreddit.events;

public class ChangeHidePostFlairEvent {
    public final boolean hidePostFlair;

    public ChangeHidePostFlairEvent(boolean hidePostFlair) {
        this.hidePostFlair = hidePostFlair;
    }
}
