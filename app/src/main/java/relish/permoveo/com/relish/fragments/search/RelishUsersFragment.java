package relish.permoveo.com.relish.fragments.search;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.parse.ParseException;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.AddFriendsListAdapter;
import relish.permoveo.com.relish.dialogs.FriendGroupsDialog;
import relish.permoveo.com.relish.manager.FriendsManager;
import relish.permoveo.com.relish.model.Friend;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelishUsersFragment extends Fragment {

    public static final int FRIENDS_GROUP_REQUEST_CODE = 111;

    private MenuItem searchItem;
    private AddFriendsListAdapter adapter;
    private String oldQuery;
    private CircularProgressButton current;

    @Bind(R.id.empty_query_container)
    LinearLayout emptyView;

    @Bind(R.id.add_friends_recycler)
    RecyclerView recyclerView;

    public RelishUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        adapter = new AddFriendsListAdapter(getActivity(), new AddFriendsListAdapter.ViewHolder.AddFriendButtonClickListener() {
            @Override
            public void onClick(final View view) {
                current = (CircularProgressButton) view.findViewById(R.id.friend_btn);
                int position = recyclerView.getChildPosition(view);
                final Friend friend = (Friend) adapter.getItem(position);
                if (current.getProgress() != 100) {
                    FriendGroupsDialog dialog = FriendGroupsDialog.newInstance(friend);
                    dialog.setTargetFragment(RelishUsersFragment.this, FRIENDS_GROUP_REQUEST_CODE);
                    dialog.show(getChildFragmentManager(), "friends_group_dialog");
                }
            }
        });
        if (savedInstanceState != null) {
            oldQuery = savedInstanceState.getString("query");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_relish_users, container, false);
        ButterKnife.bind(this, v);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (searchItem != null && !MenuItemCompat.isActionViewExpanded(searchItem)) {
//            MenuItemCompat.expandActionView(searchItem);
//        }
        if (searchItem != null && !TextUtils.isEmpty(((SearchView) searchItem.getActionView()).getQuery())) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            adapter.clear();
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
//            MenuItemCompat.collapseActionView(searchItem);
//        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("query", ((SearchView) searchItem.getActionView()).getQuery().toString());
        super.onSaveInstanceState(outState);
    }

    private void onQueryTextSubmitted(String query) {
        if (!TextUtils.isEmpty(query)) {
            FriendsManager.searchFriend(query, new FriendsManager.FriendsManagerCallback<ArrayList<Friend>, ParseException>() {
                @Override
                public void done(ArrayList<Friend> friends, ParseException e) {
                    if (e == null) {
                        if (friends.size() > 0 && !TextUtils.isEmpty(((SearchView) searchItem.getActionView()).getQuery())) {
                            adapter.swap(friends);
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyView.setVisibility(View.GONE);
                        } else {
                            adapter.clear();
                            recyclerView.setVisibility(View.GONE);
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (isAdded())
                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            adapter.clear();
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_relish_users, menu);

        searchItem = menu.findItem(R.id.action_search_relish);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.relish_users_search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                onQueryTextSubmitted(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        if (!TextUtils.isEmpty(oldQuery)) {
            searchView.setQuery(oldQuery, false);
        }
        searchView.findViewById(R.id.search_button).performClick();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == FRIENDS_GROUP_REQUEST_CODE) {
            if (current != null && current.getProgress() != 100) {
                current.setProgress(50);
                final String groupName = data.getStringExtra(FriendGroupsDialog.CHOSEN_GROUP);
                String friendId = data.getStringExtra(FriendGroupsDialog.CHOSEN_FRIEND);
                FriendsManager.addFriend(groupName, friendId, new FriendsManager.FriendsManagerCallback<Object, ParseException>() {
                    @Override
                    public void done(Object o, ParseException e) {
                        if (e == null) {
                            current.setCompleteText(groupName);
                            current.setProgress(100);
                        } else {
                            current.setProgress(0);
                            if (isAdded())
                                Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                        current = null;
                    }
                });
            }
        }
    }
}
