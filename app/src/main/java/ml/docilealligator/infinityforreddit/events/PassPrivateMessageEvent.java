package ml.docilealligator.infinityforreddit.events;

import ml.docilealligator.infinityforreddit.message.Message;

public class PassPrivateMessageEvent {
    public final Message message;

    public PassPrivateMessageEvent(Message message) {
        this.message = message;
    }
}
