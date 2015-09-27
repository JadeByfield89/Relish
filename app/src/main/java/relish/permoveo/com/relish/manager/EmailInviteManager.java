package relish.permoveo.com.relish.manager;

import android.os.AsyncTask;
import android.util.Log;

import org.scribe.model.Request;
import org.scribe.model.Verb;

import relish.permoveo.com.relish.interfaces.OnInviteSentListener;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.UserUtils;

/**
 * Created by byfieldj on 9/18/15.
 * <p/>
 * This class handles sending invites via email
 */
public class EmailInviteManager {

    private static final String RELISH_SEND_EMAIL_URL = "http://www.relishwith.us/send_email.php";
    private Contact contact;
    private Invite invite;

    public EmailInviteManager(final Contact contact, final Invite invite) {
        this.contact = contact;
        this.invite = invite;
    }

    public void sendEmailInvite(final OnInviteSentListener listener) {
        new SendEmailInviteTask().execute();
    }

    private class SendEmailInviteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            // Try to send the invite to multiple people via email
            Log.d("EmailInviteManager", "SENT email to " + contact.email);
            Request request = new Request(Verb.POST, RELISH_SEND_EMAIL_URL);
            request.addBodyParameter("inviteTitle", invite.title);
            request.addBodyParameter("inviteDateTime", invite.getFormattedDateTime());
            request.addBodyParameter("placeName", invite.name);
            request.addBodyParameter("placeAddress", invite.location.address);
            request.addBodyParameter("inviteCreatorName", UserUtils.getFirstName());
            request.addBodyParameter("inviteGuestCount", "" + (invite.invited.size() - 1));
            request.addBodyParameter("inviteCode", invite.inviteId);
            Log.d("EmailInviteManager", "inviteCode -> " + invite.inviteId);
            request.addBodyParameter("contactEmail", contact.email);
            request.addBodyParameter("contactName", contact.name);

            request.send();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("EmailInviteFragment", "Sent emails!");
        }
    }
}
