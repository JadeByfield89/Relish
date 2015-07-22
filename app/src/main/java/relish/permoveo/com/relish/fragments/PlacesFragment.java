package relish.permoveo.com.relish.fragments;


import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etsy.android.grid.StaggeredGridView;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.PlacesAdapter;
import relish.permoveo.com.relish.interfaces.ToolbarCallbacks;
import relish.permoveo.com.relish.util.FakeData;
import relish.permoveo.com.relish.widget.RatingView;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlacesFragment extends Fragment {

    private int lastDampedScroll;
    private ToolbarCallbacks toolbarCallbacks;
    private Drawable actionBarBackgroundDrawable;
    private PlacesAdapter adapter;
    private boolean mHasRequestedMore;

    @Bind(R.id.staggered_grid)
    StaggeredGridView observableStaggeredGridView;

    @Bind(R.id.fab_places)
    FloatingActionButton fab;

    @Bind(R.id.header_layout)
    RelativeLayout headerLayout;

    @Bind(R.id.header_place_image)
    ImageView headerImage;

    @Bind(R.id.header_place_name)
    TextView headerPlaceName;

    @Bind(R.id.header_place_distance)
    TextView headerPlaceDistance;

    @Bind(R.id.header_place_cost)
    TextView headerPlaceCost;

    @Bind(R.id.header_rating_view)
    RatingView headerRating;

    public PlacesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        toolbarCallbacks = (ToolbarCallbacks) activity;
        actionBarBackgroundDrawable = toolbarCallbacks.getToolbar().getBackground();
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

        final FrameLayout mMarginView = new FrameLayout(observableStaggeredGridView.getContext());
        mMarginView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0));
        observableStaggeredGridView.addHeaderView(mMarginView, null, false);

        fab.attachToListView(observableStaggeredGridView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        observableStaggeredGridView.setAdapter(adapter);
        observableStaggeredGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        observableStaggeredGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                View topChild = view.getChildAt(0);
                if (topChild == null) {
                    onNewScroll(0);
                } else if (topChild != mMarginView) {
                    onNewScroll(headerLayout.getHeight());
                } else {
                    onNewScroll(-topChild.getTop());
                }
            }
        });

        renderHeader();
        adapter.swap(FakeData.getFakePlaces());

        onNewScroll(0);
        return v;
    }

    private void renderHeader() {
        Picasso.with(getActivity())
                .load(FakeData.HEADER_IMAGE_URL)
                .into(headerImage);

        headerPlaceName.setText(FakeData.HEADER_PLACE_NAME);
        headerPlaceDistance.setText(FakeData.HEADER_PLACE_DISTANCE + " miles");
        headerRating.setRating(FakeData.HEADER_PLACE_RATING);
        headerPlaceCost.setText(FakeData.HEADER_PRICE_RANKING.toString());
    }


    private void updateActionBarTransparency(float scrollRatio) {
        int newAlpha = (int) (scrollRatio * 255);
        actionBarBackgroundDrawable.setAlpha(newAlpha);
        toolbarCallbacks.getToolbar().setBackgroundDrawable(actionBarBackgroundDrawable);
    }

    private void updateParallaxEffect(int scrollPosition) {
        float damping = 0.5f;
        int dampedScroll = (int) (scrollPosition * damping);
        int offset = lastDampedScroll - dampedScroll;
        headerLayout.offsetTopAndBottom(-offset);

        lastDampedScroll = dampedScroll;
    }

    public void onNewScroll(int scrollPosition) {
        int headerHeight = headerLayout.getHeight() - toolbarCallbacks.getToolbar().getHeight();
        float ratio = 0;
        if (scrollPosition > 0 && headerHeight > 0)
            ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

        updateActionBarTransparency(ratio);
        updateParallaxEffect(scrollPosition);
    }
}
