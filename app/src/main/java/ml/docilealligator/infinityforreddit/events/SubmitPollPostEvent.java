package ml.docilealligator.infinityforreddit.events;

public class SubmitPollPostEvent {
    public final boolean postSuccess;
    public final String postUrl;
    public final String errorMessage;

    public SubmitPollPostEvent(boolean postSuccess, String postUrl, String errorMessage) {
        this.postSuccess = postSuccess;
        this.postUrl = postUrl;
        this.errorMessage = errorMessage;
    }
}
