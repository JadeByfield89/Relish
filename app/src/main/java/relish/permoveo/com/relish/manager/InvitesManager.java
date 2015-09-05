package relish.permoveo.com.relish.manager;

import android.content.Context;
import android.text.TextUtils;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import relish.permoveo.com.relish.model.Invite;

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

    public static void createInvite(final Invite invite, InvitesManagerCallback callback) {
        ParseObject inviteObj = new ParseObject("Invite");
        inviteObj.add("date", invite.date);
        inviteObj.add("time", invite.time);
        inviteObj.add("title", invite.title);
        inviteObj.add("placeName", invite.name);
        inviteObj.add("address", invite.location.address);
        inviteObj.add("location", new ParseGeoPoint(invite.location.lat, invite.location.lng));
        for (INv)

//        inviteObj.addAllUnique("invited", inv);
        if (!TextUtils.isEmpty(invite.note))
            inviteObj.add("note", invite.note);
    }
}
