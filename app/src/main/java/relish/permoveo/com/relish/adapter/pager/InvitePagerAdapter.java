package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import relish.permoveo.com.relish.fragments.inviteflow.EmptyCardFragment;
import relish.permoveo.com.relish.fragments.inviteflow.FriendsInviteFragment;
import relish.permoveo.com.relish.fragments.inviteflow.InviteDetailsFragment;
import relish.permoveo.com.relish.fragments.inviteflow.PickPlaceInviteFragment;
import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by byfieldj on 8/18/15.
 */
public class InvitePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;
    private YelpPlace currentPlace;


    public InvitePagerAdapter(FragmentManager manager){
        super(manager);
    }

    public InvitePagerAdapter(FragmentManager manager, YelpPlace currentPlace){
        super(manager);
        this.currentPlace = currentPlace;
    }


    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                return PickPlaceInviteFragment.newInstance(currentPlace);
            case 1:
                return new FriendsInviteFragment();
            case 2:
                return new InviteDetailsFragment();
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
