package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.SharedPrefsUtil;

/**
 * Created by rom4ek on 04.09.2015.
 */
public class InvitesManager {
    private static Context context;

    public static void initialize(Context aContext) {
        context = aContext;
    }

    public interface InvitesManagerCallback<T1, T2> {
        void done(T1 t1, T2 t2);
    }

    public static void retrieveInvitesList(final InvitesManagerCallback callback) {
        ParseQuery<ParseObject> creatorQuery = ParseQuery.getQuery("Invite");
        creatorQuery.whereEqualTo("creatorId", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> friendsQuery = ParseQuery.getQuery("Invite");
        friendsQuery.whereEqualTo("invitedFriends", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(creatorQuery, friendsQuery));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<Invite> invites = new ArrayList<>();
                    if (parseObjects == null || parseObjects.size() == 0) {
                        callback.done(invites, null);
                    } else {
                        new LoadInvitesWithFriendsTask(callback).execute(new ArrayList<>(parseObjects));
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void createInvite(final Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = new ParseObject("Invite");
        inviteObj.put("creatorId", ParseUser.getCurrentUser().getObjectId());
        inviteObj.put("mapSnapshot", invite.mapSnapshot);
        inviteObj.put("date", invite.date);
        inviteObj.put("time", invite.time);
        inviteObj.put("title", invite.title);
        inviteObj.put("placeName", invite.name);
        inviteObj.put("address", invite.location.address);
        inviteObj.put("location", new ParseGeoPoint(invite.location.lat, invite.location.lng));
        ArrayList<String> invitedContacts = new ArrayList<>();
        ArrayList<String> invitedFriends = new ArrayList<>();
        for (InvitePerson person : invite.invited) {
            if (person instanceof Contact)
                invitedContacts.add(person.number);
            else if (person instanceof Friend)
                invitedFriends.add(((Friend) person).id);
        }
        inviteObj.addAllUnique("invitedContacts", invitedContacts);
        inviteObj.addAllUnique("invitedFriends", invitedFriends);
        if (!TextUtils.isEmpty(invite.image))
            inviteObj.put("image", invite.image);
        if (!TextUtils.isEmpty(invite.note))
            inviteObj.put("note", invite.note);

        inviteObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (SharedPrefsUtil.get.lastVisibleInvitesCount() == -1) {
                        SharedPrefsUtil.get.setLastVisibleInvitesCount(1);
                    } else {
                        SharedPrefsUtil.get.setLastVisibleInvitesCount(SharedPrefsUtil.get.lastVisibleFriendsCount() + 1);
                    }
                    callback.done(null, null);
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    private static class LoadInvitesWithFriendsTask extends AsyncTask<ArrayList<ParseObject>, Void, ArrayList<Invite>> {

        private InvitesManagerCallback callback;

        public LoadInvitesWithFriendsTask(InvitesManagerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected ArrayList<Invite> doInBackground(ArrayList<ParseObject>... params) {
            ArrayList<ParseObject> parseObjects = params[0];
            ArrayList<Invite> invites = new ArrayList<>();
            for (ParseObject parseObject : parseObjects) {
                Invite invite = Invite.from(parseObject);

                ArrayList<String> invitedIds = (ArrayList<String>) parseObject.get("invitedFriends");
                invite.invited = new ArrayList<>();
                for (String objectId : invitedIds) {
                    try {
                        ParseUser user = ParseUser.getQuery().get(objectId);
                        Friend friend = new Friend();
                        friend.id = user.getObjectId();
                        friend.name = user.getUsername();
                        if (user.containsKey("avatar")) {
                            ParseFile parseFile = (ParseFile) user.get("avatar");
                            friend.image = parseFile.getUrl();
                        }
                        invite.invited.add(friend);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                ArrayList<String> acceptedIds = (ArrayList<String>) parseObject.get("acceptedFriends");
                ;
                invite.accepted = new ArrayList<>();
                if (acceptedIds != null) {
                    for (String objectId : acceptedIds) {
                        try {
                            ParseUser user = ParseUser.getQuery().get(objectId);
                            Friend friend = new Friend();
                            friend.id = user.getObjectId();
                            friend.name = user.getUsername();
                            if (user.containsKey("avatar")) {
                                ParseFile parseFile = (ParseFile) user.get("avatar");
                                friend.image = parseFile.getUrl();
                            }
                            invite.accepted.add(friend);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                ArrayList<String> declinedIds = (ArrayList<String>) parseObject.get("declinedFriends");
                invite.declined = new ArrayList<>();
                if (declinedIds != null) {
                    for (String objectId : declinedIds) {
                        try {
                            ParseUser user = ParseUser.getQuery().get(objectId);
                            Friend friend = new Friend();
                            friend.id = user.getObjectId();
                            friend.name = user.getUsername();
                            if (user.containsKey("avatar")) {
                                ParseFile parseFile = (ParseFile) user.get("avatar");
                                friend.image = parseFile.getUrl();
                            }
                            invite.declined.add(friend);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                invites.add(invite);
            }
            return invites;
        }

        @Override
        protected void onPostExecute(ArrayList<Invite> invites) {
            super.onPostExecute(invites);
            callback.done(invites, null);
        }
    }
}
