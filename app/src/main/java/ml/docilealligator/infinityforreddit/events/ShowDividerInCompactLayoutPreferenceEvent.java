package ml.docilealligator.infinityforreddit.events;

public class ShowDividerInCompactLayoutPreferenceEvent {
    public final boolean showDividerInCompactLayout;

    public ShowDividerInCompactLayoutPreferenceEvent(boolean showDividerInCompactLayout) {
        this.showDividerInCompactLayout = showDividerInCompactLayout;
    }
}
