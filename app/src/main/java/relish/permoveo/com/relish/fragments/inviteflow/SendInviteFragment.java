package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joooonho.SelectableRoundedImageView;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.OnInviteSentListener;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.manager.CalendarEventManager;
import relish.permoveo.com.relish.manager.EmailInviteManager;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.manager.TwitterInviteManager;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.FlurryConstantUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TwilioSmsManager;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.util.UserUtils;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendInviteFragment extends Fragment implements RenderCallbacks {

    @Bind(R.id.invite_send_date_desc)
    TextView sendDateDesc;

    @Bind(R.id.invite_send_time_desc)
    TextView sendTimeDesc;

    @Bind(R.id.invite_send_sendto_desc)
    TextView sendSendToDesc;

    @Bind(R.id.send_invite_note)
    EditText sendNote;

    @Bind(R.id.invite_send_date)
    TextView sendDate;

    @Bind(R.id.invite_send_time)
    TextView sendTime;

    @Bind(R.id.first_person)
    CircleImageView firstPerson;

    @Bind(R.id.second_person)
    CircleImageView secondPerson;

    @Bind(R.id.third_person)
    CircleImageView thirdPerson;

    @Bind(R.id.more_persons)
    TextView morePersons;

    @Bind(R.id.button_send)
    Button sendButton;

    @Bind(R.id.invite_send_note_container)
    RelativeLayout sendNoteContainer;

    @Bind(R.id.invite_send_card)
    RelativeLayout inviteSendCard;

    @Bind(R.id.invite_send_root)
    RelativeLayout inviteSendRoot;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progressBar;

    @Bind(R.id.invite_layout)
    LinearLayout inviteLayout;

    @Bind(R.id.map_snapshot)
    SelectableRoundedImageView mapSnapshot;


    private InviteCreator creator;
    private GoogleMap mMap;
    private Marker placeMarker;
    private OnInviteSentListener mListener;

    public SendInviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InviteCreator)
            creator = (InviteCreator) activity;
        if (activity instanceof OnInviteSentListener)
            mListener = (OnInviteSentListener) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_invite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setDescriptionTypeface();
        setTypeface();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendButton.setText("");
                sendButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                InvitesManager.createInvite(creator.getInvite(), new InvitesManager.InvitesManagerCallback<ParseObject, ParseException>() {
                    @Override
                    public void done(final ParseObject inviteObject, ParseException e) {
                        if (e == null) {
                            if (isAdded())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (creator.getInvite().reminder != 0) {
                                            PendingIntent pintent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("relish.permoveo.com.relish.REMINDER")
                                                    .putExtra(ConstantUtil.INVITE_EXTRA, creator.getInvite()), 0);
                                            AlarmManager manager = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));

                                            DateTime time = new DateTime().withMillis(creator.getInvite().time);
                                            DateTime date = new DateTime().withMillis(creator.getInvite().date);
                                            DateTime when = new DateTime()
                                                    .withYear(date.getYear())
                                                    .withMonthOfYear(date.getMonthOfYear())
                                                    .withDayOfMonth(date.getDayOfMonth())
                                                    .withHourOfDay(time.getHourOfDay())
                                                    .withMinuteOfHour(time.getMinuteOfHour());
                                            manager.set(AlarmManager.RTC_WAKEUP, when.getMillis() - creator.getInvite().reminder * 1000, pintent);
                                        }

                                        String objectId = inviteObject.getObjectId();
                                        String idSuffix = objectId.substring(objectId.length() - 3, objectId.length());
                                        Log.d("SendInviteFragment", "Invite Id -> " + idSuffix);
                                        idSuffix = idSuffix.toUpperCase();
                                        inviteObject.put("inviteId", idSuffix);
                                        inviteObject.saveInBackground();

                                        sendInviteViaSMS(idSuffix);
                                        sendInviteViaTwitter(idSuffix);
                                        sendInviteViaEmail(idSuffix);
                                        sendInviteViaPush(inviteObject);

                                        //if (SharedPrefsUtil.get.isGoogleCalendarSyncEnabled()) {
                                        CalendarEventManager.get.insertEventIntoCalender(creator.getInvite(), new CalendarEventManager.OnEventInsertedListener() {
                                            @Override
                                            public void OnEventInserted(boolean succes) {

                                            }
                                        });
                                        //}

                                        startSendAnimation(inviteSendRoot);

                                    }


                                });

                            FlurryAgent.logEvent(FlurryConstantUtil.EVENT.INVITE_SENT);
                        } else

                        {

                            if (isAdded()) {
                                sendButton.setText(getString(R.string.invite_send));
                                sendButton.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(inviteSendCard, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }


                    }
                });
            }
        });
    }


    private void sendInviteViaSMS(String idSuffix) {
        // SEND VIA SMS IF PHONE CONTACTS WERE SELECTED
        TwilioSmsManager manager = new TwilioSmsManager();
        for (InvitePerson person : creator.getInvite().invited) {
            if (person instanceof Contact) {
                if (!TextUtils.isEmpty(person.number)) {
                    String senderName = UserUtils.getFullName();
                    String smsMessage = String.format(getString(R.string.share_sms_message),
                            senderName, creator.getInvite().name, creator.getInvite().getFormattedDate(), creator.getInvite().getFormattedTime(), idSuffix, idSuffix, idSuffix);

                    if (!TextUtils.isEmpty(person.number)) {
                        manager.sendInviteSmsViaTwilio(person.number, smsMessage);
                    } else {
                        Log.d("SendInviteFragment", "Can't send SMS, contact number is empty");
                    }
                }
            }
        }
    }

    private void sendInviteViaTwitter(String inviteId) {
        final int maxCharCount = 140;
        //SENT VIA TWITTER IF TWITTER CONTACT WAS SELECTED
        TwitterInviteManager twitterManager = new TwitterInviteManager();
        for (InvitePerson person : creator.getInvite().invited) {
            if (person instanceof Contact) {
                if (TextUtils.isEmpty(person.number) && TextUtils.isEmpty(((Contact) person).email) && !TextUtils.isEmpty(((Contact) person).twitterUsername)) {
                    String inviteMessage = "Hey " + ((Contact) person).twitterUsername + ", " + "@" + SharedPrefsUtil.get.getTwitterUsername() + " has invited you to join them at " + creator.getInvite().name + ", on " + creator.getInvite().getFormattedDateTime() + " Invite Code: " + inviteId;

                    if (inviteMessage.length() > maxCharCount) {
                        String[] name_sections = creator.getInvite().name.split(" ");
                        if (name_sections.length > 2) {
                            inviteMessage = "Hey " + ((Contact) person).twitterUsername + ", " + "@" + SharedPrefsUtil.get.getTwitterUsername() + " has invited you to join them at " + name_sections[0] + ", on " + creator.getInvite().getFormattedDateTime() + " Invite Code: " + inviteId;
                            twitterManager.sendTwitterInvite(inviteMessage);

                        }
                    } else {
                        twitterManager.sendTwitterInvite(inviteMessage);

                    }
                }
            }
        }
    }

    private void sendInviteViaEmail(String idSuffix) {
        // SEND INVITE VIA EMAIL IF EMAIL CONTACTS WERE SELECTED
        for (InvitePerson person : creator.getInvite().invited) {
            if (person instanceof Contact) {
                if (TextUtils.isEmpty(person.number) && !TextUtils.isEmpty(((Contact) person).email)) {
                    Log.d("SendInviteFragment", "Sending invite to " + ((Contact) person).email);
                    creator.getInvite().inviteId = idSuffix;
                    EmailInviteManager emailInviteManager = new EmailInviteManager((Contact) person, creator.getInvite());
                    emailInviteManager.sendEmailInvite(new OnInviteSentListener() {
                        @Override
                        public void onInviteSent(boolean success) {

                        }
                    });

                }
            }

        }
    }

    private void sendInviteViaPush(final ParseObject inviteObject) {
        ArrayList<String> friendsIds = new ArrayList<>();
        for (
                InvitePerson person
                : creator.getInvite().invited)

        {
            if (person instanceof Friend)
                friendsIds.add(((Friend) person).id);
        }


        ParsePush parsePush = new ParsePush();
        ParseQuery pQuery = ParseInstallation.getQuery();
        pQuery.whereContainedIn("userId", friendsIds);
        JSONObject pushData = new JSONObject();
        try

        {
            pushData.put(ConstantUtil.SENDER_IMAGE_KEY, UserUtils.getUserAvatar());
            pushData.put("id", inviteObject.getObjectId());
            pushData.put("type", Invite.InviteType.RECEIVED.toString());
            pushData.put("title", creator.getInvite().title);
            pushData.put("alert", String.format(getString(R.string.share_push_message),
                    UserUtils.getFirstName(), creator.getInvite().name, creator.getInvite().getFormattedDate(), creator.getInvite().getFormattedTime()));
        } catch (
                JSONException e1
                )

        {
            e1.printStackTrace();
        }

        parsePush.setQuery(pQuery);
        parsePush.setData(pushData);
        parsePush.sendInBackground();
    }


    private void startSendAnimation(View view) {
        YoYo.with(Techniques.ZoomOutRight).duration(500).withListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                mListener.onInviteSent(true);
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        }).playOn(view);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
        if (mMap != null) {
            setUpMap();
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(false);

        if (creator.getInvite().location != null) {
            if (placeMarker == null) {
                placeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng), 18.0f));
            }
        } else {
            if (GPSTracker.get.getLocation() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude()), 18.0f));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        setUpMapIfNeeded();
    }

    @Override
    public void render() {
        if (creator.getInvite() != null) {
            sendDate.setText(creator.getInvite().getFormattedDate());
            sendTime.setText(creator.getInvite().getFormattedTime());

            if (!TextUtils.isEmpty(creator.getInvite().note)) {
                sendNoteContainer.setVisibility(View.VISIBLE);
                sendNote.setText(creator.getInvite().note);
            } else {
                sendNoteContainer.setVisibility(View.GONE);
            }

            Picasso.with(getActivity())
                    .load(creator.getInvite().mapSnapshot)
                    .into(mapSnapshot);

            sendButton.setText(creator.getInvite().isSent ? getString(R.string.invite_update) : getString(R.string.invite_send));

//            if (!TextUtils.isEmpty(creator.getInvite().mapSnapshot)) {
//                snapshot.setVisibility(View.VISIBLE);
//                Picasso.with(getActivity())
//                        .load(new File(creator.getInvite().mapSnapshot))
//                        .into(snapshot);
//            } else {
//            }

            ArrayList<String> avatars = new ArrayList<>();
            for (InvitePerson person : creator.getInvite().invited) {
                if (!TextUtils.isEmpty(person.image) && avatars.size() < 3) {
                    avatars.add(person.image);
                }
            }
            renderInvited(avatars);
        } else {
            getActivity().finish();
        }
    }

    private void renderInvited(ArrayList<String> avatars) {
        inviteLayout.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);
        if (avatars.size() == 0) {
            firstPerson.setVisibility(View.GONE);
            secondPerson.setVisibility(View.GONE);
            thirdPerson.setVisibility(View.GONE);
            morePersons.setVisibility(View.VISIBLE);
            morePersons.setText(creator.getInvite().invited.size() +
                    " " + getResources().getQuantityString(R.plurals.persons, creator.getInvite().invited.size()));
        } else if (avatars.size() == 1) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.GONE);
            thirdPerson.setVisibility(View.GONE);
            if (creator.getInvite().invited.size() - 1 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 1) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 2) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(secondPerson);

            thirdPerson.setVisibility(View.GONE);
            if (creator.getInvite().invited.size() - 2 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 2) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 3) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(secondPerson);

            thirdPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(2))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(thirdPerson);

            if (creator.getInvite().invited.size() - 3 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 3) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        }
    }

    private void setDescriptionTypeface() {
        sendDateDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        sendSendToDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        sendTimeDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
    }

    private void setTypeface() {
        sendNote.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        morePersons.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        sendDate.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        sendTime.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
    }

    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(getActivity(), 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "SMS sent",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getActivity(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getActivity(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getActivity(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getActivity(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getActivity(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getActivity(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }
}

