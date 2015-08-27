package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.FriendsInvitePagerAdapter;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsInviteFragment extends Fragment {

    @Bind(R.id.invite_friends_title)
    TextView inviteFriendsTitle;

    @Bind(R.id.invite_friends_tabs)
    PagerSlidingTabStrip tabs;

    @Bind(R.id.invite_friends_pager)
    ViewPager viewPager;

    @Bind(R.id.button_next)
    Button next;

    private FriendsInvitePagerAdapter adapter;
    private PagerCallbacks pagerCallbacks;

    public FriendsInviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PagerCallbacks) {
            pagerCallbacks = (PagerCallbacks) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FriendsInvitePagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        inviteFriendsTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        inviteFriendsTitle.setIncludeFontPadding(false);

        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);

        viewPager.setOffscreenPageLimit(3);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Friend> selected = new ArrayList<>();
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if (fragment instanceof ISelectable) {
                        selected.addAll(((ISelectable) fragment).getSelection());
                    }
                }
                if (pagerCallbacks != null)
                    pagerCallbacks.next();
            }
        });
    }
}
