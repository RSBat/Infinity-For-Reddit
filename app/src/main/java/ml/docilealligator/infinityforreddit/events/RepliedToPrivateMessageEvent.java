package ml.docilealligator.infinityforreddit.events;

import ml.docilealligator.infinityforreddit.message.Message;

public class RepliedToPrivateMessageEvent {
    public final Message newReply;
    public final int messagePosition;

    public RepliedToPrivateMessageEvent(Message newReply, int messagePosition) {
        this.newReply = newReply;
        this.messagePosition = messagePosition;
    }
}
