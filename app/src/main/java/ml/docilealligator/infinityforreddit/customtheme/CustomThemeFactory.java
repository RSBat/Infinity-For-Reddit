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
                view = new SwipeRefreshLayout(context, attrs);
                break;
        }

        if (view instanceof SwipeRefreshLayout) {
            Log.d("CTF", "SRL");
            ((SwipeRefreshLayout) view).setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
            ((SwipeRefreshLayout) view).setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        }
        if (view == null) {
            Log.d("CTF", "null");
        }
        return view;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d("CTF", "oCV3 " + name);
        return null;
    }
}
