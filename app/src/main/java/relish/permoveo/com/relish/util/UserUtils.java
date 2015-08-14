package relish.permoveo.com.relish.util;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/** Utility class for getting user info, can use to retrieve username, first name, email, avatar url, etc
 * Created by byfieldj on 8/14/15.
 */
public class UserUtils {

    public static String getUsername(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            return currentUser.getUsername();
        }
        else
            return "";

    }


    public static String getUserEmail(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){
            return currentUser.getEmail();
        }
        else
            return "";
    }

    public static String getUserAvatar(){

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null){

           if(currentUser.getParseFile("avatar") != null){
               return currentUser.getParseFile("avatar").getUrl();
           }
            else
            return "";
        }
        else
            return "";
    }
}
