package ml.docilealligator.infinityforreddit.events;

public class ChangeSwipeActionThresholdEvent {
    public final float swipeActionThreshold;

    public ChangeSwipeActionThresholdEvent(float swipeActionThreshold) {
        this.swipeActionThreshold = swipeActionThreshold;
    }
}
