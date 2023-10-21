package ml.docilealligator.infinityforreddit.events;

public class ChangeVoteButtonsPositionEvent {
    public final boolean voteButtonsOnTheRight;

    public ChangeVoteButtonsPositionEvent(boolean voteButtonsOnTheRight) {
        this.voteButtonsOnTheRight = voteButtonsOnTheRight;
    }
}
