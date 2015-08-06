package relish.permoveo.com.relish.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;

import net.danlew.android.joda.JodaTimeAndroid;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class RelishApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, ConstantUtil.PARSE_APPLICATION_ID, ConstantUtil.PARSE_CLIENT_KEY);
        FacebookSdk.sdkInitialize(getApplicationContext());
        TypefaceUtil.init(this);
        GPSTracker.get.init(this);
        API.init(this);
        JodaTimeAndroid.init(this);

    }
}
