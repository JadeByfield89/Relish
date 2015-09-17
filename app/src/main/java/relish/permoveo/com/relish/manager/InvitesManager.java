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

        ParseQuery<ParseObject> acceptedQuery = ParseQuery.getQuery("Invite");
        acceptedQuery.whereEqualTo("acceptedFriends", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> declinedQuery = ParseQuery.getQuery("Invite");
        declinedQuery.whereEqualTo("declinedFriends", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> invitedQuery = ParseQuery.getQuery("Invite");
        invitedQuery.whereEqualTo("invitedFriends", ParseUser.getCurrentUser().getObjectId());

        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(creatorQuery, acceptedQuery, declinedQuery, invitedQuery));
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

    public static void acceptInvite(Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = ParseObject.createWithoutData("Invite", invite.id);
        inviteObj.addUnique("acceptedFriends", ParseUser.getCurrentUser().getObjectId());
        inviteObj.removeAll("declinedFriends", Arrays.asList(new String[]{ParseUser.getCurrentUser().getObjectId()}));
        inviteObj.removeAll("invitedFriends", Arrays.asList(new String[]{ParseUser.getCurrentUser().getObjectId()}));

        InvitePerson current = invite.getPersonById(ParseUser.getCurrentUser().getObjectId());
        invite.accepted.add(current);
        invite.declined.remove(current);
        invite.invited.remove(current);

        inviteObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.done(true, null);
                } else {
                    callback.done(false, e);
                }
            }
        });
    }

    public static void declineInvite(Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = ParseObject.createWithoutData("Invite", invite.id);
        inviteObj.addUnique("declinedFriends", ParseUser.getCurrentUser().getObjectId());
        inviteObj.removeAll("acceptedFriends", Arrays.asList(new String[]{ParseUser.getCurrentUser().getObjectId()}));
        inviteObj.removeAll("invitedFriends", Arrays.asList(new String[]{ParseUser.getCurrentUser().getObjectId()}));

        InvitePerson current = invite.getPersonById(ParseUser.getCurrentUser().getObjectId());
        invite.declined.add(current);
        invite.accepted.remove(current);
        invite.invited.remove(current);

        inviteObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    callback.done(true, null);
                } else {
                    callback.done(false, e);
                }
            }
        });
    }

    public static void createInvite(final Invite invite, final InvitesManagerCallback callback) {
        new CreateInviteWIthContactsTask(callback).execute(invite);
    }

    private static class CreateInviteWIthContactsTask extends AsyncTask<Invite, Void, Invite> {

        private InvitesManagerCallback callback;

        public CreateInviteWIthContactsTask(InvitesManagerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Invite doInBackground(Invite... params) {
            Invite invite = params[0];
            for (int i = 0; i < invite.invited.size(); i++) {
                InvitePerson person = invite.invited.get(i);
                if (person instanceof Contact) {
                    Contact contact = (Contact) person;
                    ParseObject contactObj = new ParseObject("Contact");
                    if (!TextUtils.isEmpty(contact.email))
                        contactObj.put("contactEmail", contact.email);
                    if (!TextUtils.isEmpty(contact.number))
                        contactObj.put("contactNumber", contact.number);
                    if (!TextUtils.isEmpty(contact.image) && contact.imageFile != null) {
                        contactObj.put("avatar", contact.imageFile);
                    }
                    contactObj.put("contactName", contact.name);
                    try {
                        contactObj.save();
                        invite.invited.set(i, new Contact(contactObj.getObjectId()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return invite;
        }

        @Override
        protected void onPostExecute(Invite invite) {
            super.onPostExecute(invite);
            ParseObject inviteObj = new ParseObject("Invite");
            inviteObj.put("creatorId", ParseUser.getCurrentUser().getObjectId());
            inviteObj.put("mapSnapshot", invite.mapSnapshot);
            inviteObj.put("date", invite.date);
            inviteObj.put("time", invite.time);
            inviteObj.put("title", invite.title);
            inviteObj.put("placeName", invite.name);
            inviteObj.put("placeRating", invite.rating);
            inviteObj.put("address", invite.location.address);
            inviteObj.put("location", new ParseGeoPoint(invite.location.lat, invite.location.lng));
            ArrayList<String> invitedContacts = new ArrayList<>();
            ArrayList<String> invitedFriends = new ArrayList<>();
            for (InvitePerson person : invite.invited) {
                if (person instanceof Contact)
                    invitedContacts.add(person.id);
                else if (person instanceof Friend)
                    invitedFriends.add(person.id);
            }
            ArrayList<String> acceptedFriends = new ArrayList<>();
            acceptedFriends.add(ParseUser.getCurrentUser().getObjectId());
            inviteObj.addAllUnique("invitedContacts", invitedContacts);
            inviteObj.addAllUnique("invitedFriends", invitedFriends);
            inviteObj.addAllUnique("acceptedFriends", acceptedFriends);
            if (!TextUtils.isEmpty(invite.image))
                inviteObj.put("placeImage", invite.image);
            if (!TextUtils.isEmpty(invite.note))
                inviteObj.put("note", invite.note);
            if (!TextUtils.isEmpty(invite.phone))
                inviteObj.put("placePhone", invite.phone);
            if (!TextUtils.isEmpty(invite.url))
                inviteObj.put("placeUrl", invite.url);
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
    }

    private static class LoadInvitesWithFriendsTask extends AsyncTask<ArrayList<ParseObject>, Void, ArrayList<Invite>> {

        private InvitesManagerCallback callback;

        public LoadInvitesWithFriendsTask(InvitesManagerCallback callback) {
            this.callback = callback;
        }

        private ArrayList<InvitePerson> fetchUsers(ArrayList<String> objectIds) {
            ArrayList<InvitePerson> persons = new ArrayList<>();
            for (String objectId : objectIds) {
                try {
                    ParseUser user = ParseUser.getQuery().get(objectId);
                    Friend friend = new Friend();
                    friend.id = user.getObjectId();
                    friend.name = user.getUsername();
                    if (user.containsKey("avatar")) {
                        ParseFile parseFile = (ParseFile) user.get("avatar");
                        friend.image = parseFile.getUrl();
                    }
                    persons.add(friend);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return persons;
        }

        private ArrayList<InvitePerson> fetchContacts(ArrayList<String> objectIds) {
            ArrayList<InvitePerson> persons = new ArrayList<>();
            for (String objectId : objectIds) {
                try {
                    ParseQuery<ParseObject> contactQuery = ParseQuery.getQuery("Contact");
                    ParseObject contactObj = contactQuery.get(objectId);
                    Contact contact = new Contact();
                    contact.id = contactObj.getObjectId();
                    contact.name = contactObj.getString("contactName");
                    if (contactObj.containsKey("contactNumber"))
                        contact.number = contactObj.getString("number");
                    if (contactObj.containsKey("contactEmail"))
                        contact.email = contactObj.getString("email");
                    if (contactObj.containsKey("avatar")) {
                        ParseFile parseFile = (ParseFile) contactObj.get("avatar");
                        contact.image = parseFile.getUrl();
                    }
                    persons.add(contact);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return persons;
        }

        @Override
        protected ArrayList<Invite> doInBackground(ArrayList<ParseObject>... params) {
            ArrayList<ParseObject> parseObjects = params[0];
            ArrayList<Invite> invites = new ArrayList<>();
            for (ParseObject parseObject : parseObjects) {
                Invite invite = Invite.from(parseObject);

                // fetch invited contacts and friends
                invite.invited = new ArrayList<>();
                ArrayList<String> invitedIds = (ArrayList<String>) parseObject.get("invitedFriends");
                if (invitedIds != null) {
                    if (invitedIds.contains(ParseUser.getCurrentUser().getObjectId()))
                        invite.status = Invite.InviteStatus.PENDING;
                    invite.invited.addAll(fetchUsers(invitedIds));
                }

                ArrayList<String> invitedContacts = (ArrayList<String>) parseObject.get("invitedContacts");
                if (invitedContacts != null)
                    invite.invited.addAll(fetchContacts(invitedContacts));

                // fetch accepted contacts and friends
                invite.accepted = new ArrayList<>();
                ArrayList<String> acceptedIds = (ArrayList<String>) parseObject.get("acceptedFriends");
                if (acceptedIds != null) {
                    if (acceptedIds.contains(ParseUser.getCurrentUser().getObjectId()))
                        invite.status = Invite.InviteStatus.ACCEPTED;
                    invite.accepted.addAll(fetchUsers(acceptedIds));
                }

                ArrayList<String> acceptedContacts = (ArrayList<String>) parseObject.get("acceptedContacts");
                if (acceptedContacts != null)
                    invite.accepted.addAll(fetchContacts(acceptedContacts));

                // fetch declined contacts and friends
                invite.declined = new ArrayList<>();
                ArrayList<String> declinedIds = (ArrayList<String>) parseObject.get("declinedFriends");
                if (declinedIds != null) {
                    if (declinedIds.contains(ParseUser.getCurrentUser().getObjectId()))
                        invite.status = Invite.InviteStatus.DECLINED;
                    invite.declined.addAll(fetchUsers(declinedIds));
                }

                ArrayList<String> declinedContacts = (ArrayList<String>) parseObject.get("declinedContacts");
                if (declinedContacts != null)
                    invite.declined.addAll(fetchContacts(declinedContacts));

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
