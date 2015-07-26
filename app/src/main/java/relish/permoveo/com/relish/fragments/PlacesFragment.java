package relish.permoveo.com.relish.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.PlacesAdapter;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.OnResumeLoadingCallbacks;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.model.Place;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.ConnectionUtil;
import relish.permoveo.com.relish.util.SpacesItemDecoration;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.widget.RatingView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment implements ObservableScrollViewCallbacks, OnResumeLoadingCallbacks {

    private ToolbarCallbacks toolbarCallbacks;
    private int parallaxImageHeight;
    private PlacesAdapter adapter;

    @Bind(R.id.staggered_grid)
    ObservableRecyclerView recyclerView;

    @Bind(R.id.recycler_background)
    View recyclerBackground;

    @Bind(R.id.fab_places)
    FloatingActionButton fab;

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

    @Bind(R.id.places_progress)
    ProgressWheel placesProgress;

    @Bind(R.id.places_header_progress)
    ProgressWheel placesHeaderProgress;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_places, container, false);
        ButterKnife.bind(this, v);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(getResources().getInteger(R.integer.column_count), StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(false);
        int spacing = getResources().getDimensionPixelSize(R.dimen.places_grid_spacing);
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacing));

        fab.attachToRecyclerView(recyclerView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);

        recyclerView.setScrollViewCallbacks(this);
        return v;
    }


    private void renderHeader(Place place) {
        headerDetailsFrame.setVisibility(View.VISIBLE);

        if (TextUtils.isEmpty(place.getLargeImage())) {
            headerImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
        } else {
            Picasso.with(getActivity())
                    .load(place.getLargeImage())
                    .into(headerImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            placesHeaderProgress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            placesHeaderProgress.setVisibility(View.GONE);
                            headerImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
                        }
                    });
        }

        headerPlaceName.setText(place.name);
        headerPlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        headerPlaceName.setIncludeFontPadding(false);

        int quantity = (int) Math.floor(place.getCalculatedDistance());
        headerPlaceDistance.setText(place.formatDistance() +
                " " +
                getResources().getQuantityString(R.plurals.miles, quantity, place.getCalculatedDistance()));
        headerPlaceDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        headerPlaceDistance.setIncludeFontPadding(false);

        headerRating.setRating((int) Math.round(place.rating));

        headerPlaceCost.setText(place.getPriceLevel());
        headerPlaceCost.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        headerPlaceCost.setIncludeFontPadding(false);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.main_color);
        float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
        toolbarCallbacks.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(headerImage, -scrollY / 2);

        // Translate list background
        ViewHelper.setTranslationY(recyclerBackground, Math.max(0, -scrollY + parallaxImageHeight));
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

    private void initialRender() {
        if (GPSTracker.get.getLocation() == null) {
            showErrorText(getString(R.string.unable_get_places_gps));
        } else if (!ConnectionUtil.isInternetAvailable(getActivity())) {
            showErrorText(getString(R.string.unable_get_places_internet));
        } else {
            placesHeaderProgress.setVisibility(View.VISIBLE);
            fab.hide();
            placesProgress.setVisibility(View.VISIBLE);
            placesMessage.setVisibility(View.GONE);
            loadData();
        }
    }

    private void showErrorText(String message) {
        showErrorText(message, true);
    }

    private void showErrorText(String message, boolean header) {
        recyclerView.setVisibility(View.GONE);
        recyclerBackground.setVisibility(View.GONE);
        fab.hide();
        if (header) {
            toolbarCallbacks.getToolbar().setBackgroundColor(getResources().getColor(R.color.main_color));
            placesHeaderProgress.setVisibility(View.GONE);
        }
        placesProgress.setVisibility(View.GONE);
        placesMessage.setVisibility(View.VISIBLE);
        placesMessage.setText(message);
    }

    @Override
    public void loadData() {
        API.getNearestPlaces(new IRequestable() {
            @Override
            public void completed(Object... params) {
                placesProgress.setVisibility(View.GONE);
                fab.show();
                recyclerBackground.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                ArrayList<Place> places = new ArrayList<>((List<Place>) params[0]);
                if (places.size() == 0) {
                    showErrorText(getString(R.string.no_places));
                } else {
                    toolbarCallbacks.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
                    renderHeader(places.get(0));

                    ArrayList<Place> others = new ArrayList<>(places.subList(1, places.size()));
                    if (others.size() == 0) {
                        showErrorText(getString(R.string.no_places), false);
                    }
                }
                adapter.swap(places);
            }

            @Override
            public void failed(Object... params) {
                if (params == null) {
                    showErrorText(getString(R.string.problems_with_loading));
                } else {
                    showErrorText((String) params[0]);
                }
            }
        });
    }
}
