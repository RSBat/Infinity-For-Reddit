package ml.docilealligator.infinityforreddit.events;

public class ToggleSecureModeEvent {
    public final boolean isSecureMode;

    public ToggleSecureModeEvent(boolean isSecureMode) {
        this.isSecureMode = isSecureMode;
    }
}
