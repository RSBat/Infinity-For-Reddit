package ml.docilealligator.infinityforreddit.customviews;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import ml.docilealligator.infinityforreddit.customtheme.CustomThemeWrapper;

public class TabsColorSynchronizer implements AppBarLayout.OnOffsetChangedListener {
    private final CollapsingToolbarLayout collapsingToolbarLayout;
    private final TabLayout tabLayout;
    private final int expandedTabTextColor;
    private final int expandedTabIndicatorColor;
    private final int expandedTabBackgroundColor;
    private final int collapsedTabTextColor;
    private final int collapsedTabIndicatorColor;
    private final int collapsedTabBackgroundColor;

    private boolean lastScrimVisible = true;

    public TabsColorSynchronizer(CollapsingToolbarLayout collapsingToolbarLayout,
                                 TabLayout tabLayout,
                                 CustomThemeWrapper customThemeWrapper) {
        this.collapsingToolbarLayout = collapsingToolbarLayout;
        this.tabLayout = tabLayout;
        expandedTabTextColor = customThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTextColor();
        expandedTabIndicatorColor = customThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTabIndicator();
        expandedTabBackgroundColor = customThemeWrapper.getTabLayoutWithExpandedCollapsingToolbarTabBackground();
        collapsedTabTextColor = customThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTextColor();
        collapsedTabIndicatorColor = customThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTabIndicator();
        collapsedTabBackgroundColor = customThemeWrapper.getTabLayoutWithCollapsedCollapsingToolbarTabBackground();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int scrimTrigger = collapsingToolbarLayout.getScrimVisibleHeightTrigger();
        int height = collapsingToolbarLayout.getHeight();
        boolean scrimVisible = height + verticalOffset < scrimTrigger;
        if (scrimVisible == lastScrimVisible) {
            // don't try to update any views, this causes lag
            return;
        }
        lastScrimVisible = scrimVisible;

        if (scrimVisible) {
            tabLayout.setTabTextColors(collapsedTabTextColor, collapsedTabTextColor);
            tabLayout.setSelectedTabIndicatorColor(collapsedTabIndicatorColor);
            tabLayout.setBackgroundColor(collapsedTabBackgroundColor);
        } else {
            tabLayout.setTabTextColors(expandedTabTextColor, expandedTabTextColor);
            tabLayout.setSelectedTabIndicatorColor(expandedTabIndicatorColor);
            tabLayout.setBackgroundColor(expandedTabBackgroundColor);
        }
    }
}
