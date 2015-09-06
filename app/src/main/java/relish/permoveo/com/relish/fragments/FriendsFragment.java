package relish.permoveo.com.relish.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.parse.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.AddFriendsActivity;
import relish.permoveo.com.relish.adapter.pager.FriendsPagerAdapter;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.util.SharedPrefsUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private FriendsPagerAdapter adapter;

    @Bind(R.id.friends_tabs)
    PagerSlidingTabStrip tabs;

    @Bind(R.id.friends_pager)
    ViewPager viewPager;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new FriendsPagerAdapter(getActivity(), getChildFragmentManager());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(0, R.id.action_add_friend, 0, R.string.add_friends_title)
                .setIcon(R.drawable.ic_add_friends);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_friend:
                startActivity(new Intent(getActivity(), AddFriendsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_friends, container, false);
        ButterKnife.bind(this, v);
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);

        viewPager.setOffscreenPageLimit(1);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        FriendsManager.retrieveFriendsCount(new FriendsManager.FriendsManagerCallback<Integer, ParseException>() {
            @Override
            public void done(final Integer count, ParseException e) {
                if (e == null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (SharedPrefsUtil.get.lastVisibleFriendsCount() == -1)
                                SharedPrefsUtil.get.setLastVisibleFriendsCount(count);

                            adapter.swap(count, count - SharedPrefsUtil.get.lastVisibleFriendsCount());
                        }
                    });
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
