package relish.permoveo.com.relish.util;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/** Utility class for getting user info, can use to retrieve username, first name, email, avatar url, etc
 * Created by byfieldj on 8/14/15.
 */
public class UserUtils {

    public static String getUsername(){
        return ParseUser.getCurrentUser().getUsername();
    }


    public static String getUserEmail(){

        return ParseUser.getCurrentUser().getEmail();
    }

    public static String getUserAvatar(){

       ParseFile avatarFile = ParseUser.getCurrentUser().getParseFile("avatar");

        return avatarFile.getUrl();
    }
}
