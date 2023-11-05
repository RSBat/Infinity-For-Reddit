package ml.docilealligator.infinityforreddit.markdown.gif;

import org.commonmark.node.CustomBlock;

import ml.docilealligator.infinityforreddit.comment.CommentMediaMetadata;
import ml.docilealligator.infinityforreddit.comment.GiphyGifMetadata;

public class CommentMediaBlock extends CustomBlock {
    public final CommentMediaMetadata metadata;

    public CommentMediaBlock(CommentMediaMetadata metadata) {
        this.metadata = metadata;
    }
}
