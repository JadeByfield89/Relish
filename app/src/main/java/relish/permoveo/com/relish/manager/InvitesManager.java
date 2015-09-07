package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.text.TextUtils;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;

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

    public static void createInvite(final Invite invite, final InvitesManagerCallback callback) {
        ParseObject inviteObj = new ParseObject("Invite");
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
}