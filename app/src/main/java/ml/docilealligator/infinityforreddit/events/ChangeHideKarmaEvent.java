package ml.docilealligator.infinityforreddit.events;

public class ChangeHideKarmaEvent {
    public final boolean hideKarma;

    public ChangeHideKarmaEvent(boolean showKarma) {
        this.hideKarma = showKarma;
    }
}
