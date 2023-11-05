package ml.docilealligator.infinityforreddit.markdown.gif;

import org.commonmark.node.Block;
import org.commonmark.parser.block.AbstractBlockParser;
import org.commonmark.parser.block.AbstractBlockParserFactory;
import org.commonmark.parser.block.BlockContinue;
import org.commonmark.parser.block.BlockStart;
import org.commonmark.parser.block.MatchedBlockParser;
import org.commonmark.parser.block.ParserState;

import ml.docilealligator.infinityforreddit.comment.GiphyGifMetadata;
import ml.docilealligator.infinityforreddit.comment.CommentMediaMetadata;
import ml.docilealligator.infinityforreddit.comment.ImageMetadata;

public class CommentMediaBlockParser extends AbstractBlockParser {
    private final CommentMediaBlock block;

    private CommentMediaBlockParser(CommentMediaMetadata gif) {
        block = new CommentMediaBlock(gif);
    }

    @Override
    public Block getBlock() {
        return block;
    }

    @Override
    public BlockContinue tryContinue(ParserState parserState) {
        // gif/image is always one line
        return null;
    }

    public static class Factory extends AbstractBlockParserFactory {
        public BlockStart tryStart(ParserState state, MatchedBlockParser matchedBlockParser) {
            String line = state.getLine().toString();

            for (CommentMediaMetadata metadata: CommentMediaPlugin.metadata) {
                if (metadata.matchesMarkdown(line)) {
                    return BlockStart.of(new CommentMediaBlockParser(metadata));
                }
            }
            return BlockStart.none();
        }
    }
}
