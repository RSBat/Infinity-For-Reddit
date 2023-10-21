package ml.docilealligator.infinityforreddit.events;

public class ChangeHideFabInPostFeedEvent {
    public final boolean hideFabInPostFeed;

    public ChangeHideFabInPostFeedEvent(boolean hideFabInPostFeed) {
        this.hideFabInPostFeed = hideFabInPostFeed;
    }
}
