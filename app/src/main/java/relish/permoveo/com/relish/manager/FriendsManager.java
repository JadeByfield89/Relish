package relish.permoveo.com.relish.manager;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

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

}
