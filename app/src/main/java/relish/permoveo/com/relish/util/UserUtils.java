package relish.permoveo.com.relish.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Base64;

import com.parse.ParseFile;
import com.parse.ParseUser;

import relish.permoveo.com.relish.activities.SignupActivity;

/**
 * Utility class for getting user info, can use to retrieve username, first name, email, avatar url, etc
 * Created by byfieldj on 8/14/15.
 */
public class UserUtils {

    public static String getUsername() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUsername();
        } else
            return "";

    }


    public static String getUserEmail() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser.getEmail();
        } else
            return "";
    }

    public static String getFullName() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return currentUser.get("fullName").toString();
        } else
            return "";
    }

    public static String getFirstName() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            String fullname = currentUser.get("fullName").toString();
            String first = fullname.split(" ")[0];
            return first;
        } else
            return "";
    }


    public static String getUserAvatar() {

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {

            if (currentUser.getParseFile("avatar") != null) {
                return currentUser.getParseFile("avatar").getUrl();
            } else
                return "";
        } else
            return "";
    }

    public static String uploadUserAvatar(byte[] bytes) {

        String fileName = "user_avatar.jpg";

        ParseFile file = new ParseFile(fileName, bytes);
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put("avatar", file);
            user.saveInBackground();
        }
        return getUserAvatar();

    }

    public static void logoutUser(final Activity context) {
        SharedPrefsUtil.get.clearAll();
        ParseUser.logOutInBackground();
        Intent intent = new Intent(context, SignupActivity.class);
        context.startActivity(intent);
        context.finish();
    }


}
