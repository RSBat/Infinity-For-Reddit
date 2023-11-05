package ml.docilealligator.infinityforreddit.comment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class GiphyGifMetadata implements CommentMediaMetadata, Parcelable {
    private final String id;
    public final int x;
    public final int y;

    public GiphyGifMetadata(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    private GiphyGifMetadata(Parcel in) {
        id = in.readString();
        x = in.readInt();
        y = in.readInt();
    }

    @Override
    public boolean matchesMarkdown(@NonNull String markdown) {
        return markdown.equals("![gif](giphy|" + id + ")");
    }

    public String getGifUrl() {
        return "https://i.giphy.com/media/" + id.replace("|", "/") + "/giphy.gif";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(x);
        dest.writeInt(y);
    }

    public static final Creator<GiphyGifMetadata> CREATOR = new Creator<>() {
        @Override
        public GiphyGifMetadata createFromParcel(Parcel in) {
            return new GiphyGifMetadata(in);
        }

        @Override
        public GiphyGifMetadata[] newArray(int size) {
            return new GiphyGifMetadata[size];
        }
    };
}
