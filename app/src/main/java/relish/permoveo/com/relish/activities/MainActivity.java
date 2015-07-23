package relish.permoveo.com.relish.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseUser;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.FriendsFragment;
import relish.permoveo.com.relish.fragments.HelpFragment;
import relish.permoveo.com.relish.fragments.InvitesFragment;
import relish.permoveo.com.relish.fragments.NavigationDrawerFragment;
import relish.permoveo.com.relish.fragments.PlacesFragment;
import relish.permoveo.com.relish.fragments.SettingsFragment;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;


public class MainActivity extends RelishActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, ToolbarCallbacks {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Bind(R.id.content_frame)
    FrameLayout contentFrame;

    ActionBarDrawerToggle drawerToggle;
    boolean drawerOpen = false;
    Fragment current = null;
    Dialog d;
    NavigationDrawerFragment navDrawer;

    BroadcastReceiver locationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (d != null && d.isShowing())
                d.dismiss();

            hideLoader();
//            if (Alerts.get.alertTypes.size() == 0)
//                initLoading();
            LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        navDrawer = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

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
            }
        });

//        String appName = getString(R.string.app_name);
//        SpannableStringBuilder SS = new SpannableStringBuilder(appName);
//        SS.setSpan(new CustomTypefaceSpan("", TypefaceUtil.BRANNBOLL_BOLD), 0, appName.length(), 0);
        getActionBarTextView().setTypeface(TypefaceUtil.BRANNBOLL_BOLD);
        getActionBarTextView().setIncludeFontPadding(false);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {
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
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.main_color_dark));

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(toolbar.getPaddingLeft(), getStatusBarHeight(), toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            toolbar.requestLayout();
        }

        updateStatusBar(getResources().getColor(R.color.status_bar_color));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!GPSTracker.get.isGpsEnabled()) {
            d = new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Your location")
                    .setCancelable(false)
                    .setMessage(getString(R.string.gps_disabled_message))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent viewIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(viewIntent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else if (GPSTracker.get.getLocation() == null || GPSTracker.get.getLocation().getLatitude() == 0 || GPSTracker.get.getLocation().getLongitude() == 0) {
            LocalBroadcastManager.getInstance(this).registerReceiver(locationReceiver, new IntentFilter(ConstantUtil.ACTION_GET_LOCATION));
            showLoader(getString(R.string.detecting_location));
        } else {

        }
    }

    private void hideMenuItems(Menu menu, boolean visible) {
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(visible);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, R.id.action_logout, 0, "Logout").setIcon(R.drawable.ic_logout);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            ParseUser.logOut();
            startActivity(new Intent(this, SignupActivity.class));
            finish();
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
                break;
            case 1:
                if (!(current instanceof InvitesFragment))
                    current = new InvitesFragment();
                break;
            case 2:
                if (!(current instanceof FriendsFragment))
                    current = new FriendsFragment();
                break;
            case 3:
                if (!(current instanceof SettingsFragment))
                    current = new SettingsFragment();
                break;
            case 4:
                if (!(current instanceof HelpFragment))
                    current = new HelpFragment();
                break;
        }

        if (current != null) {
            updateContent(!(current instanceof PlacesFragment));

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, current).commitAllowingStateLoss();

            if (drawerLayout != null)
                drawerLayout.closeDrawer(drawerView);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
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

}
