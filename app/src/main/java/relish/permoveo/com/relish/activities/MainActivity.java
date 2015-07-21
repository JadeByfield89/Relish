package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.FriendsFragment;
import relish.permoveo.com.relish.fragments.HelpFragment;
import relish.permoveo.com.relish.fragments.InvitesFragment;
import relish.permoveo.com.relish.fragments.NavigationDrawerFragment;
import relish.permoveo.com.relish.fragments.PlacesFragment;
import relish.permoveo.com.relish.fragments.SettingsFragment;
import relish.permoveo.com.relish.util.CustomTypefaceSpan;


public class MainActivity extends RelishActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    ActionBarDrawerToggle drawerToggle;
    boolean drawerOpen = false;
    Fragment current = null;
    NavigationDrawerFragment navDrawer;

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
                Display display = getWindowManager().getDefaultDisplay();
                int width = display.getWidth();  // deprecated
                navDrawer.getView().getLayoutParams().width = width - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56.0f , getResources().getDisplayMetrics());
                navDrawer.getView().requestLayout();
            }
        });

        Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");
        String appName = getString(R.string.app_name);
        SpannableStringBuilder SS = new SpannableStringBuilder(appName);
        SS.setSpan(new CustomTypefaceSpan("", satisfy), 0, appName.length(), 0);
        getSupportActionBar().setTitle(SS);

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

        updateStatusBar();
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
}
