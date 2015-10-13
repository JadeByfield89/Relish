package relish.permoveo.com.relish.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;

import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.InvitePagerAdapter;
import relish.permoveo.com.relish.interfaces.ContactsLoader;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.OnInviteSentListener;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.manager.EmailInviteManager;
import relish.permoveo.com.relish.manager.TwitterInviteManager;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.util.BlurBehind;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.FixedSpeedScroller;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TwilioSmsManager;
import relish.permoveo.com.relish.util.UserUtils;
import relish.permoveo.com.relish.view.NonSwipeableViewPager;

public class EditInviteActivity extends RelishActivity implements PagerCallbacks, InviteCreator, OnInviteSentListener {

    private static final int CONTACTS_PERMISSION_REQUEST = 222;
    public static final String INVITE_EXTRA = "extra_invite";

    @Bind(R.id.pager_invite)
    NonSwipeableViewPager invitePager;
    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
//    @Bind(R.id.pager_container)
//    RelativeLayout pagerContainer;

    private InvitePagerAdapter invitePagerAdapter;
    private int currentStep = 0;
    private String[] TITLES;
    private Invite invite;
    private long oldDate;
    private long oldTime;
    private YelpPlace.PlaceLocation oldLocation;
    private ArrayList<InvitePerson> oldFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_invite);
        ButterKnife.bind(this);

        BlurBehind.getInstance().setBackground(this);

        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(invitePager.getContext(), new LinearInterpolator());
            mScroller.set(invitePager, scroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
        }

        if (savedInstanceState != null) {
            currentStep = savedInstanceState.getInt("current_step");
            invite = (Invite) savedInstanceState.getSerializable("current_invite");
        }

        TITLES = new String[]{getString(R.string.invite_first_step),
                getString(R.string.invite_second_step),
                getString(R.string.invite_third_step),
                getString(R.string.invite_fourth_step)};
        updateToolbar(toolbar);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.invite_first_step));
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        invite = (Invite) getIntent().getSerializableExtra(INVITE_EXTRA);
        invite.isSent = true;
        oldDate = invite.date;
        oldTime = invite.time;
        oldLocation = invite.location;
        oldFriends = new ArrayList<>();
        oldFriends.addAll(invite.invited);
        for (InvitePerson person : invite.accepted) {
            if (!person.id.equals(ParseUser.getCurrentUser().getObjectId())) {
                oldFriends.add(person);
            }
        }

        invitePagerAdapter = new InvitePagerAdapter(getSupportFragmentManager(), null);
        invitePagerAdapter.setSource(0);
        invitePagerAdapter.updateMode();
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);
        invitePager.setOffscreenPageLimit(4);

        pagerIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(TITLES[position]);
                currentStep = position;
                if (getActiveFragment(invitePager, currentStep) instanceof RenderCallbacks)
                    ((RenderCallbacks) getActiveFragment(invitePager, currentStep)).render();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                        CONTACTS_PERMISSION_REQUEST);
            }
        }

//        YoYo.with(Techniques.SlideInUp)
//                .delay(1000)
//                .withListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                        invitePager.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                })
//                .playOn(invitePager);
    }

    protected static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    protected Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return getSupportFragmentManager().findFragmentByTag(name);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("current_step", currentStep);
        outState.putSerializable("current_invite", invite);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            previous();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Invite getInvite() {
        return invite;
    }

    @Override
    public void updateInviteWithGooglePlace(GooglePlace place) {
        this.invite.updateWithGooglePlace(place);
    }

    @Override
    public void onInviteSent(final boolean success, final Invite invite) {
        if (oldTime != invite.time || oldDate != invite.date || !oldLocation.address.equals(invite.location.address)) {
            notifyOldUsers();
        }
        notifyNewUsers();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void next() {
        invitePager.setCurrentItem(invitePager.getCurrentItem() + 1, true);
    }

    @Override
    public void previous() {
        if (invitePager.getCurrentItem() == 0) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            invitePager.setCurrentItem(invitePager.getCurrentItem() - 1, true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

        Log.d("InviteFlowAcitivty", "InviteflowActivity onActivityResult");
    }

    private void notifyOldUsers() {
        ArrayList<String> friendsIds = new ArrayList<>();
        for (InvitePerson person : oldFriends) {
            if (person instanceof Friend)
                friendsIds.add(person.id);
        }

        ParsePush parsePush = new ParsePush();
        ParseQuery pQuery = ParseInstallation.getQuery();
        pQuery.whereContainedIn("userId", friendsIds);
        JSONObject pushData = new JSONObject();
        try {
            pushData.put(ConstantUtil.SENDER_IMAGE_KEY, UserUtils.getUserAvatar());
            pushData.put("id", invite.id);
            pushData.put("type", Invite.InviteType.UPDATE.toString());
            pushData.put("title", invite.title);
            pushData.put("alert", String.format(getString(R.string.alert_update_push_message),
                    UserUtils.getFirstName()));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        parsePush.setQuery(pQuery);
        parsePush.setData(pushData);
        parsePush.sendInBackground();
    }

    private void notifyNewUsers() {
        // SEND VIA SMS IF PHONE CONTACTS WERE SELECTED
        TwilioSmsManager manager = new TwilioSmsManager();
        for (InvitePerson person : invite.invited) {
            if (person instanceof Contact && !containsPerson(person)) {
                if (!TextUtils.isEmpty(person.number)) {
                    String senderName = UserUtils.getFullName();
                    String smsMessage = String.format(getString(R.string.share_sms_message),
                            senderName, invite.name, invite.getFormattedDate(), invite.getFormattedTime(), invite.inviteId, invite.inviteId, invite.inviteId);

                    if (!TextUtils.isEmpty(person.number)) {
                        manager.sendInviteSmsViaTwilio(person.number, smsMessage);
                    } else {
                        Log.d("SendInviteFragment", "Can't send SMS, contact number is empty");
                    }
                }
            }
        }

        // SEND INVITE VIA EMAIL IF EMAIL CONTACTS WERE SELECTED
        for (InvitePerson person : invite.invited) {
            if (person instanceof Contact && !containsPerson(person)) {
                if (TextUtils.isEmpty(person.number) && !TextUtils.isEmpty(((Contact) person).email)) {
                    Log.d("SendInviteFragment", "Sending invite to " + ((Contact) person).email);
                    EmailInviteManager emailInviteManager = new EmailInviteManager((Contact) person, invite);
                    emailInviteManager.sendEmailInvite(new OnInviteSentListener() {
                        @Override
                        public void onInviteSent(final boolean success, final Invite invite) {

                        }
                    });

                }
            }

        }

        TwitterInviteManager twitterManager = new TwitterInviteManager();
        for (InvitePerson person : invite.invited) {
            if (person instanceof Contact) {
                if (TextUtils.isEmpty(person.number) && TextUtils.isEmpty(((Contact) person).email) && !TextUtils.isEmpty(((Contact) person).twitterUsername)) {
                    String inviteMessage = ((Contact) person).twitterUsername + ", " + "@" + SharedPrefsUtil.get.getTwitterUsername() + " has invited you to lunch!";
                    twitterManager.sendTwitterInvite(inviteMessage);
                }
            }
        }

        // SENDING INVITE VIA PUSH NOTIFICATIONS
        ArrayList<String> friendsIds = new ArrayList<>();
        for (InvitePerson person : invite.invited) {
            if (person instanceof Friend && !containsPerson(person))
                friendsIds.add(((Friend) person).id);
        }

        ParsePush parsePush = new ParsePush();
        ParseQuery pQuery = ParseInstallation.getQuery();
        pQuery.whereContainedIn("userId", friendsIds);
        JSONObject pushData = new JSONObject();
        try {
            pushData.put(ConstantUtil.SENDER_IMAGE_KEY, UserUtils.getUserAvatar());
            pushData.put("id", invite.id);
            pushData.put("type", Invite.InviteType.RECEIVED.toString());
            pushData.put("title", invite.title);
            pushData.put("alert", String.format(getString(R.string.share_push_message),
                    UserUtils.getFirstName(), invite.name, invite.getFormattedDate(), invite.getFormattedTime()));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        parsePush.setQuery(pQuery);
        parsePush.setData(pushData);
        parsePush.sendInBackground();
    }

    private boolean containsPerson(InvitePerson needed) {
        for (InvitePerson person : oldFriends) {
            if (person.id.equals(needed.id))
                return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CONTACTS_PERMISSION_REQUEST:
                for (int i = 0; i < invitePagerAdapter.getCount(); i++) {
                    String name = makeFragmentName(invitePager.getId(), i);
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(name);
                    if (fragment != null && fragment instanceof ContactsLoader) {
                        ((ContactsLoader) fragment).loadContactsWithPermission();
                    }
                }
                break;
        }
    }
}
