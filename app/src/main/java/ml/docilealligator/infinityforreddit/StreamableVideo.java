package ml.docilealligator.infinityforreddit;

import androidx.annotation.Nullable;

public class StreamableVideo {
    public final String title;
    @Nullable
    public final Media mp4;
    @Nullable
    public final Media mp4Mobile;

    public StreamableVideo(String title, @Nullable Media mp4, @Nullable Media mp4Mobile) {
        this.title = title;
        this.mp4 = mp4;
        this.mp4Mobile = mp4Mobile;
    }

    public static class Media {
        public final String url;
        public final int width;
        public final int height;

        public Media(String url, int width, int height) {
            this.url = url;
            this.width = width;
            this.height = height;
        }
    }
}
