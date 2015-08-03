package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.nineoldandroids.view.ViewHelper;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.IRequestableDefaultImpl;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.model.Yelp.YelpPlace;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.google.GoogleReview;
import relish.permoveo.com.relish.network.API;

public class PlaceDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks{

    public static final String PASSED_PLACE = "passed_place_extra";
    private static final String FETCHED_PLACE = "fetched_place_extra";

    private YelpPlace passedPlace;
    private YelpPlace fetchedPlace;
    private int parallaxImageHeight;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.place_details_progress)
    ProgressWheel placeDetailsProgress;

    @Bind(R.id.place_details_inform_message)
    TextView placeDetailsMessage;

    @Bind(R.id.header_place_details_image)
    ImageView placeDetailsImage;

    @Bind(R.id.place_details_scroll_view)
    com.melnykov.fab.ObservableScrollView placeDetalsScrollView;

    @Bind(R.id.fab_place_details)
    FloatingActionButton placeDetailsFab;

    @Bind(R.id.anchor)
    View anchor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        ButterKnife.bind(this);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FETCHED_PLACE))
                fetchedPlace = (YelpPlace) savedInstanceState.getSerializable(FETCHED_PLACE);
            passedPlace = (YelpPlace) savedInstanceState.getSerializable(PASSED_PLACE);
        } else if (getIntent().hasExtra(PASSED_PLACE))
            passedPlace = (YelpPlace) getIntent().getSerializableExtra(PASSED_PLACE);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(passedPlace.name);

        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);

        placeDetailsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        placeDetailsFab.attachToScrollView(placeDetalsScrollView, null, new ObservableScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ScrollView scrollView, int i, int scrollY, int i2, int i3) {
                int baseColor = getResources().getColor(R.color.main_color);
                float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
                toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
                ViewHelper.setTranslationY(placeDetailsImage, -scrollY / 2);
            }
        });

        updateStatusBar(getResources().getColor(R.color.main_color_dark));
    }

    private void renderPlaceDetails() {
        if (TextUtils.isEmpty(fetchedPlace.image)) {
            placeDetailsImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
        } else {
            Picasso.with(this)
                    .load(fetchedPlace.getOriginalImage())
                    .into(placeDetailsImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            placeDetailsImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
                        }
                    });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        API.getYelpPlaceDetails(passedPlace.id, new IRequestable() {
            @Override
            public void completed(Object... params) {
                placeDetailsProgress.setVisibility(View.GONE);
                placeDetailsMessage.setVisibility(View.GONE);
                fetchedPlace = (YelpPlace) params[0];
                fetchedPlace.distance = passedPlace.distance;
                renderPlaceDetails();
                API.googleSearch(passedPlace, new IRequestableDefaultImpl() {
                    @Override
                    public void completed(Object... params) {
                        ArrayList<GooglePlace> places = (ArrayList<GooglePlace>) params[0];
                        if (places != null && places.size() > 0) {
                            GooglePlace matching = places.get(0);
                            API.getGooglePlaceDetails(matching.reference, new IRequestableDefaultImpl() {
                                @Override
                                public void completed(Object... params) {
                                    final ArrayList<Review> googleReviews = (ArrayList<Review>) params[0];
                                    if (googleReviews != null && googleReviews.size() > 0) {
                                        for (int i = 0; i < googleReviews.size(); i++) {
                                            final GoogleReview googleReview = (GoogleReview) googleReviews.get(i);
                                            final int reviewId = i;
                                            API.getGoogleAuthorImage(googleReview.authorUrl.substring(googleReview.authorUrl.lastIndexOf('/') + 1), new IRequestableDefaultImpl() {
                                                @Override
                                                public void completed(Object... params) {
                                                    googleReviews.get(reviewId).authorImage = (String) params[0];
                                                    fetchedPlace.reviews.add(googleReviews.get(reviewId));
                                                }
                                            });
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }

            @Override
            public void failed(Object... params) {
                placeDetailsProgress.setVisibility(View.GONE);
                placeDetailsMessage.setVisibility(View.VISIBLE);
                if (params == null || params.length == 0) {
                    Toast.makeText(PlaceDetailsActivity.this, getString(R.string.problems_with_loading), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlaceDetailsActivity.this, (String) params[0], Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PASSED_PLACE, passedPlace);
        if (fetchedPlace != null)
            outState.putSerializable(FETCHED_PLACE, fetchedPlace);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
