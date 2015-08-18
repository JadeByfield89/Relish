package relish.permoveo.com.relish.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import relish.permoveo.com.relish.activities.PlaceDetailsActivity;
import relish.permoveo.com.relish.adapter.list.PlacesAdapter;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
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

    private ToolbarCallbacks toolbarCallbacks;
    private int parallaxImageHeight;
    private PlacesAdapter adapter;
    private int page = 0;
    private int total = Integer.MAX_VALUE;
    private int previousTotal = 0;
    private boolean loading = true;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
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

    private ArrayList<String> categories;
    private boolean byCategory = false;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        toolbarCallbacks = (ToolbarCallbacks) activity;
        adapter = new PlacesAdapter(activity);
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
                startActivity(new Intent(getActivity(), PlaceDetailsActivity.class)
                        .putExtra(PlaceDetailsActivity.PASSED_PLACE, restaurant));
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

//        headerPlaceCost.setText(place.getPriceLevel());
//        headerPlaceCost.setTypeface(TypefaceUtil.PROXIMA_NOVA);
//        headerPlaceCost.setIncludeFontPadding(false);
            }
        }
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
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
        //headerLayout.setVisibility(View.INVISIBLE);
        //swipeRefreshLayout.setVisibility(View.INVISIBLE);
        //bounceProgressBar.setVisibility(View.VISIBLE);
        renderHeader(null);
        recyclerView.scrollToPosition(0);
        page = 0;
        total = Integer.MAX_VALUE;
        adapter.clear();
        loadData(true, byCategory);
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
//        recyclerView.setVisibility(View.GONE);
//        recyclerBackground.setVisibility(View.GONE);
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
    public void loadData(final boolean loadMore, boolean byCategory) {
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
//                    recyclerBackground.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.VISIBLE);
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


    public void setCategories(ArrayList<String> categories){
        this.categories = categories;
        byCategory = true;
    }
}
