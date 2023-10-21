package ml.docilealligator.infinityforreddit.events;

public class ChangeEnableSwipeActionSwitchEvent {
    public final boolean enableSwipeAction;

    public ChangeEnableSwipeActionSwitchEvent(boolean enableSwipeAction) {
        this.enableSwipeAction = enableSwipeAction;
    }
}
