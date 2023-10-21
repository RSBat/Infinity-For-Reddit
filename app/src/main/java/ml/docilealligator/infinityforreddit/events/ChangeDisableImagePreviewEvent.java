package ml.docilealligator.infinityforreddit.events;

public class ChangeDisableImagePreviewEvent {
    public final boolean disableImagePreview;

    public ChangeDisableImagePreviewEvent(boolean disableImagePreview) {
        this.disableImagePreview = disableImagePreview;
    }
}
