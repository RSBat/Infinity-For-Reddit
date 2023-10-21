package ml.docilealligator.infinityforreddit.events;

public class ChangePostFeedMaxResolutionEvent {
    public final int postFeedMaxResolution;

    public ChangePostFeedMaxResolutionEvent(int postFeedMaxResolution) {
        this.postFeedMaxResolution = postFeedMaxResolution;
    }
}
