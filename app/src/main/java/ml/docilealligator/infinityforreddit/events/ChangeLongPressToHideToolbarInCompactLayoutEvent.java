package ml.docilealligator.infinityforreddit.events;

public class ChangeLongPressToHideToolbarInCompactLayoutEvent {
    public final boolean longPressToHideToolbarInCompactLayout;

    public ChangeLongPressToHideToolbarInCompactLayoutEvent(boolean longPressToHideToolbarInCompactLayout) {
        this.longPressToHideToolbarInCompactLayout = longPressToHideToolbarInCompactLayout;
    }
}
