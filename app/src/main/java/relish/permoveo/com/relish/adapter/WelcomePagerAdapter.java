package relish.permoveo.com.relish.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.WelcomeFragment;

/**
 * Created by byfieldj on 7/31/15.
 */
public class WelcomePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;

    public WelcomePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WelcomeFragment.newInstance("0", R.drawable.welcome_1);
            case 1:
                return WelcomeFragment.newInstance("1", R.drawable.welcome_2);
            case 2:
                return WelcomeFragment.newInstance("2", R.color.main_color);
            case 3:
                return WelcomeFragment.newInstance("3", R.color.main_color);

            default:
                return null;


        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
