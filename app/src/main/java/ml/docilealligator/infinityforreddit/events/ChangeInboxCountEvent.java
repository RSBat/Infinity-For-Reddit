package ml.docilealligator.infinityforreddit.events;

public class ChangeInboxCountEvent {
    public final int inboxCount;

    public ChangeInboxCountEvent(int inboxCount) {
        this.inboxCount = inboxCount;
    }
}
