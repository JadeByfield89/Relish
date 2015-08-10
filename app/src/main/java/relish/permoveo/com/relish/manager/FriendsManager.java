package relish.permoveo.com.relish.manager;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import relish.permoveo.com.relish.model.Friend;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class FriendsManager {
    public interface FriendsManagerCallback<T1, T2> {
        void done(T1 t1, T2 t2);
    }

    public static void retrieveFriendsList(final String group, final FriendsManagerCallback callback) {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final ParseUser user = (ParseUser) parseObject;
                ArrayList<Friend> friends = new ArrayList<>();
                ArrayList<String> friendsIds = (ArrayList<String>) user.get(group.toLowerCase() + "Group");
                if (friendsIds == null || friendsIds.size() == 0) {
                    callback.done(friends, null);
                } else {

                }
            }
        });
    }

    public static void queryUsers(final String query, final FriendsManagerCallback callback) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereStartsWith("username", query);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {

                } else {
                }
            }
        });
    }

}
