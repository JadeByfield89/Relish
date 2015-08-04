package relish.permoveo.com.relish.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.ReviewsAdapter;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.IRequestableDefaultImpl;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.model.Yelp.YelpPlace;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.google.GoogleReview;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.NonScrollListView;
import relish.permoveo.com.relish.view.RatingView;

public class PlaceDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks {

    public static final String PASSED_PLACE = "passed_place_extra";
    private static final String FETCHED_PLACE = "fetched_place_extra";

    private YelpPlace passedPlace;
    private YelpPlace fetchedPlace;
    private int parallaxImageHeight;
    private int lastDumpedScroll;
    private int lastScrollY;
    private int mScrollThreshold;
    private int fabHeight;
    private boolean wasAnimatedToBottom = false;
    private boolean wasAnimatedToTop = false;
    private int[] fabLocation, fakeFabLocation;
    private ReviewsAdapter adapter;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.place_details_progress)
    ProgressWheel placeDetailsProgress;

    @Bind(R.id.place_details_inform_message)
    TextView placeDetailsMessage;

    @Bind(R.id.header_place_details_image)
    ImageView placeDetailsImage;

    @Bind(R.id.place_details_scroll_view)
    ObservableScrollView placeDetalsScrollView;

    @Bind(R.id.fab_place_details)
    FloatingActionButton placeDetailsFab;

    @Bind(R.id.fake_fab_place_details)
    FloatingActionButton fakePlaceDetailsFab;

    @Bind(R.id.place_details_rating_view)
    RatingView ratingView;

    @Bind(R.id.place_details_address)
    TextView placeDetailsAddress;

    @Bind(R.id.place_details_distance)
    TextView placeDetailsDistance;

    @Bind(R.id.place_details_phone)
    TextView placeDetailsPhone;

    @Bind(R.id.place_details_name)
    TextView placeDetailsName;

    @Bind(R.id.place_details_reviews_title)
    TextView placeDetailsReviewsTitle;

    @Bind(R.id.place_details_reviews_container)
    LinearLayout placeDetailsReviewsContainer;

    @Bind(R.id.place_details_reviews_list)
    NonScrollListView placeDetailsReviews;

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

        placeDetalsScrollView.setScrollViewCallbacks(this);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);
        mScrollThreshold = getResources().getDimensionPixelOffset(R.dimen.fab_threshold);
        fabHeight = getResources().getDimensionPixelOffset(R.dimen.fab_size);

        placeDetailsFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    placeDetailsFab.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    placeDetailsFab.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                fabLocation = new int[2];
                placeDetailsFab.getLocationOnScreen(fabLocation);
            }
        });

        fakePlaceDetailsFab.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    fakePlaceDetailsFab.getViewTreeObserver()
                            .removeOnGlobalLayoutListener(this);
                } else {
                    fakePlaceDetailsFab.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                }
                fakeFabLocation = new int[2];
                fakePlaceDetailsFab.getLocationOnScreen(fakeFabLocation);
            }
        });

        adapter = new ReviewsAdapter(this);
        placeDetailsReviews.setAdapter(adapter);

        updateStatusBar(getResources().getColor(R.color.main_color_dark));
    }

    @OnClick({R.id.fake_fab_place_details, R.id.fab_place_details})
    public void fabClicked() {

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

        placeDetailsName.setText(fetchedPlace.name);
        placeDetailsName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        placeDetailsName.setIncludeFontPadding(false);

        placeDetailsAddress.setText(fetchedPlace.location.address);
        placeDetailsAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        placeDetailsAddress.setIncludeFontPadding(false);

        int quantity = (int) Math.floor(fetchedPlace.getCalculatedDistance());
        placeDetailsDistance.setText(fetchedPlace.formatDistance() +
                " " +
                getResources().getQuantityString(R.plurals.miles, quantity, fetchedPlace.getCalculatedDistance()));
        placeDetailsDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        placeDetailsDistance.setIncludeFontPadding(false);

        if (!TextUtils.isEmpty(fetchedPlace.phone)) {
            placeDetailsPhone.setVisibility(View.VISIBLE);
            placeDetailsPhone.setText(fetchedPlace.phone);
            placeDetailsPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            placeDetailsPhone.setIncludeFontPadding(false);
        } else {
            placeDetailsPhone.setVisibility(View.GONE);
        }
        ratingView.setLarge(true);
        ratingView.setRating(fetchedPlace.rating);

        renderReviews();

        if (fetchedPlace.reviews != null && fetchedPlace.reviews.size() > 0)
            adapter.swap(fetchedPlace.reviews);
    }

    private void renderReviews() {
        if (fetchedPlace.reviews != null && fetchedPlace.reviews.size() > 0) {
            placeDetailsReviewsContainer.setVisibility(View.VISIBLE);

            placeDetailsReviewsTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
            placeDetailsReviewsTitle.setIncludeFontPadding(false);
        } else {
            placeDetailsReviewsContainer.setVisibility(View.GONE);
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
                                                    adapter.add(googleReviews.get(reviewId));
                                                    renderReviews();
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
        // toolbar animation
        int baseColor = getResources().getColor(R.color.main_color);
        float alpha = Math.min(1, (float) scrollY / parallaxImageHeight);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));

        float damping = 0.5f;
        int dampedScroll = (int) (scrollY * damping);
        int offset = lastDumpedScroll - dampedScroll;
        placeDetailsImage.offsetTopAndBottom(-offset);

        lastDumpedScroll = dampedScroll;

        //fab animation
        boolean isSignificantDelta = Math.abs(scrollY - lastScrollY) > mScrollThreshold;
        if (isSignificantDelta) {
            if (scrollY > lastScrollY) {
                //to bottom
                if (!wasAnimatedToBottom) {
                    int y = fakeFabLocation[1] - fabHeight / 2;
                    ViewPropertyAnimator.animate(placeDetailsFab).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(300)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    wasAnimatedToBottom = true;
                                    wasAnimatedToTop = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .y(y);
                }
            } else {
                //to top
                if (!wasAnimatedToTop && alpha < 0.1f) {
                    int y = fabLocation[1] - fabHeight / 2;
                    ViewPropertyAnimator.animate(placeDetailsFab).setInterpolator(new AccelerateDecelerateInterpolator())
                            .setDuration(300)
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    wasAnimatedToTop = true;
                                    wasAnimatedToBottom = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .y(y);
                }
            }
        }
        lastScrollY = scrollY;
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
