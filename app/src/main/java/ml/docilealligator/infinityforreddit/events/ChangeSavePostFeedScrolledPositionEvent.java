package ml.docilealligator.infinityforreddit.events;

public class ChangeSavePostFeedScrolledPositionEvent {
    public final boolean savePostFeedScrolledPosition;

    public ChangeSavePostFeedScrolledPositionEvent(boolean savePostFeedScrolledPosition) {
        this.savePostFeedScrolledPosition = savePostFeedScrolledPosition;
    }
}
