package ml.docilealligator.infinityforreddit.markdown.gif;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.commonmark.parser.Parser;

import io.noties.markwon.AbstractMarkwonPlugin;

public class GifPlugin extends AbstractMarkwonPlugin {
    @Nullable
    public static GiphyGif currentGif = null;

    public static GifPlugin create() {
        return new GifPlugin();
    }

    private GifPlugin() {
    }

    @Override
    public void configureParser(@NonNull Parser.Builder builder) {
        builder.customBlockParserFactory(new GifBlockParser.Factory());
    }
}
