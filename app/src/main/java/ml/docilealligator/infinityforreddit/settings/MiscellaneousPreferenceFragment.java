package ml.docilealligator.infinityforreddit.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.SwitchPreference;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import ml.docilealligator.infinityforreddit.Infinity;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.customviews.CustomFontPreferenceFragmentCompat;
import ml.docilealligator.infinityforreddit.events.ChangePostFeedMaxResolutionEvent;
import ml.docilealligator.infinityforreddit.events.ChangeSavePostFeedScrolledPositionEvent;
import ml.docilealligator.infinityforreddit.events.RecreateActivityEvent;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;

public class MiscellaneousPreferenceFragment extends CustomFontPreferenceFragmentCompat {

    @Inject
    @Named("post_feed_scrolled_position_cache")
    SharedPreferences cache;

    public MiscellaneousPreferenceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.miscellaneous_preferences, rootKey);

        ((Infinity) activity.getApplication()).getAppComponent().inject(this);

        ListPreference mainPageBackButtonActionListPreference = findPreference(SharedPreferencesUtils.MAIN_PAGE_BACK_BUTTON_ACTION);
        SwitchPreference savePostFeedScrolledPositionSwitch = findPreference(SharedPreferencesUtils.SAVE_FRONT_PAGE_SCROLLED_POSITION);
        ListPreference languageListPreference = findPreference(SharedPreferencesUtils.LANGUAGE);
        EditTextPreference postFeedMaxResolution = findPreference(SharedPreferencesUtils.POST_FEED_MAX_RESOLUTION);

        if (mainPageBackButtonActionListPreference != null) {
            mainPageBackButtonActionListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                EventBus.getDefault().post(new RecreateActivityEvent());
                return true;
            });
        }

        if (savePostFeedScrolledPositionSwitch != null) {
            savePostFeedScrolledPositionSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
                if (!(Boolean) newValue) {
                    cache.edit().clear().apply();
                }
                EventBus.getDefault().post(new ChangeSavePostFeedScrolledPositionEvent((Boolean) newValue));
                return true;
            });
        }

        if (languageListPreference != null) {
            // display correct value in case user language setting from system settings
            LocaleListCompat currentLocales = AppCompatDelegate.getApplicationLocales();
            String[] supportedLocales = getResources().getStringArray(R.array.settings_language_values);
            Locale currentLocale = currentLocales.getFirstMatch(supportedLocales);
            if (currentLocale != null) {
                boolean exactMatch = false;
                for (String preferenceValue: supportedLocales) {
                    // this looks complicated, but it is necessary because ISO 639 names can change
                    Locale locale = Locale.forLanguageTag(preferenceValue);
                    boolean languageMatch = currentLocale.getLanguage().equals(locale.getLanguage());
                    boolean countryMatch = locale.getCountry().isEmpty() || currentLocale.getCountry().equals(locale.getCountry());
                    if (languageMatch && countryMatch) {
                        languageListPreference.setValue(preferenceValue);
                        exactMatch = true;
                        break;
                    }
                }

                // sometimes user can choose language for which we don't have an exact match,
                // for example pt-AO doesn't match either pt-PT or pt-BR
                // find any compatible locale here
                if (!exactMatch) {
                    for (String preferenceValue: supportedLocales) {
                        Locale locale = Locale.forLanguageTag(preferenceValue);
                        if (LocaleListCompat.matchesLanguageAndScript(currentLocale, locale)) {
                            languageListPreference.setValue(preferenceValue);
                            break;
                        }
                    }
                }
            }

            languageListPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean useSystemLocale = SharedPreferencesUtils.LANGUAGE_DEFAULT_VALUE.equals(newValue);
                LocaleListCompat appLocale = useSystemLocale
                        ? LocaleListCompat.getEmptyLocaleList()
                        : LocaleListCompat.forLanguageTags((String) newValue);
                AppCompatDelegate.setApplicationLocales(appLocale);
                return true;
            });
        }

        if (postFeedMaxResolution != null) {
            postFeedMaxResolution.setOnPreferenceChangeListener((preference, newValue) -> {
                try {
                    int resolution = Integer.parseInt((String) newValue);
                    if (resolution <= 0) {
                        Toast.makeText(activity, R.string.not_a_valid_number, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    EventBus.getDefault().post(new ChangePostFeedMaxResolutionEvent(resolution));
                } catch (NumberFormatException e) {
                    Toast.makeText(activity, R.string.not_a_valid_number, Toast.LENGTH_SHORT).show();
                    return false;
                }
                return true;
            });
        }
    }
}