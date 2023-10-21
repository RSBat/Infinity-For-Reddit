package ml.docilealligator.infinityforreddit.events;

public class ChangeAppLockEvent {
    public final boolean appLock;
    public final long appLockTimeout;

    public ChangeAppLockEvent(boolean appLock, long appLockTimeout) {
        this.appLock = appLock;
        this.appLockTimeout = appLockTimeout;
    }
}
