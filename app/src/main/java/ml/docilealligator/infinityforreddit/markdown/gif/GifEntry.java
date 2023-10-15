package ml.docilealligator.infinityforreddit.markdown.gif;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;

import io.noties.markwon.Markwon;
import io.noties.markwon.recycler.MarkwonAdapter;
import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customtheme.CustomThemeWrapper;
import ml.docilealligator.infinityforreddit.databinding.AdapterGifEntryBinding;

public class GifEntry extends MarkwonAdapter.Entry<GifBlock, GifEntry.Holder> {
    private final RequestManager glide;
    private final CustomThemeWrapper customThemeWrapper;

    public GifEntry(RequestManager glide, CustomThemeWrapper customThemeWrapper1) {
        this.glide = glide;
        this.customThemeWrapper = customThemeWrapper1;
    }

    @NonNull
    @Override
    public GifEntry.Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(AdapterGifEntryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull GifEntry.Holder holder, @NonNull GifBlock node) {
        holder.bind(node.gif);
    }

    @Override
    public void onViewRecycled(@NonNull Holder holder) {
        holder.recycle();
    }

    public class Holder extends MarkwonAdapter.Holder {
        private final AdapterGifEntryBinding binding;
        private final RequestListener<Drawable> requestListener = new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                binding.gifProgressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                binding.gifProgressBar.setVisibility(View.GONE);
                return false;
            }
        };

        public Holder(@NonNull AdapterGifEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.gifProgressBar.setIndeterminateTintList(ColorStateList.valueOf(customThemeWrapper.getColorAccent()));
            binding.giphyWatermark.setTextColor(customThemeWrapper.getCommentColor());
        }

        void bind(GiphyGif gif) {
            binding.gifProgressBar.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = binding.ivGif.getLayoutParams();
            if (gif.x > gif.y) {
                params.height = dpToPx(160);
                params.width = params.height * gif.x / gif.y;
            } else {
                params.width = dpToPx(160);
                params.height = params.width * gif.y / gif.x;
            }
            binding.ivGif.setLayoutParams(params);

            Target<Drawable> target = new DrawableImageViewTarget(binding.ivGif)
                    .waitForLayout();
            glide.load(gif.getGifUrl())
                    .addListener(requestListener)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(target);
        }

        void recycle() {
            glide.clear(binding.ivGif);
        }

        private int dpToPx(int dp) {
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            return (int) (dp * density);
        }
    }
}
