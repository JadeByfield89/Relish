package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.fragments.InviteCardFragment;

/**
 * Created by byfieldj on 8/18/15.
 */
public class InvitePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;


    public InvitePagerAdapter(FragmentManager manager){
        super(manager);

    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return InviteCardFragment.newInstance();
            case 1:
                return InviteCardFragment.newInstance();
            case 2:
                return InviteCardFragment.newInstance();
            case 3:
                return  InviteCardFragment.newInstance();


            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
