package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.inviteflow.InviteFriendsListAdapter;
import relish.permoveo.com.relish.interfaces.ISelectable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsInviteListFragment extends Fragment implements ISelectable, Filterable {

    private InviteFriendsListAdapter adapter;

    @Bind(R.id.empty_list_container)
    LinearLayout emptyView;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

    @Bind(R.id.bounce_progress)
    BounceProgressBar bounceProgressBar;

    @Bind(R.id.invite_friends_list_recycler)
    RecyclerView recyclerView;

    private InviteCreator creator;

    public FriendsInviteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InviteCreator) {
            creator = (InviteCreator) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new InviteFriendsListAdapter(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_invite_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        emptyMessage.setText(getString(R.string.invite_friends_list_empty));
        emptyMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emptyMessage.setIncludeFontPadding(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
//        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        render();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void render() {
        FriendsManager.retrieveFriendsList(new FriendsManager.FriendsManagerCallback<ArrayList<Friend>, ParseException>() {
            @Override
            public void done(ArrayList<Friend> friends, ParseException e) {
                if (e == null) {
                    if (friends.size() > 0) {
                        emptyView.setVisibility(View.GONE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        for (Friend friend : friends) {
                            for (InvitePerson invited : creator.getInvite().invited) {
                                if (invited instanceof Friend && ((Friend) invited).id.equals(friend.id)) {
                                    friend.isSelected = true;
                                }
                            }
                        }

                        adapter.swap(friends);
                    } else {
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                } else {
                    if (isAdded()) {
                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        adapter.clear();
                        emptyView.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    @Override
    public ArrayList<Friend> getSelection() {
        return adapter != null ? adapter.getSelected() : new ArrayList<Friend>();
    }

    @Override
    public Filter getFilter() {
        return adapter.getFilter();
    }
}
