package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 8/25/15.
 */
public class SMSVerificationActivity extends RelishActivity {


    @Bind(R.id.verify_phone_number)
    Button verifyPhoneNumber;

    @Bind(R.id.sms_sub_one)
    TextView smsSubText;


    private AuthCallback authCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_validation);
        ButterKnife.bind(this);

        smsSubText.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession digitsSession, String number) {
                sendPhoneNumberToParse(number);
                Toast.makeText(getBaseContext(), "Success -> " + digitsSession.getAuthToken(), Toast.LENGTH_SHORT).show();
                Log.d("SMSVerification", "Number -> " + number);
            }

            @Override
            public void failure(DigitsException e) {
                Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        };

        verifyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Digits.authenticate(authCallback, R.style.Theme_MyTheme);
            }
        });

    }

    private void sendPhoneNumberToParse(final String number){
        try{
            final ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("phoneNumber", number);
            currentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        // Start MainActivity ONLY once parse user account was created successfully
                        // and phone number has been verified via Digits and saved to Parse
                        FriendsManager.findContact(currentUser.getEmail(), number, new FriendsManager.FriendsManagerCallback<Contact, ParseException>() {
                            @Override
                            public void done(final Contact contact, ParseException e) {
                                if (e == null) {
                                    if (contact == null) {
                                        // user wasn't associated with Contacts table, so he hasn't any invites, just go next
                                        next(false);
                                    } else {
                                        // user was associated with Contacts table, so we will try to find invites associated with his contact id
                                        InvitesManager.findInvites(contact.id, new InvitesManager.InvitesManagerCallback<ArrayList<Invite>, ParseException>() {
                                            @Override
                                            public void done(ArrayList<Invite> invites, ParseException e) {
                                                if (e == null) {
                                                    if (invites.size() == 0) {
                                                        // no invites associated with the user contact id (they were expired by date),
                                                        // so we will delete him from Contacts table
                                                        // and just go next
                                                        ParseObject.createWithoutData("Contact", contact.id).deleteInBackground();
                                                        next(false);
                                                    } else {
                                                        // some invites were found for the user contact id, so we will remove contact ids from ":contacts" lists
                                                        // and add user ids to ":friends" lists
                                                        // then again we will remove user from Contacts table and go next to Invites screen
                                                        new NewUserProcessTask(contact).execute(invites);
                                                    }
                                                } else {
                                                    Toast.makeText(SMSVerificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }
                                } else {
                                    Toast.makeText(SMSVerificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(SMSVerificationActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    private class NewUserProcessTask extends AsyncTask<ArrayList<Invite>, Void , Void>  {

        private Contact contact;

        public NewUserProcessTask(Contact contact) {
            this.contact = contact;
        }

        @Override
        protected Void doInBackground(ArrayList<Invite>... params) {
            ArrayList<Invite> invites = params[0];
            for (Invite invite : invites) {
                ParseObject inviteObj = ParseObject.createWithoutData("Invite", invite.id);
                inviteObj.removeAll("invitedContacts", Collections.singletonList(contact.id));
                inviteObj.removeAll("acceptedContacts", Collections.singletonList(contact.id));
                inviteObj.removeAll("declinedContacts", Collections.singletonList(contact.id));

                switch (invite.status) {
                    case PENDING:
                        inviteObj.addUnique("invitedFriends", ParseUser.getCurrentUser().getObjectId());
                        break;
                    case ACCEPTED:
                        inviteObj.addUnique("acceptedFriends", ParseUser.getCurrentUser().getObjectId());
                        break;
                    case DECLINED:
                        inviteObj.addUnique("declinedFriends", ParseUser.getCurrentUser().getObjectId());
                        break;
                }
                try {
                    inviteObj.save();

                    ParseObject friendship = new ParseObject("Friendship");
                    friendship.addAllUnique("userIds", Arrays.asList(invite.creatorId, ParseUser.getCurrentUser().getObjectId()));
                    friendship.save();

                    if (SharedPrefsUtil.get.lastVisibleFriendsCount() == -1) {
                        SharedPrefsUtil.get.setLastVisibleFriendsCount(1);
                    } else {
                        SharedPrefsUtil.get.setLastVisibleFriendsCount(SharedPrefsUtil.get.lastVisibleFriendsCount() + 1);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ParseObject.createWithoutData("Contact", contact.id).deleteInBackground();
            next(true);
        }
    }

    private void next(boolean toInviteList) {
        Intent intent = new Intent(SMSVerificationActivity.this, MainActivity.class);
        intent.putExtra(ConstantUtil.TO_INVITES_LIST, toInviteList);
        startActivity(intent);
        finish();
    }
}
