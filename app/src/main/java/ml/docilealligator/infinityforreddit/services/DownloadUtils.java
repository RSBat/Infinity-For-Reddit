package ml.docilealligator.infinityforreddit.services;

import static ml.docilealligator.infinityforreddit.services.DownloadMediaService.EXTRA_MEDIA_TYPE_GIF;
import static ml.docilealligator.infinityforreddit.services.DownloadMediaService.EXTRA_MEDIA_TYPE_VIDEO;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import kotlin.Result;
import ml.docilealligator.infinityforreddit.R;
import ml.docilealligator.infinityforreddit.utils.SharedPreferencesUtils;

public class DownloadUtils {
    @NonNull
    public static String getBaseDownloadDirectory(int mediaType, boolean isNsfw, SharedPreferences sharedPreferences) {
            if (isNsfw && sharedPreferences.getBoolean(SharedPreferencesUtils.SAVE_NSFW_MEDIA_IN_DIFFERENT_FOLDER, false)) {
                return sharedPreferences.getString(SharedPreferencesUtils.NSFW_DOWNLOAD_LOCATION, "");
            }
            switch (mediaType) {
                case EXTRA_MEDIA_TYPE_GIF:
                    return sharedPreferences.getString(SharedPreferencesUtils.GIF_DOWNLOAD_LOCATION, "");
                case EXTRA_MEDIA_TYPE_VIDEO:
                    return sharedPreferences.getString(SharedPreferencesUtils.VIDEO_DOWNLOAD_LOCATION, "");
                default:
                    return sharedPreferences.getString(SharedPreferencesUtils.IMAGE_DOWNLOAD_LOCATION, "");
            }
    }
    
    public static void checkDownloadLocationPermission(int mediaType, boolean isNSFW, SharedPreferences sharedPreferences, Context context, Runnable download) {
        if (hasDownloadLocationPermission(mediaType, isNSFW, sharedPreferences, context.getContentResolver())) {
            download.run();
        } else {
            new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogTheme)
                    .setMessage("Omega doesn't have necessary permissions to save file to the selected folder. Please select a new download location in the settings. If you've already done that you can try to ignore this error and download the file anyways.")
                    .setPositiveButton("Understood", null)
                    .setNeutralButton("Ignore", (dialogInterface, i) -> download.run())
                    .show();
        }
    }

    public static boolean hasDownloadLocationPermission(int mediaType, boolean isNSFW, SharedPreferences sharedPreferences, ContentResolver contentResolver) {
        String dir = DownloadUtils.getBaseDownloadDirectory(mediaType, isNSFW, sharedPreferences);
        if (dir.equals("")) {
            return true;
        } else {
            Uri uri = Uri.parse(dir);
            boolean hasRead = false;
            boolean hasWrite = false;

            List<UriPermission> permissions = contentResolver.getPersistedUriPermissions();
            for (UriPermission permission: permissions) {
                if (permission.getUri().equals(uri) && permission.isReadPermission()) {
                    hasRead = true;
                }
                if (permission.getUri().equals(uri) && permission.isWritePermission()) {
                    hasWrite = true;
                }
            }
            return hasRead && hasWrite;
        }
    }
}
