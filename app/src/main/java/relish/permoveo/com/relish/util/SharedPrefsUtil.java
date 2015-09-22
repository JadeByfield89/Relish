package relish.permoveo.com.relish.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by byfieldj on 8/4/15.
 */
public enum SharedPrefsUtil {
    get;

    public static String PARAM_APP_LAUNCHED = "appLaunched";
    public static String PARAM_LAST_VISIBLE_FRIENDS_COUNT = "last_visible_friends_count_for";
    public static String PARAM_LAST_VISIBLE_INVITES_COUNT = "last_visible_invites_count";
    public static String PARAM_PUSH_NOTIFICATIONS = "push_notifications";
    public static String PARAM_LOCATION_SHARING = "location_sharing";
    public static String PARAM_GOOGLE_CALENDAR_SYNC = "google_calendar_sync";
    private SharedPreferences sharedPreferences;


    public void init(final Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasLaunchedPrior() {
        return sharedPreferences.getBoolean(PARAM_APP_LAUNCHED, false);
    }

    public void setAppLaunched() {
        sharedPreferences.edit().putBoolean(PARAM_APP_LAUNCHED, true).commit();
    }

    public int lastVisibleFriendsCount() {
        return sharedPreferences.getInt(PARAM_LAST_VISIBLE_FRIENDS_COUNT + "_friends", -1);
    }

    public void setLastVisibleFriendsCount(int count) {
        sharedPreferences.edit().putInt(PARAM_LAST_VISIBLE_FRIENDS_COUNT + "_friends", count).commit();
    }

    public int lastVisibleInvitesCount() {
        return sharedPreferences.getInt(PARAM_LAST_VISIBLE_INVITES_COUNT, -1);
    }

    public void setLastVisibleInvitesCount(int count) {
        sharedPreferences.edit().putInt(PARAM_LAST_VISIBLE_INVITES_COUNT, count).commit();
    }

    public void togglePushNotifications() {

        // If PUSH is turned ON
        if (sharedPreferences.getBoolean(PARAM_PUSH_NOTIFICATIONS, false)) {
            // Turn it off
            sharedPreferences.edit().putBoolean(PARAM_PUSH_NOTIFICATIONS, false).commit();
        } else {
            // Turn it on
            sharedPreferences.edit().putBoolean(PARAM_PUSH_NOTIFICATIONS, true).commit();
        }
    }

    public boolean arePushNotificationsEnabled() {
        return sharedPreferences.getBoolean(PARAM_PUSH_NOTIFICATIONS, false);
    }

    public void toggleLocationSharing() {

        // If Location Sharing is turned ON
        if (sharedPreferences.getBoolean(PARAM_LOCATION_SHARING, false)) {
            // Turn it off
            sharedPreferences.edit().putBoolean(PARAM_LOCATION_SHARING, false).commit();
        } else {
            // Turn it on
            sharedPreferences.edit().putBoolean(PARAM_LOCATION_SHARING, true).commit();
        }
    }

    public boolean isLocationSharingEnabled() {
        return sharedPreferences.getBoolean(PARAM_LOCATION_SHARING, false);

    }

    public void toggleGoogleCalendarSync() {
        // If Location Sharing is turned ON
        if (sharedPreferences.getBoolean(PARAM_GOOGLE_CALENDAR_SYNC, false)) {
            // Turn it off
            sharedPreferences.edit().putBoolean(PARAM_GOOGLE_CALENDAR_SYNC, false).commit();
        } else {
            // Turn it on
            sharedPreferences.edit().putBoolean(PARAM_GOOGLE_CALENDAR_SYNC, true).commit();
        }
    }

    public boolean isGoogleCalendarSyncEnabled() {
        return sharedPreferences.getBoolean(PARAM_LOCATION_SHARING, false);

    }

}

