package relish.permoveo.com.relish.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.charbgr.BlurNavigationDrawer.v7.BlurActionBarDrawerToggle;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.BlurBuilder;

/**
 * Created by byfieldj on 8/4/15.
 */
public class RelishDrawerToggle extends BlurActionBarDrawerToggle {

    private View mainView;

    public RelishDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar)
    {
        super(activity, drawerLayout, toolbar, R.string.menu_open, R.string.menu_close);
    }

    public RelishDrawerToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar, int open, int close){
        super(activity, drawerLayout, toolbar, open, close);
    }

    public void setMainView(final View v){
        this.mainView = v;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset)
    {
        super.onDrawerSlide(drawerView, slideOffset);
        mainView.setScaleX(1 - (slideOffset * 0.035f));
        mainView.setScaleY(1 - (slideOffset * 0.035f));

    }


}
