package relish.permoveo.com.relish.manager;

import android.os.AsyncTask;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
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
                if (e == null) {
                    final ParseUser user = (ParseUser) parseObject;
                    ArrayList<Friend> friends = new ArrayList<>();
                    if (user != null) {
                        ArrayList<String> friendsIds = (ArrayList<String>) user.get(group.toLowerCase() + "Group");
                        if (friendsIds == null || friendsIds.size() == 0) {
                            callback.done(friends, null);
                        } else {
                            new LoadParseUsersTask(callback).execute(friendsIds);
                        }
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    private static class LoadParseUsersTask extends AsyncTask<ArrayList<String>, Void, ArrayList<Friend>> {

        private FriendsManagerCallback callback;

        public LoadParseUsersTask(FriendsManagerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ArrayList<Friend> doInBackground(ArrayList<String>... params) {
            ArrayList<String> friendsIds = params[0];
            ArrayList<Friend> friends = new ArrayList<>();
            for (String objectId : friendsIds) {
                try {
                    ParseUser user = ParseUser.getQuery().get(objectId);
                    Friend friend = new Friend();
                    friend.id = user.getObjectId();
                    friend.name = user.getUsername();
                    if (user.containsKey("avatar")) {
                        ParseFile parseFile = (ParseFile) user.get("avatar");
                        friend.image = parseFile.getUrl();
                    }
                    if (user.containsKey("location")) {
                        friend.location = (ParseGeoPoint) user.get("location");
                    }
                    friends.add(friend);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return friends;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            super.onPostExecute(friends);
            callback.done(friends, null);
        }
    }

}
