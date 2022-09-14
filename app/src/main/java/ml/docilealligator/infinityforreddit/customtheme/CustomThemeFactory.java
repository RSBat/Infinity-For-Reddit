package ml.docilealligator.infinityforreddit.customtheme;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ml.docilealligator.infinityforreddit.BuildConfig;
import ml.docilealligator.infinityforreddit.R;

public class CustomThemeFactory implements LayoutInflater.Factory2 {
    @NonNull
    private final AppCompatDelegate mDelegate;
    @NonNull
    private final CustomThemeWrapper mCustomThemeWrapper;

    public CustomThemeFactory(@NonNull AppCompatDelegate delegate,
                              @NonNull CustomThemeWrapper customThemeWrapper) {
        mDelegate = delegate;
        mCustomThemeWrapper = customThemeWrapper;
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d("CTF", "oCV4 " + name);
        View view = mDelegate.createView(parent, name, context, attrs);
        switch (name) {
            case "androidx.swiperefreshlayout.widget.SwipeRefreshLayout":
                if (BuildConfig.DEBUG && view != null) {
                    throw new IllegalStateException("View " + name + " got inflated by the delegate");
                }
                view = createSwipeRefreshLayout(context, attrs);
                break;
        }

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            applyTextColor(textView, attrs);
        }

        return view;
    }

    @NonNull
    private SwipeRefreshLayout createSwipeRefreshLayout(@NonNull Context context, @NonNull AttributeSet attrs) {
        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(context, attrs);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        swipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        return swipeRefreshLayout;
    }

    private void applyTextColor(@NonNull TextView textView, @NonNull AttributeSet attrs) {
        int res = attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "textColor", -1);
        switch (res) {
            case R.color.postTitleTextColor:
                textView.setTextColor(mCustomThemeWrapper.getPostTitleColor());
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d("CTF", "oCV3 " + name);
        return null;
    }
}
