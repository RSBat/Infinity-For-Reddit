package ml.docilealligator.infinityforreddit.events;

public class ChangeTimeFormatEvent {
    public final String timeFormat;

    public ChangeTimeFormatEvent(String timeFormat) {
        this.timeFormat = timeFormat;
    }
}
