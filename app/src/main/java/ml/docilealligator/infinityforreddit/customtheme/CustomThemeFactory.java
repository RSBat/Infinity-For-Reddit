package ml.docilealligator.infinityforreddit.customtheme;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import ml.docilealligator.infinityforreddit.BuildConfig;

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

        return view;
    }

    @NonNull
    private SwipeRefreshLayout createSwipeRefreshLayout(@NonNull Context context, @NonNull AttributeSet attrs) {
        SwipeRefreshLayout swipeRefreshLayout = new SwipeRefreshLayout(context, attrs);
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
        swipeRefreshLayout.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        return swipeRefreshLayout;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d("CTF", "oCV3 " + name);
        return null;
    }
}
