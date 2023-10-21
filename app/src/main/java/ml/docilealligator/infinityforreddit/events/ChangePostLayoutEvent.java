package ml.docilealligator.infinityforreddit.events;

public class ChangePostLayoutEvent {
    public final int postLayout;

    public ChangePostLayoutEvent(int postLayout) {
        this.postLayout = postLayout;
    }
}
