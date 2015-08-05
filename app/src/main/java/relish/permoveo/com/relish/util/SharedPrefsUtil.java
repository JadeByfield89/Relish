package relish.permoveo.com.relish.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by byfieldj on 8/4/15.
 */
public class SharedPrefsUtil {

    public static String PARAM_APP_LAUNCHED = "appLaunched";
    private SharedPreferences sharedPreferences;


    public SharedPrefsUtil(final Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean hasLaunchedPrior(){
        return sharedPreferences.getBoolean(PARAM_APP_LAUNCHED, false);
    }

    public void setAppLaunched(){
        sharedPreferences.edit().putBoolean(PARAM_APP_LAUNCHED, true).commit();
    }
}
