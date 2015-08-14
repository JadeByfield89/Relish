package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public static void searchFriend(String query, final FriendsManagerCallback callback) {
        ParseQuery<ParseUser> usernameQuery = ParseUser.getQuery();
        usernameQuery.whereEqualTo("username", query);

        ParseQuery<ParseUser> emailQuery = ParseUser.getQuery();
        emailQuery.whereEqualTo("email", query);

        ParseQuery searchQuery = ParseQuery.or(Arrays.asList(usernameQuery, emailQuery));
        searchQuery.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    new FriendsFromParseTask(callback).execute(objects);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void retrieveFriendsList(final String group, final FriendsManagerCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friendship");
        query.whereEqualTo("group", group);
        query.whereEqualTo("userIds", ParseUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<Friend> friends = new ArrayList<>();
                    if (parseObjects == null || parseObjects.size() == 0) {
                        callback.done(friends, null);
                    } else {
                        ArrayList<String> friendsIds = new ArrayList<>();
                        for (ParseObject parseObject : parseObjects) {
                            ArrayList<String> friendshipIds = (ArrayList<String>) parseObject.get("userIds");
                            for (String id : friendshipIds) {
                                if (!id.equals(ParseUser.getCurrentUser().getObjectId()))
                                    friendsIds.add(id);
                            }
                        }
                        new LoadParseUsersTask(callback).execute(friendsIds);
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void addFriend(final String groupName, final String friendId, final FriendsManagerCallback callback) {
        ParseObject friendship = new ParseObject("Friendship");
        friendship.put("group", groupName);
        friendship.addAllUnique("userIds", Arrays.asList(friendId, ParseUser.getCurrentUser().getObjectId()));
        friendship.saveInBackground(new SaveCallback() {
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

    private static class FriendsFromParseTask extends AsyncTask<List<ParseUser>, Void, ArrayList<Friend>> {

        private FriendsManagerCallback callback;

        public FriendsFromParseTask(FriendsManagerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ArrayList<Friend> doInBackground(List<ParseUser>... params) {
            final ArrayList<Friend> friends = new ArrayList<>();
            List<ParseUser> objects = params[0];
            for (ParseUser user : objects) {
                if (user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                    continue;

                final Friend friend = new Friend();
                friend.id = user.getObjectId();
                friend.name = user.getUsername();
                if (user.containsKey("avatar")) {
                    ParseFile parseFile = (ParseFile) user.get("avatar");
                    friend.image = parseFile.getUrl();
                }

                ParseQuery<ParseObject> existingFriendsQuery = ParseQuery.getQuery("Friendship");
                existingFriendsQuery.whereContainsAll("userIds", Arrays.asList(ParseUser.getCurrentUser().getObjectId(), user.getObjectId()));
                try {
                    List<ParseObject> friendships = existingFriendsQuery.find();
                    if (friendships != null && friendships.size() > 0) {
                        ParseObject friendship = friendships.get(0);
                        friend.group = (String) friendship.get("group");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                friends.add(friend);
            }
            return friends;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends) {
            super.onPostExecute(friends);
            callback.done(friends, null);
        }
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
