package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.FriendsInvitePagerAdapter;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsInviteFragment extends Fragment {

    @Bind(R.id.invite_friends_card)
    RelativeLayout inviteFriendsCard;

    @Bind(R.id.invite_friends_title)
    TextView inviteFriendsTitle;

    @Bind(R.id.invite_friends_tabs)
    PagerSlidingTabStrip tabs;

    @Bind(R.id.invite_friends_pager)
    ViewPager viewPager;

    @Bind(R.id.button_next)
    Button next;

    @Bind(R.id.searchView)
    SearchView searchView;

    private FriendsInvitePagerAdapter adapter;
    private PagerCallbacks pagerCallbacks;
    private InviteCreator creator;

    public FriendsInviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PagerCallbacks) {
            pagerCallbacks = (PagerCallbacks) activity;
        }

        if (activity instanceof InviteCreator) {
            creator = (InviteCreator) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FriendsInvitePagerAdapter(getChildFragmentManager());
    }


    private Fragment getActiveFragment(ViewPager container, int position) {
        String name = makeFragmentName(container.getId(), position);
        return getChildFragmentManager().findFragmentByTag(name);
    }

    private static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
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

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                closeSearchView();
                setOnQueryTextListener(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setOffscreenPageLimit(2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList selected = new ArrayList<>();
                for (Fragment fragment : getChildFragmentManager().getFragments()) {
                    if (fragment instanceof ISelectable) {
                        selected.addAll(((ISelectable) fragment).getSelection());
                    }
                }

                if (selected.size() > 0) {
                    creator.getInvite().invited = new ArrayList<>(selected);
                    if (pagerCallbacks != null)
                        pagerCallbacks.next();
                } else {
                    YoYo.with(Techniques.Shake)
                            .playOn(inviteFriendsCard);
                    Snackbar.make(inviteFriendsCard, getString(R.string.friends_error), Snackbar.LENGTH_LONG).show();
                }
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteFriendsTitle.setVisibility(View.GONE);
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                inviteFriendsTitle.setVisibility(View.VISIBLE);
                return false;
            }
        });
        ((AppCompatAutoCompleteTextView) searchView.findViewById(R.id.search_src_text)).setTextColor(getResources().getColor(android.R.color.black));

        setOnQueryTextListener(0);
    }

    public void closeSearchView() {
        if (searchView != null && !searchView.isIconified()) {
//            searchView.onActionViewCollapsed();
            if (!TextUtils.isEmpty(((AppCompatAutoCompleteTextView) searchView.findViewById(R.id.search_src_text)).getText()))
                searchView.findViewById(R.id.search_close_btn).performClick();

            searchView.findViewById(R.id.search_close_btn).performClick();
        }
    }

    private void setOnQueryTextListener(final int position) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((Filterable) getActiveFragment(viewPager, position)).getFilter().filter(newText);
                return true;
            }
        });
    }
}
