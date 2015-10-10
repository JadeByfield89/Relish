package relish.permoveo.com.relish.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.viewpagerindicator.CirclePageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.FakeInvitePagerAdapter;
import relish.permoveo.com.relish.fragments.FriendsFragment;
import relish.permoveo.com.relish.fragments.InvitesFragment;
import relish.permoveo.com.relish.fragments.NavigationDrawerFragment;
import relish.permoveo.com.relish.fragments.PlacesFilterFragment;
import relish.permoveo.com.relish.fragments.PlacesFragment;
import relish.permoveo.com.relish.fragments.SettingsFragment;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.CircularRevealAnimator;
import relish.permoveo.com.relish.interfaces.NavigationDrawerManagementCallbacks;
import relish.permoveo.com.relish.interfaces.OnResumeLoadingCallbacks;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.util.ConnectionUtil;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.DialogUtil;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.view.RelishDrawerToggle;


public class MainActivity extends RelishActivity implements CircularRevealAnimator, NavigationDrawerFragment.NavigationDrawerCallbacks, ToolbarCallbacks, NavigationDrawerManagementCallbacks, PlacesFilterFragment.OnFilterSelectionCompleteListener {

    private static final int LOCATION_PERMISSION_REQUEST = 111;

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    public boolean drawerOpen = false;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.content_frame)
    FrameLayout contentFrame;
    @Bind(R.id.pager_invite)
    ViewPager invitePager;
    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @Bind(R.id.invite_share_card)
    CardView inviteShareCard;
    @Bind(R.id.main_activity_container)
    RelativeLayout activity_container;
    @Bind(R.id.reveal_container)
    RelativeLayout reveal_container;
    RelishDrawerToggle drawerToggle;
    Fragment current = null;
    Dialog d;
    NavigationDrawerFragment navDrawer;
    PlacesFilterFragment filterFragment;
    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (d != null && d.isShowing())
                d.dismiss();

            hideLoader();
            if (current != null && current instanceof OnResumeLoadingCallbacks && current.isAdded()) {
                ((OnResumeLoadingCallbacks) current).loadData(false, false);
            }
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
        }
    };
    private FakeInvitePagerAdapter invitePagerAdapter;
    private int mCurrentSelectedPosition = 0;
    // nav drawer title
    private CharSequence mDrawerTitle;
    // used to store app title
    private CharSequence mTitle;
    private String[] navMenuTitles;
    private String inviteId = null;
    private boolean action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getIntent().getBooleanExtra(ConstantUtil.TO_INVITES_LIST, false)) {
            mCurrentSelectedPosition = 1;
            int notificationId = getIntent().getIntExtra(ConstantUtil.NOTIFICATION_ID_EXTRA, -1);
            if (notificationId != -1) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
            }

            if (getIntent().getExtras().containsKey(ConstantUtil.NOTIFICATION_ACTION_EXTRA)) {
                inviteId = getIntent().getExtras().getString(ConstantUtil.INVITE_ID_EXTRA);
                action = getIntent().getBooleanExtra(ConstantUtil.NOTIFICATION_ACTION_EXTRA, false);
            }
        } else if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        setSupportActionBar(toolbar);
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navDrawer = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        filterFragment = (PlacesFilterFragment) getSupportFragmentManager().findFragmentById(R.id.filter_drawer);

        drawerLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    drawerLayout.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    drawerLayout.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                DisplayMetrics metrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(metrics);
                float widthDp = metrics.widthPixels / metrics.density;
                float heightDp = metrics.heightPixels / metrics.density;
                float smallestWidth = Math.min(widthDp, heightDp);

                navDrawer.getView().getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, smallestWidth, metrics) - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56.0f, getResources().getDisplayMetrics());
                navDrawer.getView().requestLayout();

                filterFragment.getView().getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, smallestWidth, metrics) - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56.0f, getResources().getDisplayMetrics());
                filterFragment.getView().requestLayout();
            }
        });


        mTitle = mDrawerTitle = getTitle();


        drawerToggle = new RelishDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                drawerOpen = false;
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                drawerOpen = true;
                invalidateOptionsMenu();
                FriendsManager.retrieveFriendsCount(new FriendsManager.FriendsManagerCallback<Integer, ParseException>() {
                    @Override
                    public void done(Integer count, ParseException e) {
                        if (e == null && drawerOpen) {
                            if (SharedPrefsUtil.get.lastVisibleFriendsCount() == -1)
                                return;

                            if (SharedPrefsUtil.get.lastVisibleFriendsCount() != -1)
                                count -= SharedPrefsUtil.get.lastVisibleFriendsCount();

                            navDrawer.reloadWithData("Friends", count);
                        }
                    }
                });
                InvitesManager.retrieveInvitesCount(new InvitesManager.InvitesManagerCallback<Integer, ParseException>() {
                    @Override
                    public void done(Integer count, ParseException e) {
                        if (e == null && drawerOpen) {
                            if (SharedPrefsUtil.get.lastVisibleInvitesCount() == -1)
                                return;

                            if (SharedPrefsUtil.get.lastVisibleInvitesCount() != -1)
                                count -= SharedPrefsUtil.get.lastVisibleInvitesCount();

                            navDrawer.reloadWithData("Invites", count);
                        }
                    }
                });
            }
        };

        drawerToggle.setRadius(15);
        drawerToggle.setDownScaleFactor(6.0f);
        drawerToggle.setMainView(contentFrame);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.main_color_dark));

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(7.0f);

        invitePagerAdapter = new FakeInvitePagerAdapter(getSupportFragmentManager());
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);

        updateToolbar(toolbar);
        navDrawer.selectItem(mCurrentSelectedPosition);

        /*Invite invite = new Invite();
        invite.title = "Pants";
        invite.note = "adfadad";
        CalendarEventManager manager = new CalendarEventManager(this, invite);
        manager.insertEventIntoCalender(new CalendarEventManager.OnEventInsertedListener() {
            @Override
            public void OnEventInserted(boolean succes) {

            }
        });*/

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!ConnectionUtil.isInternetAvailable(this)) {
            d = DialogUtil.showErrorDialog(this, getString(R.string.no_internet), getString(R.string.internet_disabled_message), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
            } else {
                GPSTracker.get.init(this);
                checkGPSIsEnabled();
            }
        } else {
            checkGPSIsEnabled();
        }
    }

    private void checkGPSIsEnabled() {
        if (!GPSTracker.get.isGpsEnabled()) {
            d = DialogUtil.showSettingsDialog(this, getString(R.string.no_gps), getString(R.string.gps_disabled_message));
        } else if (GPSTracker.get.getLocation() == null || GPSTracker.get.getLocation().getLatitude() == 0 || GPSTracker.get.getLocation().getLongitude() == 0) {
            LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(ConstantUtil.ACTION_GET_LOCATION));
            showLoader(getString(R.string.detecting_location));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (d != null && d.isShowing())
            d.dismiss();
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(visible);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        hideMenuItems(menu, !drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerItemSelected(ViewGroup drawerView, int position) {
        switch (position) {
            case 0:
                if (!(current instanceof PlacesFragment))
                    current = new PlacesFragment();
                drawerToggle.setBlurEnabled(true);
                break;
            case 1:
                if (!(current instanceof InvitesFragment))
                    current = InvitesFragment.newInstance(inviteId, action);
                drawerToggle.setFragment(current);
                drawerToggle.setBlurEnabled(false);
                break;
            case 2:
                if (!(current instanceof FriendsFragment))
                    current = new FriendsFragment();
                drawerToggle.setBlurEnabled(true);
                break;
            case 4:
                if (!(current instanceof SettingsFragment))
                    current = new SettingsFragment();
                drawerToggle.setBlurEnabled(true);
                break;
        }

        if (current != null) {
            mCurrentSelectedPosition = position;
            updateContent(!(current instanceof PlacesFragment));

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, current).commitAllowingStateLoss();

            if (drawerLayout != null)
                drawerLayout.closeDrawer(drawerView);
            setTitle(navMenuTitles[position]);
            if (mCurrentSelectedPosition == 0) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, filterFragment.getView());
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, filterFragment.getView());
            }

            inviteId = null;
            action = false;
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void onBackPressed() {
        if (!(current instanceof PlacesFragment)) {
            navDrawer.selectItem(0);
        } else {
            super.onBackPressed();
        }
    }

    private void updateContent(boolean belowToolbar) {
        if (contentFrame == null) return;

        if (belowToolbar) {
            ((RelativeLayout.LayoutParams) contentFrame.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.toolbar);
        } else {
            ((RelativeLayout.LayoutParams) contentFrame.getLayoutParams()).addRule(RelativeLayout.BELOW);
        }
        contentFrame.requestLayout();
    }

    @Override
    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public CirclePageIndicator getPageIndicator() {
        return pagerIndicator;
    }

    @Override
    public CardView getShareCard() {
        return inviteShareCard;
    }

    @Override
    public ViewPager getInvitePager() {
        return invitePager;
    }

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }

    @Override
    public void onFilterSelectionComplete(ArrayList<String> categories) {
        if (current instanceof PlacesFragment) {
            ((PlacesFragment) current).setCategories(categories);
            closeDrawer();
            ((PlacesFragment) current).reloadData();
        }
    }

    @Override
    public void openDrawer() {
        drawerLayout.openDrawer(filterFragment.getView());
    }

    @Override
    public void closeDrawer() {
        drawerLayout.closeDrawer(Gravity.RIGHT);
    }

    @Override
    public ViewGroup getActivityContainer() {
        return activity_container;
    }

    @Override
    public ViewGroup getRevealContainer() {
        return reveal_container;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GPSTracker.get.init(this);
                    checkGPSIsEnabled();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    checkGPSIsEnabled();
                }
                break;
        }
    }
}
