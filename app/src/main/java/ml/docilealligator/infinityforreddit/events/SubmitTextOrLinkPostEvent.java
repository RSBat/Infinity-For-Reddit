package ml.docilealligator.infinityforreddit.events;

import ml.docilealligator.infinityforreddit.post.Post;

public class SubmitTextOrLinkPostEvent {
    public final boolean postSuccess;
    public final Post post;
    public final String errorMessage;

    public SubmitTextOrLinkPostEvent(boolean postSuccess, Post post, String errorMessage) {
        this.postSuccess = postSuccess;
        this.post = post;
        this.errorMessage = errorMessage;
    }
}
