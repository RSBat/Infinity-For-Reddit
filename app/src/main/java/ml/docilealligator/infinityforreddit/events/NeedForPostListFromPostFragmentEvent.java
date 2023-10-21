package ml.docilealligator.infinityforreddit.events;

public class NeedForPostListFromPostFragmentEvent {
    public final long postFragmentTimeId;

    public NeedForPostListFromPostFragmentEvent(long postFragmentId) {
        this.postFragmentTimeId = postFragmentId;
    }
}
