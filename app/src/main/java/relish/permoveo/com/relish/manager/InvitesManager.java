package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.UserUtils;

/**
 * Created by rom4ek on 04.09.2015.
 */
public class InvitesManager {
    public static int mostRecentInviteId;
    public static int currentInviteId;
    private static Context context;
    private static Invite currentInvite;

    public static void initialize(Context aContext) {
        context = aContext;
    }

    public static void retrieveInvitesCount(final InvitesManagerCallback callback) {
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
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    callback.done(list == null ? 0 : list.size(), null);
                } else {
                    callback.done(null, e);
                }
            }
        });
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
                        new LoadInvitesWithFriendsTask(ParseUser.getCurrentUser().getObjectId(), callback).execute(new ArrayList<>(parseObjects));
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void findInvites(final String id, final InvitesManagerCallback callback) {
        ParseQuery<ParseObject> acceptedQuery = ParseQuery.getQuery("Invite");
        acceptedQuery.whereEqualTo("acceptedContacts", id);

        ParseQuery<ParseObject> declinedQuery = ParseQuery.getQuery("Invite");
        declinedQuery.whereEqualTo("declinedContacts", id);

        ParseQuery<ParseObject> invitedQuery = ParseQuery.getQuery("Invite");
        invitedQuery.whereEqualTo("invitedContacts", id);
        ParseQuery<ParseObject> query = ParseQuery.or(Arrays.asList(acceptedQuery, declinedQuery, invitedQuery));
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    ArrayList<Invite> invites = new ArrayList<>();
                    if (parseObjects == null || parseObjects.size() == 0) {
                        callback.done(invites, null);
                    } else {
                        new LoadInvitesWithFriendsTask(id, callback).execute(new ArrayList<>(parseObjects));
                    }
                } else {
                    callback.done(null, e);
                }
            }
        });
    }

    public static void acceptInvite(final Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = ParseObject.createWithoutData("Invite", invite.id);
        inviteObj.addUnique("acceptedFriends", ParseUser.getCurrentUser().getObjectId());
        inviteObj.removeAll("declinedFriends", Collections.singletonList(ParseUser.getCurrentUser().getObjectId()));
        inviteObj.removeAll("invitedFriends", Collections.singletonList(ParseUser.getCurrentUser().getObjectId()));

        InvitePerson current = invite.getPersonById(ParseUser.getCurrentUser().getObjectId());
        invite.accepted.add(current);
        invite.declined.remove(current);
        invite.invited.remove(current);

        inviteObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParsePush parsePush = new ParsePush();
                    ParseQuery pQuery = ParseInstallation.getQuery();
                    pQuery.whereContainedIn("userId", Collections.singletonList(invite.creatorId));
                    JSONObject pushData = new JSONObject();
                    try {
                        pushData.put(ConstantUtil.SENDER_IMAGE_KEY, UserUtils.getUserAvatar());
                        pushData.put("id", invite.id);
                        pushData.put("type", Invite.InviteType.RESPONSE.toString());
                        pushData.put("title", invite.title);
                        pushData.put("alert", String.format(context.getString(R.string.user_response), UserUtils.getUsername()));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    parsePush.setQuery(pQuery);
                    parsePush.setData(pushData);
                    parsePush.sendInBackground();

                    callback.done(true, null);
                } else {
                    callback.done(false, e);
                }
            }
        });
    }

    public static void declineInvite(final Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = ParseObject.createWithoutData("Invite", invite.id);
        inviteObj.addUnique("declinedFriends", ParseUser.getCurrentUser().getObjectId());
        inviteObj.removeAll("acceptedFriends", Collections.singletonList(ParseUser.getCurrentUser().getObjectId()));
        inviteObj.removeAll("invitedFriends", Collections.singletonList(ParseUser.getCurrentUser().getObjectId()));

        InvitePerson current = invite.getPersonById(ParseUser.getCurrentUser().getObjectId());
        invite.declined.add(current);
        invite.accepted.remove(current);
        invite.invited.remove(current);

        inviteObj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    ParsePush parsePush = new ParsePush();
                    ParseQuery pQuery = ParseInstallation.getQuery();
                    pQuery.whereContainedIn("userId", Collections.singletonList(invite.creatorId));
                    JSONObject pushData = new JSONObject();
                    try {
                        pushData.put(ConstantUtil.SENDER_IMAGE_KEY, UserUtils.getUserAvatar());
                        pushData.put("id", invite.id);
                        pushData.put("type", Invite.InviteType.RESPONSE.toString());
                        pushData.put("title", invite.title);
                        pushData.put("alert", String.format(context.getString(R.string.user_response), UserUtils.getUsername()));
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    parsePush.setQuery(pQuery);
                    parsePush.setData(pushData);
                    parsePush.sendInBackground();
                    callback.done(true, null);
                } else {
                    callback.done(false, e);
                }
            }
        });
    }

    public static void createInvite(final Invite invite, final InvitesManagerCallback callback) {
        currentInvite = invite;
        new CreateInviteWithContactsTask(callback).execute(invite);
    }

    public interface InvitesManagerCallback<T1, T2> {
        void done(T1 t1, T2 t2);
    }

    private static class CreateInviteWithContactsTask extends AsyncTask<Invite, Void, Invite> {

        private InvitesManagerCallback callback;

        public CreateInviteWithContactsTask(InvitesManagerCallback callback) {
            this.callback = callback;
        }

        @Override
        protected Invite doInBackground(Invite... params) {
            Invite invite = params[0];
            for (int i = 0; i < invite.invited.size(); i++) {
                InvitePerson person = invite.invited.get(i);
                if (person instanceof Contact) {
                    Contact contact = (Contact) person;
                    // check if the contact already exists
                    ParseQuery<ParseObject> phoneQuery = null;
                    if (!TextUtils.isEmpty(contact.number)) {
                        phoneQuery = ParseQuery.getQuery("Contact");
                        phoneQuery.whereEqualTo("contactNumber", contact.number);
                    }

                    ParseQuery<ParseObject> emailQuery = null;
                    if (!TextUtils.isEmpty(contact.email)) {
                        emailQuery = ParseQuery.getQuery("Contact");
                        emailQuery.whereEqualTo("contactEmail", contact.email);
                    }

                    ArrayList<ParseQuery<ParseObject>> queries = new ArrayList<>();
                    if (phoneQuery != null)
                        queries.add(phoneQuery);
                    if (emailQuery != null)
                        queries.add(emailQuery);

                    ParseQuery<ParseObject> query = ParseQuery.or(queries);
                    try {
                        List<ParseObject> result = query.find();
                        if (result != null && result.size() > 0) {
                            ParseObject contactObj = result.get(0);
                            person.id = contactObj.getObjectId();
                        } else {
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
                                person.id = contactObj.getObjectId();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            return invite;
        }

        @Override
        protected void onPostExecute(final Invite invite) {
            super.onPostExecute(invite);
            try {
                final ParseObject inviteObj = new ParseObject("Invite");
                inviteObj.put("creatorId", ParseUser.getCurrentUser().getObjectId());
                inviteObj.put("mapSnapshot", invite.mapSnapshot);
                inviteObj.put("date", invite.date);
                inviteObj.put("time", invite.time);
                inviteObj.put("title", invite.title);
                inviteObj.put("placeName", invite.name);
                inviteObj.put("placeRating", invite.rating);
                inviteObj.put("address", invite.location.address);
                inviteObj.put("location", new ParseGeoPoint(invite.location.lat, invite.location.lng));


                //inviteObj.put("inviteId", 0);
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

                            //Add invite to Google Calendar
                            /*if(SharedPrefsUtil.get.isGoogleCalendarSyncEnabled()){
                                CalendarEventManager manager = new CalendarEventManager(context, currentInvite);
                                manager.insertEventIntoCalender(new CalendarEventManager.OnEventInsertedListener() {
                                    @Override
                                    public void OnEventInserted(boolean succes) {
                                        if(succes) {
                                            Log.d("InvitesManager", "Invite added to calendar successfully");
                                        }else{
                                            Log.d("InvitesManager", "Error inserting event into calendar!");

                                        }
                                    }
                                });
                            }*/


                            if (SharedPrefsUtil.get.lastVisibleInvitesCount() == -1) {
                                SharedPrefsUtil.get.setLastVisibleInvitesCount(1);
                            } else {
                                SharedPrefsUtil.get.setLastVisibleInvitesCount(SharedPrefsUtil.get.lastVisibleInvitesCount() + 1);
                            }


                            /*ParseQuery<ParseObject> query = ParseQuery.getQuery("Invite");
                            query.orderByDescending("updatedAt");
                            query.getFirstInBackground(new GetCallback<ParseObject>() {
                                public void done(ParseObject object, ParseException e) {
                                    String inviteId;


                                    if (object == null) {
                                        Log.d("InvitesManager", "The getFirst request failed.");
                                        currentInviteId = mostRecentInviteId + 1;
                                        Log.d("InvitesManager", "Object is null, Latest Invite ID -> " + mostRecentInviteId);
                                        inviteObj.put("inviteId", currentInviteId);
                                        inviteObj.saveInBackground();

                                    } else {
                                        try {
                                            inviteId = object.get("inviteId").toString();
                                        } catch (Exception exception) {
                                            exception.printStackTrace();
                                        }
                                        // got the most recently modified object... do something with it here
                                        Log.d("InvitesManager", "Most recent invite ID -> " + object.get("inviteId").toString());
                                        mostRecentInviteId = Integer.parseInt(object.get("inviteId").toString());
                                        currentInviteId = mostRecentInviteId + 1;

                                        inviteObj.put("inviteId", currentInviteId);
                                        inviteObj.saveInBackground();
                                        Log.d("InvitesManager", "Latest Invite ID -> " + currentInviteId);
                                    }
                                }
                            });*/

                            // Get a count of all Invite objects currently in parse
                                /*ParseQuery<ParseObject> invitesQuery = ParseQuery.getQuery("Invite");
                                invitesQuery.countInBackground(new CountCallback() {
                                    public void done(int count, ParseException e) {
                                        if (e == null) {
                                            Log.d("InvitesManager", "Parse Invites count-> " + count);
                                            currentInviteId = count;

                                            inviteObj.put("inviteId", currentInviteId);

                                            inviteObj.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    callback.done(inviteObj.getObjectId(), null);

                                                }
                                            });
                                        }
                                    }
                                });*/


                            callback.done(inviteObj, null);

                        } else {
                            callback.done(null, e);
                        }
                    }
                });
            } catch (Exception e) {
                Toast.makeText(context, "An error has occured while sending this invite", Toast.LENGTH_LONG).show();
            }
        }
    }


    private static class LoadInvitesWithFriendsTask extends AsyncTask<ArrayList<ParseObject>, Void, ArrayList<Invite>> {

        private InvitesManagerCallback callback;
        private String userId;

        public LoadInvitesWithFriendsTask(String userId, InvitesManagerCallback callback) {
            this.callback = callback;
            this.userId = userId;
        }

        private ArrayList<InvitePerson> fetchUsers(ArrayList<String> objectIds) {
            ArrayList<InvitePerson> persons = new ArrayList<>();
            for (String objectId : objectIds) {
                try {
                    ParseUser user = ParseUser.getQuery().get(objectId);
                    Friend friend = new Friend();
                    friend.id = user.getObjectId();
                    friend.name = user.getString("fullName");
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
                        contact.number = contactObj.getString("contactNumber");
                    if (contactObj.containsKey("contactEmail"))
                        contact.email = contactObj.getString("contactEmail");
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
                    if (invitedIds.contains(userId))
                        invite.status = Invite.InviteStatus.PENDING;
                    invite.invited.addAll(fetchUsers(invitedIds));
                }

                ArrayList<String> invitedContacts = (ArrayList<String>) parseObject.get("invitedContacts");
                if (invitedContacts != null) {
                    if (invitedContacts.contains(userId))
                        invite.status = Invite.InviteStatus.PENDING;
                    invite.invited.addAll(fetchContacts(invitedContacts));
                }

                // fetch accepted contacts and friends
                invite.accepted = new ArrayList<>();
                ArrayList<String> acceptedIds = (ArrayList<String>) parseObject.get("acceptedFriends");
                if (acceptedIds != null) {
                    if (acceptedIds.contains(userId))
                        invite.status = Invite.InviteStatus.ACCEPTED;
                    invite.accepted.addAll(fetchUsers(acceptedIds));
                }

                ArrayList<String> acceptedContacts = (ArrayList<String>) parseObject.get("acceptedContacts");
                if (acceptedContacts != null) {
                    if (acceptedContacts.contains(userId))
                        invite.status = Invite.InviteStatus.ACCEPTED;
                    invite.accepted.addAll(fetchContacts(acceptedContacts));
                }

                // fetch declined contacts and friends
                invite.declined = new ArrayList<>();
                ArrayList<String> declinedIds = (ArrayList<String>) parseObject.get("declinedFriends");
                if (declinedIds != null) {
                    if (declinedIds.contains(userId))
                        invite.status = Invite.InviteStatus.DECLINED;
                    invite.declined.addAll(fetchUsers(declinedIds));
                }

                ArrayList<String> declinedContacts = (ArrayList<String>) parseObject.get("declinedContacts");
                if (declinedContacts != null) {
                    if (declinedContacts.contains(userId))
                        invite.status = Invite.InviteStatus.DECLINED;
                    invite.declined.addAll(fetchContacts(declinedContacts));
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

