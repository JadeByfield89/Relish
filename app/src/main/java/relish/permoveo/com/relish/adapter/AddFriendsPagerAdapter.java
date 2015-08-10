package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.search.ContactsFragment;
import relish.permoveo.com.relish.fragments.search.FacebookFriendsFragment;
import relish.permoveo.com.relish.fragments.search.RelishUsersFragment;
import relish.permoveo.com.relish.model.ViewPagerHeader;

/**
 * Created by Roman on 10.08.15.
 */
public class AddFriendsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    private ArrayList<ViewPagerHeader> headers;

    public AddFriendsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        headers = new ArrayList<>();
        headers.add(new ViewPagerHeader(context.getResources().getString(R.string.add_friends_users_title), R.drawable.ic_users_selector));
        headers.add(new ViewPagerHeader(context.getResources().getString(R.string.add_friends_contacts_title), R.drawable.ic_contacts_selector));
        headers.add(new ViewPagerHeader(context.getResources().getString(R.string.add_friends_facebook_title), R.drawable.ic_facebook_selector));
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new RelishUsersFragment();
            case 1:
                return new ContactsFragment();
            case 2:
                return new FacebookFriendsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return headers.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getCustomTabView(ViewGroup viewGroup, int i) {
        LinearLayout tabLayout = (LinearLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_friends_pager_header, viewGroup, false);
        TextView tabTitle = (TextView) tabLayout.findViewById(R.id.tab_header_title);
        ImageView tabIcon = (ImageView) tabLayout.findViewById(R.id.tab_header_image);

        ViewPagerHeader header = headers.get(i);
        tabTitle.setText(header.title.toUpperCase());
        tabTitle.setIncludeFontPadding(false);
        tabIcon.setImageResource(header.image);
        return tabLayout;
    }

    @Override
    public void tabSelected(View view) {
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_header_title);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_header_image);
        tabIcon.setSelected(true);
        tabTitle.setSelected(true);
    }

    @Override
    public void tabUnselected(View view) {
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_header_title);
        ImageView tabIcon = (ImageView) view.findViewById(R.id.tab_header_image);
        tabIcon.setSelected(false);
        tabTitle.setSelected(false);
    }
}
