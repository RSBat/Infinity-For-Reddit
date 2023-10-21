package ml.docilealligator.infinityforreddit.events;

public class ChangeHideTheNumberOfVotesEvent {
    public final boolean hideTheNumberOfVotes;

    public ChangeHideTheNumberOfVotesEvent(boolean hideTheNumberOfVotes) {
        this.hideTheNumberOfVotes = hideTheNumberOfVotes;
    }
}
