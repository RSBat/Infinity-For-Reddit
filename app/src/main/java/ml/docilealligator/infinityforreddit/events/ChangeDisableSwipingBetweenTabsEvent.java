package ml.docilealligator.infinityforreddit.events;

public class ChangeDisableSwipingBetweenTabsEvent {
    public final boolean disableSwipingBetweenTabs;

    public ChangeDisableSwipingBetweenTabsEvent(boolean disableSwipingBetweenTabs) {
        this.disableSwipingBetweenTabs = disableSwipingBetweenTabs;
    }
}
