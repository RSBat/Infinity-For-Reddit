package ml.docilealligator.infinityforreddit.events;

public class ChangeFixedHeightPreviewInCardEvent {
    public final boolean fixedHeightPreviewInCard;

    public ChangeFixedHeightPreviewInCardEvent(boolean fixedHeightPreviewInCard) {
        this.fixedHeightPreviewInCard = fixedHeightPreviewInCard;
    }
}
