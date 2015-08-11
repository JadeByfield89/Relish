package relish.permoveo.com.relish.fragments.search;


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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.AddFriendsListAdapter;
import relish.permoveo.com.relish.model.Friend;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelishUsersFragment extends Fragment {

    private MenuItem searchItem;
    private AddFriendsListAdapter adapter;

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
            public void onClick(View view) {

            }
        });
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

    private void onQueryTextChanged(String query) {
        if (!TextUtils.isEmpty(query)) {
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereStartsWith("username", query);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null) {
                        ArrayList<Friend> friends = new ArrayList<>();
                        for (ParseUser user : objects) {
                            Friend friend = new Friend();
                            friend.name = user.getUsername();
                            if (user.containsKey("avatar")) {
                                ParseFile parseFile = (ParseFile) user.get("avatar");
                                friend.image = parseFile.getUrl();
                            }
                            friends.add(friend);
                        }
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
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                onQueryTextChanged(s);
                return true;
            }
        });
        searchView.findViewById(R.id.search_button).performClick();
    }

}
