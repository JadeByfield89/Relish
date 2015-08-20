package relish.permoveo.com.relish.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.FriendsListFragment;
import relish.permoveo.com.relish.model.ViewPagerHeader;
import relish.permoveo.com.relish.util.SharedPrefsUtil;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class FriendsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    private ArrayList<ViewPagerHeader> headers;

    public FriendsPagerAdapter(FragmentManager manager) {
        super(manager);
        headers = new ArrayList<>();
        headers.add(new ViewPagerHeader("Friends", 0));
        headers.add(new ViewPagerHeader("Colleagues", 0));
        headers.add(new ViewPagerHeader("Coworkers", 0));
    }

    public void swap(int[] counters) {
        for (int i = 0; i < counters.length; i++) {
            headers.get(i).image = counters[i];
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return headers.size();
    }

    @Override
    public Fragment getItem(int position) {
        return FriendsListFragment.newInstance(headers.get(position).title);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getCustomTabView(ViewGroup viewGroup, int i) {
        RelativeLayout tabLayout = (RelativeLayout) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends_pager_header, viewGroup, false);
        TextView tabTitle = (TextView) tabLayout.findViewById(R.id.tab_title);
        TextView tabBadge = (TextView) tabLayout.findViewById(R.id.tab_badge);

        ViewPagerHeader header = headers.get(i);
        tabTitle.setText(header.title.toUpperCase());

        if (header.image != -1 && header.image != 0) {
            tabBadge.setVisibility(View.VISIBLE);
            tabBadge.setText(String.valueOf(header.image));
        } else {
            tabBadge.setVisibility(View.GONE);
        }
        return tabLayout;
    }

    @Override
    public void tabSelected(View view) {
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_title);
        TextView tabBadge = (TextView) view.findViewById(R.id.tab_badge);
        tabTitle.setSelected(true);
        tabBadge.setVisibility(View.GONE);

        String group = tabTitle.getText().toString().toLowerCase();
        int newCount = TextUtils.isEmpty(tabBadge.getText()) ? 0 : Integer.valueOf(tabBadge.getText().toString());
        if (SharedPrefsUtil.get.lastVisibleFriendsCountForGroup(group) == -1)
            SharedPrefsUtil.get.setLastVisibleFriendsCountForGroup(group, newCount);
        else
            SharedPrefsUtil.get.setLastVisibleFriendsCountForGroup(group, SharedPrefsUtil.get.lastVisibleFriendsCountForGroup(group) + newCount);
        tabBadge.setText("0");
    }

    @Override
    public void tabUnselected(View view) {
        TextView tabTitle = (TextView) view.findViewById(R.id.tab_title);
        tabTitle.setSelected(false);
    }
}
