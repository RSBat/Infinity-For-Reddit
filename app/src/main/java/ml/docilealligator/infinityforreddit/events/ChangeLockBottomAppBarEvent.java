package ml.docilealligator.infinityforreddit.events;

public class ChangeLockBottomAppBarEvent {
    public final boolean lockBottomAppBar;

    public ChangeLockBottomAppBarEvent(boolean lockBottomAppBar) {
        this.lockBottomAppBar = lockBottomAppBar;
    }
}
