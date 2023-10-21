package ml.docilealligator.infinityforreddit.events;

public class ChangeShowAbsoluteNumberOfVotesEvent {
    public final boolean showAbsoluteNumberOfVotes;

    public ChangeShowAbsoluteNumberOfVotesEvent(boolean showAbsoluteNumberOfVotes) {
        this.showAbsoluteNumberOfVotes = showAbsoluteNumberOfVotes;
    }
}
