package ml.docilealligator.infinityforreddit.markdown.gif;

import org.commonmark.node.CustomBlock;

public class GifBlock extends CustomBlock {
    public final GiphyGif gif;

    public GifBlock(GiphyGif gif) {
        this.gif = gif;
    }
}
