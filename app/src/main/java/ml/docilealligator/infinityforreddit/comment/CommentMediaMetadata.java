package ml.docilealligator.infinityforreddit.comment;

import androidx.annotation.NonNull;

public interface CommentMediaMetadata {
    boolean matchesMarkdown(@NonNull String markdown);
}
