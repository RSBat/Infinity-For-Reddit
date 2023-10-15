package ml.docilealligator.infinityforreddit.markdown.gif;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class GiphyGif implements Parcelable {
    private final String id;
    public final int x;
    public final int y;

    public GiphyGif(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    private GiphyGif(Parcel in) {
        id = in.readString();
        x = in.readInt();
        y = in.readInt();
    }

    public boolean matchMarkdown(String line) {
        return line.equals("![gif](giphy|" + id + ")");
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
    }

    public static final Creator<GiphyGif> CREATOR = new Creator<>() {
        @Override
        public GiphyGif createFromParcel(Parcel in) {
            return new GiphyGif(in);
        }

        @Override
        public GiphyGif[] newArray(int size) {
            return new GiphyGif[size];
        }
    };
}
