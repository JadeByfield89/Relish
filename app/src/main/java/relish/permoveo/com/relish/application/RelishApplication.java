package relish.permoveo.com.relish.application;

import android.app.Application;

import com.parse.Parse;

import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class RelishApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, ConstantUtil.PARSE_APPLICATION_ID, ConstantUtil.PARSE_CLIENT_KEY);
    }
}
