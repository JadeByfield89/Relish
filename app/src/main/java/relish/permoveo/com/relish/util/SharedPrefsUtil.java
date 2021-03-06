package relish.permoveo.com.relish.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;

/**
 * Created by byfieldj on 8/4/15.
 */
public enum SharedPrefsUtil {
    get;

    /* Shared preference keys */
    private static final String PREF_NAME = "twitter_pref";
    private static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private static final String PREF_KEY_TWITTER_LOGIN = "is_twitter_loggedin";
    private static final String PREF_TWITTER_USER_NAME = "twitter_user_name";
    // Venmo access token
    private static final String PREF_VENMO_TOKEN = "venmo_token";
    private static final String PREF_VENMO_TOKEN_TIMESTAMP = "venmo_token_timestamp";
    public static String PARAM_APP_LAUNCHED = "appLaunched";
    public static String PARAM_LAST_VISIBLE_FRIENDS_COUNT = "last_visible_friends_count_for";
    public static String PARAM_LAST_VISIBLE_INVITES_COUNT = "last_visible_invites_count";
    public static String PARAM_PUSH_NOTIFICATIONS = "push_notifications";
    public static String PARAM_LOCATION_SHARING = "location_sharing";
    public static String PARAM_GOOGLE_CALENDAR_SYNC = "google_calendar_sync";
    public static String PARAM_PHONE_VERIFIED = "phone_number_verified";

    private static String PREF_FACEBOOK_ACCEESS_TOKEN = "faceook_access_token";
    private SharedPreferences sharedPreferences;

    public void clearAll() {
        sharedPreferences.edit().clear().apply();
    }

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
        return sharedPreferences.getBoolean(PARAM_GOOGLE_CALENDAR_SYNC, false);

    }

    /**
     * Saving user information, after user is authenticated for the first time.
     * You don't need to show user to login, until user has a valid access toen
     */
    public void saveTwitterInfo(AccessToken accessToken, Twitter twitter) {

        long userID = accessToken.getUserId();

        User user;
        try {
            user = twitter.showUser(userID);

            String username = user.getScreenName();

			/* Storing oAuth tokens to shared preferences */
            SharedPreferences.Editor e = sharedPreferences.edit();
            e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
            Log.d("SharedPrefsUtil", "Saving Access Token -> " + accessToken.getToken());
            e.putString(PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
            Log.d("SharedPrefsUtil", "Saving Access Token Secret -> " + accessToken.getTokenSecret());

            e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
            e.putString(PREF_TWITTER_USER_NAME, username);
            e.commit();

        } catch (TwitterException e1) {
            e1.printStackTrace();
        }
    }

    public boolean isLoggedIntoTwitter() {
        return sharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

    public String getTwitterUsername() {
        return sharedPreferences.getString(PREF_TWITTER_USER_NAME, "");
    }

    public void setIsLoggedIntoTwitter() {
        sharedPreferences.edit().putBoolean(PREF_KEY_TWITTER_LOGIN, true).commit();
    }

    public String getSavedAccessToken() {
        return sharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
    }

    public String getSavedAccessTokenSecret() {
        return sharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
    }

    public void saveVenmoAccessToken(String token) {
        long saveTime = System.currentTimeMillis();
        sharedPreferences.edit().putString(PREF_VENMO_TOKEN, token).commit();
        sharedPreferences.edit().putLong(PREF_VENMO_TOKEN_TIMESTAMP, saveTime).commit();
    }

    public String getVenmoAccessToken() {
        return sharedPreferences.getString(PREF_VENMO_TOKEN, "");
    }

    public long getVenmoAccessTokenSaveTime() {
        return sharedPreferences.getLong(PREF_VENMO_TOKEN_TIMESTAMP, 0);
    }

    public void saveFacebookAccessToken(com.facebook.AccessToken token) {
        sharedPreferences.edit().putString(PREF_FACEBOOK_ACCEESS_TOKEN, token.getToken()).commit();
    }

    public String getFacebookAccessToken() {
        return sharedPreferences.getString(PREF_FACEBOOK_ACCEESS_TOKEN, "");
    }

    public void setPhoneNumberVerified(boolean verified){
        sharedPreferences.edit().putBoolean(PARAM_PHONE_VERIFIED,  verified).commit();
    }

    public boolean isPhoneNumberVerified(){
        return sharedPreferences.getBoolean(PARAM_PHONE_VERIFIED, false);
    }

}

