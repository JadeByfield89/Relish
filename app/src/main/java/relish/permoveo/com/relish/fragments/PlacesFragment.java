package relish.permoveo.com.relish.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.PlacesAdapter;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.util.FakeData;
import relish.permoveo.com.relish.util.SpacesItemDecoration;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.widget.RatingView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment implements ObservableScrollViewCallbacks{

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
//        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.column_count));
//        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int position) {
//                return position == 0 ? gridLayoutManager.getSpanCount() : 1;
//            }
//        });
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
        renderHeader();
        adapter.swap(FakeData.getFakePlaces());

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);

        toolbarCallbacks.getToolbar().setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));

        recyclerView.setScrollViewCallbacks(this);
//        recyclerView.setPadding(recyclerView.getPaddingLeft(), parallaxImageHeight/ 2, recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());
//        recyclerView.setClipToPadding(false);
//        View paddingView = new View(getActivity());
//        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
//                parallaxImageHeight);
//        paddingView.setLayoutParams(lp);
//        paddingView.setClickable(true);
//
//            recyclerView.addHeaderView(paddingView);

        return v;
    }


    private void renderHeader() {
        Picasso.with(getActivity())
                .load(FakeData.HEADER_IMAGE_URL)
                .into(headerImage);

        headerPlaceName.setText(FakeData.HEADER_PLACE_NAME);
        headerPlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        headerPlaceName.setIncludeFontPadding(false);

        headerPlaceDistance.setText(FakeData.HEADER_PLACE_DISTANCE + " miles");
        headerPlaceDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        headerPlaceDistance.setIncludeFontPadding(false);

        headerRating.setRating(FakeData.HEADER_PLACE_RATING);

        headerPlaceCost.setText(FakeData.HEADER_PRICE_RANKING.toString());
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
}
