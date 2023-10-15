package ml.docilealligator.infinityforreddit.markdown.gif;

import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

public class GifBlockParser extends AbstractBlockParser {
    private final GifBlock block;

    private GifBlockParser(GiphyGif gif) {
        block = new GifBlock(gif);
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {
        // gif is always one line
        return null;
    }

    public static class Factory extends AbstractBlockParserFactory {
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            String line = state.getLine().toString();

            if (GifPlugin.currentGif != null && GifPlugin.currentGif.matchMarkdown(line)) {
                return BlockStart.of(new GifBlockParser(GifPlugin.currentGif));
            } else {
                return BlockStart.none();
            }
        }
    }
}
