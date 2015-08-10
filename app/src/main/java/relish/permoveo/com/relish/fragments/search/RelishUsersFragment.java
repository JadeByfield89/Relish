package relish.permoveo.com.relish.fragments.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RelishUsersFragment extends Fragment {

    private MenuItem searchItem;

    @Bind(R.id.empty_query_container)
    LinearLayout emptyView;

    public RelishUsersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_relish_users, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
            MenuItemCompat.expandActionView(searchItem);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchItem != null && MenuItemCompat.isActionViewExpanded(searchItem)) {
            MenuItemCompat.collapseActionView(searchItem);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_relish_users, menu);

        searchItem = menu.findItem(R.id.action_search_relish);
        MenuItemCompat.expandActionView(searchItem);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });
    }

}
