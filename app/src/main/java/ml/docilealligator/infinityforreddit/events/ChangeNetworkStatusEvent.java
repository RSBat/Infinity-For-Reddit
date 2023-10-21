package ml.docilealligator.infinityforreddit.events;

public class ChangeNetworkStatusEvent {
    public final int connectedNetwork;

    public ChangeNetworkStatusEvent(int connectedNetwork) {
        this.connectedNetwork = connectedNetwork;
    }
}
