package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import relish.permoveo.com.relish.model.Friend;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class FriendsManager {
    private static Context context;

    public static void initialize(Context aContext) {
        context = aContext;
    }

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

    public static void addFriend(final String groupName, final String friendId, final FriendsManagerCallback callback) {
        ArrayList<String> friends = (ArrayList<String>) ParseUser.getCurrentUser().get(groupName + "Group");
        if (friends == null)
            friends = new ArrayList<>();
        friends.add(friendId);
        ParseUser.getCurrentUser().put(groupName + "Group", friends);
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParseUser.getQuery().getInBackground(friendId, new GetCallback<ParseUser>() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            ArrayList<String> friendFriends = (ArrayList<String>) parseUser.get(groupName + "Group");
                            if (friendFriends == null)
                                friendFriends = new ArrayList<>();
                            friendFriends.add(ParseUser.getCurrentUser().getObjectId());
                            parseUser.put(groupName + "Group", friendFriends);
                            parseUser.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        callback.done(null, null);
                                    } else {
                                        callback.done(null, e);
                                    }
                                }
                            });
                        }
                    });
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
                        Geocoder geocoder = new Geocoder(context);
                        try {
                            List<Address> listAddresses = geocoder.getFromLocation(friend.location.getLatitude(), friend.location.getLongitude(), 1);
                            if (null != listAddresses && listAddresses.size() > 0) {
                                if (listAddresses.get(0).getMaxAddressLineIndex() > 0)
                                    friend.address = listAddresses.get(0).getAddressLine(1);
                                else if (listAddresses.get(0).getMaxAddressLineIndex() == 0)
                                    friend.address = listAddresses.get(0).getAddressLine(0);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
