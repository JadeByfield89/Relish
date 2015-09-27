package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.fragments.inviteflow.EmptyCardFragment;
import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by rom4ek on 25.08.2015.
 */
public class FakeInvitePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;

    public FakeInvitePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new EmptyCardFragment();
            case 1:
                return new EmptyCardFragment();
            case 2:
                return new EmptyCardFragment();
            case 3:
                return new EmptyCardFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
