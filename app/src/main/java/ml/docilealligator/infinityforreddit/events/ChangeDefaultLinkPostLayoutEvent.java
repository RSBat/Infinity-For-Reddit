package ml.docilealligator.infinityforreddit.events;

public class ChangeDefaultLinkPostLayoutEvent {
    public final int defaultLinkPostLayout;

    public ChangeDefaultLinkPostLayoutEvent(int defaultLinkPostLayout) {
        this.defaultLinkPostLayout = defaultLinkPostLayout;
    }
}
