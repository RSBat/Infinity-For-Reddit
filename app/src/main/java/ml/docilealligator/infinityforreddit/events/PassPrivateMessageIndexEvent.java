package ml.docilealligator.infinityforreddit.events;

public class PassPrivateMessageIndexEvent {
    public final int privateMessageIndex;

    public PassPrivateMessageIndexEvent(int privateMessageIndex) {
        this.privateMessageIndex = privateMessageIndex;
    }
}
