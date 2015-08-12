package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.fragments.FriendsListFragment;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class FriendsPagerAdapter extends FragmentPagerAdapter {

    String[] TITLES = {"Friends", "Colleagues", "Coworkers"};

    public FriendsPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        return FriendsListFragment.newInstance(TITLES[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
