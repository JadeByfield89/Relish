package relish.permoveo.com.relish.fragments;

import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.NavDrawerAdapter;
import relish.permoveo.com.relish.model.NavDrawerItem;

public class NavigationDrawerFragment extends Fragment {

    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * A pointer to the getCurrent callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    @Bind(R.id.nav_menu)
    ListView mDrawerListView;

    @Bind(R.id.drawer_view)
    ViewGroup drawerView;

    private String[] navMenuTitles;

    @Bind(R.id.nav_header_avatar)
    CircleImageView headerAvatar;

    @Bind(R.id.nav_header_username)
    TextView headerUsername;

    @Bind(R.id.nav_header_background)
    KenBurnsView kenBurnsView;

    private int mCurrentSelectedPosition = 0;
    NavDrawerAdapter adapter;

    public NavigationDrawerFragment() {
    }


    public void reload() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        reload();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public NavDrawerAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, view);

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        TypedArray navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);
        ArrayList<NavDrawerItem> navDrawerItems = new ArrayList<>();

        for (int i = 0; i < navMenuTitles.length; i++) {
            if (i == 3) {
                navDrawerItems.add(new NavDrawerItem());
            } else {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons
                        .getResourceId(i, -1)));
            }

            if (i == 2) {
                navDrawerItems.get(i).counter = 2;
            }
        }
        navMenuIcons.recycle();

        adapter = new NavDrawerAdapter(getActivity(),
                navDrawerItems);

        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        mDrawerListView.setCacheColorHint(0);
        mDrawerListView.setScrollingCacheEnabled(false);
        mDrawerListView.setScrollContainer(false);
        mDrawerListView.setFastScrollEnabled(true);
        mDrawerListView.setSmoothScrollbarEnabled(true);
        return view;
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            mDrawerListView.setSelection(position);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(drawerView, position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        /*s
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(ViewGroup drawerView, int position);
    }
}