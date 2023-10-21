package ml.docilealligator.infinityforreddit.events;

public class ChangeHideTheNumberOfCommentsEvent {
    public final boolean hideTheNumberOfComments;

    public ChangeHideTheNumberOfCommentsEvent(boolean hideTheNumberOfComments) {
        this.hideTheNumberOfComments = hideTheNumberOfComments;
    }
}
