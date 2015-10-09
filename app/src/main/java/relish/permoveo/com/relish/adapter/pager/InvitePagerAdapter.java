package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import relish.permoveo.com.relish.fragments.inviteflow.DetailsInviteFragment;
import relish.permoveo.com.relish.fragments.inviteflow.FriendsInviteFragment;
import relish.permoveo.com.relish.fragments.inviteflow.PickYelpPlaceInviteFragment;
import relish.permoveo.com.relish.fragments.inviteflow.PickGooglePlaceInviteFragment;
import relish.permoveo.com.relish.fragments.inviteflow.SendInviteFragment;
import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by byfieldj on 8/18/15.
 */
public class InvitePagerAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;
    private YelpPlace currentPlace;

    private static final int SOURCE_YELP = 1;
    private static final int SOURCE_GOOGLE = 0;
    private int source = 1;


    public InvitePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    public InvitePagerAdapter(FragmentManager manager, YelpPlace currentPlace) {
        super(manager);
        this.currentPlace = currentPlace;
    }

    public void setSource(int source){
        this.source = source;
    }

    private int getSource(){
        return source;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(getSource() == SOURCE_YELP) {
                    Log.d("InvitePagerAdapter", "Source is YELP");
                    return currentPlace == null ? new PickYelpPlaceInviteFragment() : PickYelpPlaceInviteFragment.newInstance(currentPlace);
                }
                else if(getSource() == SOURCE_GOOGLE){
                    Log.d("InvitePagerAdapter", "Source is GOOGLE");
                    return currentPlace == null ? new PickGooglePlaceInviteFragment() : PickGooglePlaceInviteFragment.newInstance(currentPlace);
                }
            case 1:
                return new FriendsInviteFragment();
            case 2:
                return new DetailsInviteFragment();
            case 3:
                return new SendInviteFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
