package ml.docilealligator.infinityforreddit.events;

public class ChangeHidePostTypeEvent {
    public final boolean hidePostType;

    public ChangeHidePostTypeEvent(boolean hidePostType) {
        this.hidePostType = hidePostType;
    }
}
