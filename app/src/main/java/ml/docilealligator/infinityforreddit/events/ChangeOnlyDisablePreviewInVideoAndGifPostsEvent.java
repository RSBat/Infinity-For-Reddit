package ml.docilealligator.infinityforreddit.events;

public class ChangeOnlyDisablePreviewInVideoAndGifPostsEvent {
    public final boolean onlyDisablePreviewInVideoAndGifPosts;

    public ChangeOnlyDisablePreviewInVideoAndGifPostsEvent(boolean onlyDisablePreviewInVideoAndGifPosts) {
        this.onlyDisablePreviewInVideoAndGifPosts = onlyDisablePreviewInVideoAndGifPosts;
    }
}
