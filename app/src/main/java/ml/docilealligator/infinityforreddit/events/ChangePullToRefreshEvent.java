package ml.docilealligator.infinityforreddit.events;

public class ChangePullToRefreshEvent {
    public final boolean pullToRefresh;

    public ChangePullToRefreshEvent(boolean pullToRefresh) {
        this.pullToRefresh = pullToRefresh;
    }
}
