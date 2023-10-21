package ml.docilealligator.infinityforreddit.events;

public class ChangeSwipeActionEvent {
    public final int swipeLeftAction;
    public final int swipeRightAction;

    public ChangeSwipeActionEvent(int swipeLeftAction, int swipeRightAction) {
        this.swipeLeftAction = swipeLeftAction;
        this.swipeRightAction = swipeRightAction;
    }
}
