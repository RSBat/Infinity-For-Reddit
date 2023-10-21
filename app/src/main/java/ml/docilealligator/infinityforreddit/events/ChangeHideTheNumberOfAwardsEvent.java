package ml.docilealligator.infinityforreddit.events;

public class ChangeHideTheNumberOfAwardsEvent {
    public final boolean hideTheNumberOfAwards;

    public ChangeHideTheNumberOfAwardsEvent(boolean hideTheNumberOfAwards) {
        this.hideTheNumberOfAwards = hideTheNumberOfAwards;
    }
}
