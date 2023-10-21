package ml.docilealligator.infinityforreddit.events;

public class SubmitGalleryPostEvent {
    public final boolean postSuccess;
    public final String postUrl;
    public final String errorMessage;

    public SubmitGalleryPostEvent(boolean postSuccess, String postUrl, String errorMessage) {
        this.postSuccess = postSuccess;
        this.postUrl = postUrl;
        this.errorMessage = errorMessage;
    }
}
