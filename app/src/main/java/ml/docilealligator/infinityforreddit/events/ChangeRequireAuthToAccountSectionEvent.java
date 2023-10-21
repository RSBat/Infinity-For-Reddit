package ml.docilealligator.infinityforreddit.events;

public class ChangeRequireAuthToAccountSectionEvent {
    public final boolean requireAuthToAccountSection;

    public ChangeRequireAuthToAccountSectionEvent(boolean requireAuthToAccountSection) {
        this.requireAuthToAccountSection = requireAuthToAccountSection;
    }
}
