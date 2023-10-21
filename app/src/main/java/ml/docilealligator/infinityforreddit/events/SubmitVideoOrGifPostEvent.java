package ml.docilealligator.infinityforreddit.events;

public class SubmitVideoOrGifPostEvent {
    public final boolean postSuccess;
    public final boolean errorProcessingVideoOrGif;
    public final String errorMessage;

    public SubmitVideoOrGifPostEvent(boolean postSuccess, boolean errorProcessingVideoOrGif, String errorMessage) {
        this.postSuccess = postSuccess;
        this.errorProcessingVideoOrGif = errorProcessingVideoOrGif;
        this.errorMessage = errorMessage;
    }
}
