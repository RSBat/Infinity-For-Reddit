package ml.docilealligator.infinityforreddit.markdown.gif;

import androidx.annotation.NonNull;

import org.commonmark.parser.Parser;

import java.util.Collections;
import java.util.List;

import io.noties.markwon.AbstractMarkwonPlugin;
import ml.docilealligator.infinityforreddit.comment.CommentMediaMetadata;
import ml.docilealligator.infinityforreddit.comment.ImageMetadata;

public class CommentMediaPlugin extends AbstractMarkwonPlugin {
    // note: this is ugly, but I'm too lazy to find the correct approach
    @NonNull
    public static List<CommentMediaMetadata> metadata = Collections.emptyList();

    public static CommentMediaPlugin create() {
        return new CommentMediaPlugin();
    }

    private CommentMediaPlugin() {
    }

    @NonNull
    @Override
    public String processMarkdown(@NonNull String markdown) {
        if (metadata.isEmpty()) {
            // short-circuit when we don't need any preprocessing
            return markdown;
        }

        // CommonMark has a parsing optimization that automatically starts Paragraph block
        // if for the first char Character#isLetter == true. This breaks image parsing because
        // the link is always at the beginning of the paragraph and starts with "https"

        // Note: since this doesn't check for code blocks, there should be an edge case where
        // this gives wrong result. Consider this markdown:
        //     url
        //     ```
        //     url
        //     ```
        // The second url is inside code block, so it shouldn't be replace, but it will be by
        // this code. However Reddit's WYSIWYG editor uses spaces for code blocks, so this should
        // be an incredibly rare case.

        // todo: add support for non-Giphy gifs
        StringBuilder processedMardown = new StringBuilder();
        String[] lines = markdown.split("\n");
        for (String line: lines) {
            for (CommentMediaMetadata data: metadata) {
                if (!(data instanceof ImageMetadata)) {
                    processedMardown.append(line).append('\n');
                    continue;
                }

                ImageMetadata imageMetadata = (ImageMetadata) data;
                if (line.equals(imageMetadata.getUrl())) {
                    processedMardown.append("![img](").append(line).append(')').append('\n');
                } else {
                    processedMardown.append(line).append('\n');
                }
            }
        }

        return processedMardown.toString();
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new CommentMediaBlockParser.Factory());
    }
}
