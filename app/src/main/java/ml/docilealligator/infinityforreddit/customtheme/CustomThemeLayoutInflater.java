package ml.docilealligator.infinityforreddit.customtheme;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class CustomThemeLayoutInflater extends LayoutInflater {
    private static final String TAG = "CTLayoutInflater";

    private static final String[] sClassPrefixList = {
            "android.widget.",
            "android.webkit.",
            "android.app."
    };

    @NonNull
    private final CustomThemeWrapper mCustomThemeWrapper;
    @ColorInt
    private final int circularProgressBarBackground;

    public CustomThemeLayoutInflater(Context context,
                                     @NonNull CustomThemeWrapper customThemeWrapper) {
        super(context);
        mCustomThemeWrapper = customThemeWrapper;

        circularProgressBarBackground = mCustomThemeWrapper.getCircularProgressBarBackground();

        var factory = new Factory(customThemeWrapper);
//        setFactory(factory);
        setFactory2(factory);
    }

    @Override
    public LayoutInflater cloneInContext(Context newContext) {
        return new CustomThemeLayoutInflater(newContext, mCustomThemeWrapper);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull Context viewContext, @Nullable View parent, @NonNull String name, @Nullable AttributeSet attrs) throws ClassNotFoundException {
        Log.d(TAG, "onCreateView4 " + name);
        return super.onCreateView(viewContext, parent, name, attrs);
    }

    @Override
    protected View onCreateView(View parent, String name, AttributeSet attrs) throws ClassNotFoundException {
        Log.d(TAG, "onCreateView3 " + name);
        return super.onCreateView(parent, name, attrs);
    }

    @Override
    protected View onCreateView(String name, AttributeSet attrs) throws ClassNotFoundException {
        Log.d(TAG, "onCreateView2 " + name);

        View view = null;
        for (String prefix : sClassPrefixList) {
            try {
                view = createView(name, prefix, attrs);
                if (view != null) {
                    break;
                }
            } catch (ClassNotFoundException e) {
                // In this case we want to let the base class take a crack
                // at it.
            }
        }

        if (view == null) {
            view = super.onCreateView(name, attrs);
        }

        if (view instanceof SwipeRefreshLayout) {
            Log.d(TAG, "SwipeRefreshLayout");
            Log.d(TAG, "Accent " + mCustomThemeWrapper.getColorAccent());
            ((SwipeRefreshLayout) view).setProgressBackgroundColorSchemeColor(circularProgressBarBackground);
            ((SwipeRefreshLayout) view).setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
        }

        return view;
    }

    private static class Factory implements LayoutInflater.Factory, LayoutInflater.Factory2 {
        private static final String SWIPE_REFRESH_LAYOUT = "androidx.swiperefreshlayout.widget.SwipeRefreshLayout";

        private final CustomThemeWrapper mCustomThemeWrapper;

        public Factory(CustomThemeWrapper customThemeWrapper) {
            mCustomThemeWrapper = customThemeWrapper;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
            Log.d(TAG, "Factory.onCreateView " + name);

            if (SWIPE_REFRESH_LAYOUT.equals(name)) {
                SwipeRefreshLayout view = new SwipeRefreshLayout(context, attrs);
                view.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
                view.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
                return view;
            }
            return null;
        }

        @Nullable
        @Override
        public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
            Log.d(TAG, "Factory2.onCreateView " + name);

            if (SWIPE_REFRESH_LAYOUT.equals(name)) {
                SwipeRefreshLayout view = new SwipeRefreshLayout(context, attrs);
                view.setProgressBackgroundColorSchemeColor(mCustomThemeWrapper.getCircularProgressBarBackground());
                view.setColorSchemeColors(mCustomThemeWrapper.getColorAccent());
                return view;
            }
            return null;
        }
    }
}
