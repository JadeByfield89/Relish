package relish.permoveo.com.relish.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import twitter4j.User;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class RelishApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "VjJP3yLiBkslHjbhhkuG8NG5I";
    private static final String TWITTER_SECRET = "SXadqJeE7IXDoXjVB6g4w23OoJq0jVwfH22i74XJpSHlDmTp0y";

    private static ArrayList<Contact> followersList;


    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());
        Parse.initialize(this, ConstantUtil.PARSE_APPLICATION_ID, ConstantUtil.PARSE_CLIENT_KEY);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        FacebookSdk.sdkInitialize(getApplicationContext());
        TypefaceUtil.init(this);
        GPSTracker.get.init(this);
        API.init(this);
        JodaTimeAndroid.init(this);
        FriendsManager.initialize(this);
        InvitesManager.initialize(this);
        SharedPrefsUtil.get.init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }

    public static void setTwitterFollowersList(ArrayList<Contact> followers){
        followersList = followers;
    }

    public static ArrayList<Contact> getTwitterFollowersList(){
        return followersList;
    }
}
