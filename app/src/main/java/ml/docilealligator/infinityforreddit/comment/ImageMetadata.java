package ml.docilealligator.infinityforreddit.comment;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ImageMetadata implements CommentMediaMetadata, Parcelable {

    private final String id;
    public final int x;
    public final int y;
    private final String url;

    public ImageMetadata(String id, int x, int y, String url) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.url = url;
    }

    protected ImageMetadata(Parcel in) {
        id = in.readString();
        x = in.readInt();
        y = in.readInt();
        url = in.readString();
    }

    @Override
    public boolean matchesMarkdown(@NonNull String markdown) {
        return markdown.equals("![img](" + url + ")");
    }

    public String getUrl() {
        return url;
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
        dest.writeString(url);
    }

    public static final Creator<ImageMetadata> CREATOR = new Creator<>() {
        @Override
        public ImageMetadata createFromParcel(Parcel in) {
            return new ImageMetadata(in);
        }

        @Override
        public ImageMetadata[] newArray(int size) {
            return new ImageMetadata[size];
        }
    };
}
