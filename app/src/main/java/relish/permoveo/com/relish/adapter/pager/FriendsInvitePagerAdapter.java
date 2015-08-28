package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.fragments.inviteflow.FriendsInviteListFragment;

/**
 * Created by rom4ek on 28.08.2015.
 */
public class FriendsInvitePagerAdapter extends FragmentPagerAdapter {

    private String[] TITLES = new String[]{"Friends", "Contacts"};

    public FriendsInvitePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return new FriendsInviteListFragment();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
