package ml.docilealligator.infinityforreddit.markdown.gif;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;

import io.noties.markwon.Markwon;
import io.noties.markwon.recycler.MarkwonAdapter;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.comment.GiphyGifMetadata;
import ml.docilealligator.infinityforreddit.comment.ImageMetadata;
import ml.docilealligator.infinityforreddit.customtheme.CustomThemeWrapper;
import ml.docilealligator.infinityforreddit.databinding.AdapterGifEntryBinding;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;
import ml.docilealligator.infinityforreddit.utils.Utils;

public class MediaEntry extends MarkwonAdapter.Entry<CommentMediaBlock, MediaEntry.Holder> {
    private final RequestManager glide;
    private final CustomThemeWrapper customThemeWrapper;
    private final Consumer<Uri> onClickListener;

    public MediaEntry(RequestManager glide, CustomThemeWrapper customThemeWrapper, Consumer<Uri> onClickListener) {
        this.glide = glide;
        this.customThemeWrapper = customThemeWrapper;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public MediaEntry.Holder createHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(AdapterGifEntryBinding.inflate(inflater, parent, false));
    }

    @Override
    public void bindHolder(@NonNull Markwon markwon, @NonNull MediaEntry.Holder holder, @NonNull CommentMediaBlock node) {
        if (node.metadata instanceof GiphyGifMetadata) {
            holder.bindGif((GiphyGifMetadata) node.metadata);
        } else if (node.metadata instanceof ImageMetadata) {
            holder.bindImage((ImageMetadata) node.metadata);
        }
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
                binding.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                binding.progressBar.setVisibility(View.GONE);
                return false;
            }
        };

        public Holder(@NonNull AdapterGifEntryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.progressBar.setIndeterminateTintList(ColorStateList.valueOf(customThemeWrapper.getColorAccent()));
            binding.giphyWatermark.setTextColor(customThemeWrapper.getCommentColor());

            binding.gifLink.setTextColor(customThemeWrapper.getLinkColor());

        }

        void bindImage(ImageMetadata image) {
            binding.gifLink.setVisibility(View.GONE);
            binding.iv.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.giphyWatermark.setVisibility(View.GONE);

            ViewGroup.LayoutParams params = binding.iv.getLayoutParams();
            if (image.x > image.y) {
                params.height = dpToPx(160);
                params.width = params.height * image.x / image.y;
            } else {
                params.width = dpToPx(160);
                params.height = params.width * image.y / image.x;
            }
            binding.iv.setLayoutParams(params);

            // todo: check if waitForLayout is necessary here since we explicitly set width/height in LP
            Target<Drawable> target = new DrawableImageViewTarget(binding.iv)
                    .waitForLayout();
            glide.load(image.getUrl())
                    .addListener(requestListener)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(target);
        }

        void bindGif(GiphyGifMetadata gif) {
            if (!canLoadGif()) {
                // video autoplay is disabled, don't load gif
                binding.gifLink.setVisibility(View.VISIBLE);
                binding.iv.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
                binding.giphyWatermark.setVisibility(View.GONE);

                binding.gifLink.setOnClickListener(v -> {
                    onClickListener.accept(Uri.parse(gif.getGifUrl()));
                });
                return;
            }
            binding.gifLink.setVisibility(View.GONE);
            binding.iv.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.giphyWatermark.setVisibility(View.VISIBLE);

            ViewGroup.LayoutParams params = binding.iv.getLayoutParams();
            if (gif.x > gif.y) {
                params.height = dpToPx(160);
                params.width = params.height * gif.x / gif.y;
            } else {
                params.width = dpToPx(160);
                params.height = params.width * gif.y / gif.x;
            }
            binding.iv.setLayoutParams(params);

            // todo: check if waitForLayout is necessary here since we explicitly set width/height in LP
            Target<Drawable> target = new DrawableImageViewTarget(binding.iv)
                    .waitForLayout();
            glide.load(gif.getGifUrl())
                    .addListener(requestListener)
                    .error(R.drawable.ic_error_outline_black_24dp)
                    .into(target);
        }

        void recycle() {
            glide.clear(binding.iv);
        }

        @SuppressWarnings("SameParameterValue")
        private int dpToPx(int dp) {
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            return (int) (dp * density);
        }

        private boolean canLoadGif() {
            // ideally this would be injected, but it is a bit unpleasant to do
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            String dataSavingMode = sharedPreferences.getString(SharedPreferencesUtils.DATA_SAVING_MODE, SharedPreferencesUtils.DATA_SAVING_MODE_OFF);
            Log.i("GifEntry", "datasaving=" + dataSavingMode);
            if (dataSavingMode.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ALWAYS)) {
                return false;
            } else if (dataSavingMode.equals(SharedPreferencesUtils.DATA_SAVING_MODE_ONLY_ON_CELLULAR_DATA)) {
                int networkType = Utils.getConnectedNetwork(itemView.getContext());
                return networkType != Utils.NETWORK_TYPE_CELLULAR;
            } else {
                return true;
            }
        }
    }
}
