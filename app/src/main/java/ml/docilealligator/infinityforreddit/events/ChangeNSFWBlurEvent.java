package ml.docilealligator.infinityforreddit.events;

public class ChangeNSFWBlurEvent {
    public final boolean needBlurNSFW;
    public final boolean doNotBlurNsfwInNsfwSubreddits;

    public ChangeNSFWBlurEvent(boolean needBlurNSFW, boolean doNotBlurNsfwInNsfwSubreddits) {
        this.needBlurNSFW = needBlurNSFW;
        this.doNotBlurNsfwInNsfwSubreddits = doNotBlurNsfwInNsfwSubreddits;
    }
}
