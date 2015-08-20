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
    private SharedPreferences sharedPreferences;


    public void init(final Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasLaunchedPrior(){
        return sharedPreferences.getBoolean(PARAM_APP_LAUNCHED, false);
    }

    public void setAppLaunched(){
        sharedPreferences.edit().putBoolean(PARAM_APP_LAUNCHED, true).commit();
    }

    public int lastVisibleFriendsCountForGroup(String group) { return sharedPreferences.getInt(PARAM_LAST_VISIBLE_FRIENDS_COUNT + "_" + group, -1); }

    public void setLastVisibleFriendsCountForGroup(String group, int count) { sharedPreferences.edit().putInt(PARAM_LAST_VISIBLE_FRIENDS_COUNT + "_" + group, count).commit(); }
}
