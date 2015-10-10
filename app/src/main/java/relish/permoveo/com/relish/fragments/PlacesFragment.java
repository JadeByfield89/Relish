package relish.permoveo.com.relish.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.YelpPlaceDetailsActivity;
import relish.permoveo.com.relish.adapter.list.PlacesAdapter;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.NavigationDrawerManagementCallbacks;
import relish.permoveo.com.relish.interfaces.OnResumeLoadingCallbacks;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.ConnectionUtil;
import relish.permoveo.com.relish.util.RecyclerItemClickListener;
import relish.permoveo.com.relish.util.SpacesItemDecoration;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RatingView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment implements ObservableScrollViewCallbacks, OnResumeLoadingCallbacks {

    private static final String PAGE = "news_page";
    private static final String TOTAL_NEWS_COUNT = "news_total_count";
    int firstVisibleItem, visibleItemCount, totalItemCount;
    @Bind(R.id.staggered_grid)
    ObservableRecyclerView recyclerView;
    @Bind(R.id.recycler_background)
    View recyclerBackground;
    @Bind(R.id.header_place_image)
    ImageView headerImage;
    //
    @Bind(R.id.header_place_name)
    TextView headerPlaceName;
    //
    @Bind(R.id.header_place_distance)
    TextView headerPlaceDistance;
    //
    @Bind(R.id.header_place_cost)
    TextView headerPlaceCost;
    //
    @Bind(R.id.header_rating_view)
    RatingView headerRating;
    @Bind(R.id.header_place_details_frame)
    FrameLayout headerDetailsFrame;
    @Bind(R.id.places_inform_message)
    TextView placesMessage;
    @Bind(R.id.bounce_progress)
    BounceProgressBar bounceProgressBar;
    @Bind(R.id.places_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.header_layout)
    RelativeLayout headerLayout;

    @Bind(R.id.ivRatingImage)
    ImageView ratingImage;

    private ToolbarCallbacks toolbarCallbacks;
    private int parallaxImageHeight;
    private PlacesAdapter adapter;
    private int page = 0;
    private int total = Integer.MAX_VALUE;
    private int previousTotal = 0;
    private NavigationDrawerManagementCallbacks navigationDrawerManagementCallbacks;
    private boolean loading = true;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ArrayList<String> categories;
    private boolean byCategory = false;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof NavigationDrawerManagementCallbacks)
            navigationDrawerManagementCallbacks = (NavigationDrawerManagementCallbacks) activity;
        if (activity instanceof ToolbarCallbacks)
            toolbarCallbacks = (ToolbarCallbacks) activity;
        adapter = new PlacesAdapter(activity);
        GPSTracker.get.startOnMainLooper();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        toolbarCallbacks.getToolbar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.main_color)));
        toolbarCallbacks = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            page = savedInstanceState.getInt(PAGE);
            total = savedInstanceState.getInt(TOTAL_NEWS_COUNT);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.add(0, R.id.action_filter, 0, "Filter").setIcon(R.drawable.ic_filter);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            navigationDrawerManagementCallbacks.openDrawer();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, v);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.column_count), StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.places_grid_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                YelpPlace restaurant = (YelpPlace) adapter.getItem(position);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    View image = view.findViewById(R.id.grid_item_place_image);
//                    ViewCompat.setTransitionName(image, YelpPlaceDetailsActivity.SHARED_IMAGE_NAME);
//                    YelpPlaceDetailsActivity.launch(getActivity(), image, restaurant);
//                } else {
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    YelpPlaceDetailsActivity.launch(getActivity(), startingLocation[1], restaurant);
//                }
            }
        }));

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);

        recyclerView.setScrollViewCallbacks(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (GPSTracker.get.getLocation() == null) {
                    showErrorText(getString(R.string.unable_get_places_gps));
                    swipeRefreshLayout.setRefreshing(false);
                } else if (!ConnectionUtil.isInternetAvailable(getActivity())) {
                    showErrorText(getString(R.string.unable_get_places_internet));
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    reloadData();
                }
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.main_color);

        return v;
    }


    private void renderHeader(YelpPlace restaurant) {
        if (restaurant == null) {
            headerDetailsFrame.setVisibility(View.GONE);
        } else {
            if (isAdded()) {
                headerDetailsFrame.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(restaurant.image)) {
                    headerImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
//            placesHeaderProgress.setVisibility(View.GONE);
                } else {
                    Picasso.with(getActivity())
                            .load(restaurant.getOriginalImage())
                            .into(headerImage, new Callback() {
                                @Override
                                public void onSuccess() {
//                            placesHeaderProgress.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
//                            placesHeaderProgress.setVisibility(View.GONE);
                                    headerImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
                                }
                            });
                }

                headerPlaceName.setText(restaurant.name);
                headerPlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
                headerPlaceName.setIncludeFontPadding(false);

                headerPlaceDistance.setText(restaurant.formatDistance() +
                        " " +
                        getResources().getQuantityString(R.plurals.miles, restaurant.getCalculatedDistance() == 1.0d ? 1 : 2, restaurant.getCalculatedDistance()));
                headerPlaceDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
                headerPlaceDistance.setIncludeFontPadding(false);

                headerRating.setRating(restaurant.rating);
                //Picasso.with(getContext()).load(restaurant.rating_img_url).fit().into(ratingImage);

            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        Log.d("PlacesFragment", "onScrollChanged");
//        Log.d("PlacesFragment", "onScrollChanged scrollY " + scrollY);
        int baseColor = getResources().getColor(R.color.main_color);
        float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
        toolbarCallbacks.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(headerImage, -scrollY / 2);


        // Translate list background
        ViewHelper.setTranslationY(recyclerBackground, Math.max(0, -scrollY + parallaxImageHeight));


        // loading more
        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = staggeredGridLayoutManager.getItemCount();
        firstVisibleItem = staggeredGridLayoutManager.findFirstVisibleItemPositions(null)[0];

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }

        int visibleThreshold = 0;
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            page++;
            loadData(true, byCategory);
        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    @Override
    public void onResume() {
        super.onResume();

        initialRender();
    }

    public void reloadData() {
        Log.d("PlacesFragment", "reloadData");
        headerLayout.setVisibility(View.INVISIBLE);
        renderHeader(null);
        bounceProgressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
        recyclerView.scrollToPosition(0);
        recyclerView.setVisibility(View.INVISIBLE);

        page = 0;
        total = Integer.MAX_VALUE;
        adapter.clear();
        loadData(false, byCategory);
    }

    private void initialRender() {
        if (GPSTracker.get.getLocation() == null) {
            showErrorText(getString(R.string.unable_get_places_gps));
        } else if (!ConnectionUtil.isInternetAvailable(getActivity())) {
            showErrorText(getString(R.string.unable_get_places_internet));
        } else {
//            recyclerView.setVisibility(View.GONE);
//            recyclerBackground.setVisibility(View.GONE);
//            placesHeaderProgress.setVisibility(View.VISIBLE);
            placesMessage.setVisibility(View.GONE);
//            swipeRefreshLayout.post(new Runnable() {
//                @Override
//                public void run() {
//                    swipeRefreshLayout.setRefreshing(true);
//                }
//            });
            if (adapter.getItemCount() == 0)
                reloadData();
            else
                bounceProgressBar.setVisibility(View.GONE);
        }
    }

    private void showErrorText(String message) {
        showErrorText(message, true);
    }

    private void showErrorText(String message, boolean header) {

        if (header) {
            toolbarCallbacks.getToolbar().setBackgroundColor(getResources().getColor(R.color.main_color));
//            placesHeaderProgress.setVisibility(View.GONE);
        }
        bounceProgressBar.setVisibility(View.GONE);
        if (adapter.getItemCount() == 0)
            placesMessage.setVisibility(View.VISIBLE);
        if (isAdded())
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void loadData(final boolean loadMore, final boolean byCategory) {
        if (total > adapter.getItemCount()) {
            loading = true;
            renderHeader(null);
            API.yelpSearch(page, new IRequestable() {
                @Override
                public void completed(Object... params) {
                    headerLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setVisibility(View.VISIBLE);
                    bounceProgressBar.setVisibility(View.GONE);
                    placesMessage.setVisibility(View.GONE);


                    if (!loadMore && byCategory) {
                        Log.d("PlacesFragment", "Reloading data for category");
                        recyclerView.scrollToPosition(0);
                        adapter.notifyDataSetChanged();
                        renderHeader(adapter.getTop());

                    }

                    ArrayList<YelpPlace> places = new ArrayList<>((List<YelpPlace>) params[1]);
                    if (places.size() == 0) {
                        if (!loadMore)
                            showErrorText(getString(R.string.no_places));
                    } else {
                        if (!loadMore && isAdded())
                            toolbarCallbacks.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
                        adapter.addAll(places);
                        renderHeader(adapter.getTop());
                        total = (Integer) params[0];
                        recyclerView.setVisibility(View.VISIBLE);

                        if (adapter.getItemCount() == 0) {
                            showErrorText(getString(R.string.no_places), false);
                        }
                    }
                    loading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void failed(Object... params) {
                    bounceProgressBar.setVisibility(View.GONE);
                    placesMessage.setVisibility(View.GONE);
                    if (params == null || params.length == 0) {
                        showErrorText(getString(R.string.problems_with_loading));
                    } else {
                        showErrorText((String) params[0]);
                    }
                    loading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, byCategory, categories);
        } else {
            adapter.removeFooter();
            bounceProgressBar.setVisibility(View.GONE);
        }
    }


    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
        byCategory = true;
    }
}
