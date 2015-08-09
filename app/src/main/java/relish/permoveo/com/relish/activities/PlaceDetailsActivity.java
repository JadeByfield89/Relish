package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.IRequestableDefaultImpl;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.google.GoogleReview;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.model.yelp.YelpReview;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.RatingView;

public class PlaceDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks {

    public static final String PASSED_PLACE = "passed_place_extra";
    private static final String FETCHED_PLACE = "fetched_place_extra";

    private YelpPlace passedPlace;
    private YelpPlace fetchedPlace;
    private int parallaxImageHeight;
    private int lastScrollY;
    private int mScrollThreshold;
    private int fabHeight;
    private int fabShadowSize;
    private boolean wasAnimatedToBottom = false;
    private boolean wasAnimatedToTop = false;
    private int[] fabLocation, fakeFabLocation;
    private Map<String, ImageView> reviewImageMap;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.place_details_progress)
    ProgressWheel placeDetailsProgress;

    @Bind(R.id.place_details_inform_message)
    TextView placeDetailsMessage;

    @Bind(R.id.header_place_details_image)
    KenBurnsView placeDetailsImage;

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

    @Bind(R.id.place_details_reviews)
    LinearLayout placeDetailsReviews;

    @Bind(R.id.place_like)
    ImageView placeLike;

    @Bind(R.id.yelp_logo)
    ImageView yelpLogo;

    private class PlaceDetailsCallback extends IRequestableDefaultImpl {

        @Override
        public void failed(Object... params) {
            placeDetailsProgress.setVisibility(View.GONE);
            placeDetailsMessage.setVisibility(View.GONE);
            renderPlaceDetails();
        }
    }

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
        getSupportActionBar().setElevation(7.0f);


        SpannableString s = new SpannableString(passedPlace.name);
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);
        mScrollThreshold = getResources().getDimensionPixelOffset(R.dimen.fab_threshold);
        fabHeight = getResources().getDimensionPixelOffset(R.dimen.fab_size);
        fabShadowSize = getResources().getDimensionPixelSize(R.dimen.fab_shadow_size);

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

        placeDetalsScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        reviewImageMap = new HashMap<>();

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            toolbar.setPadding(toolbar.getPaddingLeft(), getStatusBarHeight(), toolbar.getPaddingRight(), toolbar.getPaddingBottom());
            toolbar.requestLayout();
            updateStatusBar(getResources().getColor(R.color.place_image_dim));
        } else {
            updateStatusBar(getResources().getColor(R.color.main_color_dark));
        }

        renderFavorite();

        updateStatusBar(getResources().getColor(R.color.main_color_dark));

    }

    @OnClick({R.id.fake_fab_place_details, R.id.fab_place_details})
    public void fabClicked() {

    }

    @OnClick(R.id.yelp_logo)
    public void yelpLogoClicked() {
        openWebView();
    }

    private void renderFavorite() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                final ParseUser user = (ParseUser) parseObject;
                ArrayList<String> favorites = (ArrayList<String>) user.get("favoritePlaces");
                if (favorites == null)
                    favorites = new ArrayList<>();
                boolean isFavorite = false;
                for (String id : favorites) {
                    if (passedPlace.id.equals(id)) {
                        isFavorite = true;
                    }
                }

                if (isFavorite) {
                    placeLike.setImageResource(R.drawable.ic_favorite_selected);
                } else {
                    placeLike.setImageResource(R.drawable.ic_favorite);
                }
                placeLike.setTag(isFavorite);

                placeLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        YoYo.with(Techniques.BounceIn)
                                .duration(500)
                                .withListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        ArrayList<String> favorites = (ArrayList<String>) user.get("favoritePlaces");
                                        if (favorites == null)
                                            favorites = new ArrayList<>();
                                        if ((Boolean) placeLike.getTag()) {
                                            favorites.remove(passedPlace.id);
                                            placeLike.setImageResource(R.drawable.ic_favorite);
                                        } else {
                                            favorites.add(passedPlace.id);
                                            placeLike.setImageResource(R.drawable.ic_favorite_selected);
                                        }
                                        placeLike.setTag(!(Boolean) placeLike.getTag());
                                        user.put("favoritePlaces", favorites);
                                        user.saveEventually();
                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .playOn(placeLike);
                    }
                });
            }
        });
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
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fetchedPlace.phone);
            PhoneNumberUtils.formatNumber(stringBuilder, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
            placeDetailsPhone.setText(stringBuilder.toString());
            placeDetailsPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            placeDetailsPhone.setIncludeFontPadding(false);
        } else {
            placeDetailsPhone.setVisibility(View.GONE);
        }
        ratingView.setLarge(true);
        ratingView.setRating(fetchedPlace.rating);

        renderReviews();

        placeDetailsReviews.removeAllViews();
        if (fetchedPlace.reviews != null && fetchedPlace.reviews.size() > 0) {
            for (Review review : fetchedPlace.reviews) {
                addReview(review);
            }
        }

        placeDetalsScrollView.setOnTouchListener(null);
        placeDetalsScrollView.setScrollViewCallbacks(this);
        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
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

    private void renderReviewImage(Review review) {
        ImageView reviewImage = reviewImageMap.get(review.getAuthorName());
        if (reviewImage != null) {
            if (TextUtils.isEmpty(review.getAuthorImage())) {
                reviewImage.setImageResource(R.drawable.avatar_placeholder);
            } else {
                Picasso.with(this)
                        .load(review.getLargeAuthorImage())
                        .into(reviewImage);
            }
        }
    }

    private void addReview(final Review review) {
        View reviewView = getLayoutInflater().inflate(R.layout.review_list_item, null);
        TextView reviewName = (TextView) reviewView.findViewById(R.id.review_name);
        TextView reviewText = (TextView) reviewView.findViewById(R.id.review_text);
        TextView reviewDate = (TextView) reviewView.findViewById(R.id.review_date);
        ImageView reviewImage = (ImageView) reviewView.findViewById(R.id.review_image);
        ImageView yelpReviewRating = (ImageView) reviewView.findViewById(R.id.review_rating_yelp);
        TextView readMore = (TextView) reviewView.findViewById(R.id.review_more);
        RatingView reviewRating = (RatingView) reviewView.findViewById(R.id.review_rating);

        reviewImageMap.put(review.getAuthorName(), reviewImage);
        renderReviewImage(review);

        if (TextUtils.isEmpty(review.getText())) {
            reviewText.setVisibility(View.GONE);
        } else {
            reviewText.setVisibility(View.VISIBLE);
            reviewText.setText(review.getText());
            reviewText.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            reviewText.setIncludeFontPadding(false);
        }

        DateTime reviewDateTime = new DateTime().withMillis(review.getTime() * 1000);
        DateTime now = new DateTime();
        Interval interval =
                new Interval(reviewDateTime, now);
        Period period = interval.toPeriod();
        String formattedTime = null;

        if (period.getYears() != 0) {
            formattedTime = period.getYears() + " " + getResources().getQuantityString(R.plurals.years, period.getYears());
        } else if (period.getMonths() != 0) {
            formattedTime = period.getMonths() + " " + getResources().getQuantityString(R.plurals.months, period.getMonths());
        } else if (period.getDays() != 0) {
            formattedTime = period.getDays() + " " + getResources().getQuantityString(R.plurals.days, period.getDays());
        } else {
            formattedTime = "Today";
        }

        reviewDate.setText(formattedTime);
        reviewDate.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        reviewDate.setIncludeFontPadding(false);

        reviewName.setText(review.getAuthorName());
        reviewName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        reviewName.setIncludeFontPadding(false);

        if (review instanceof YelpReview) {
            readMore.setVisibility(View.VISIBLE);
            reviewRating.setVisibility(View.GONE);

            if (review.getRating() == 0.0f) {
                yelpReviewRating.setVisibility(View.GONE);
            } else {
                yelpReviewRating.setVisibility(View.VISIBLE);
                Picasso.with(this)
                        .load(review.getRatingImage())
                        .into(yelpReviewRating);
            }

            readMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openWebView();
                }
            });
        } else {
            readMore.setVisibility(View.GONE);
            yelpReviewRating.setVisibility(View.GONE);

            if (review.getRating() == 0.0f) {
                reviewRating.setVisibility(View.GONE);
            } else {
                reviewRating.setVisibility(View.VISIBLE);
                reviewRating.setRating(review.getRating());
            }
        }

        placeDetailsReviews.addView(reviewView);
    }

    private void openWebView() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(passedPlace.url));
        startActivity(i);
    }


    @Override
    protected void onResume() {
        super.onResume();
        API.getYelpPlaceDetails(passedPlace.id, new IRequestable() {
            @Override
            public void completed(Object... params) {
                fetchedPlace = (YelpPlace) params[0];
                fetchedPlace.distance = passedPlace.distance;
                API.googleSearch(passedPlace, new PlaceDetailsCallback() {
                    @Override
                    public void completed(Object... params) {
                        ArrayList<GooglePlace> places = (ArrayList<GooglePlace>) params[0];
                        if (places != null && places.size() > 0) {
                            GooglePlace matching = places.get(0);
                            API.getGooglePlaceDetails(matching.reference, new PlaceDetailsCallback() {
                                @Override
                                public void completed(Object... params) {
                                    final ArrayList<Review> googleReviews = (ArrayList<Review>) params[0];
                                    if (googleReviews != null && googleReviews.size() > 0) {
                                        for (int i = 0; i < googleReviews.size(); i++) {
                                            final GoogleReview googleReview = (GoogleReview) googleReviews.get(i);
                                            fetchedPlace.reviews.add(googleReview);
                                            final int reviewId = fetchedPlace.reviews.size() - 1;
                                            API.getGoogleAuthorImage(googleReview.getAuthorUrl().substring(googleReview.getAuthorUrl().lastIndexOf('/') + 1), new IRequestableDefaultImpl() {
                                                @Override
                                                public void completed(Object... params) {
                                                    fetchedPlace.reviews.get(reviewId).setAuthorImage((String) params[0]);
                                                    renderReviewImage(fetchedPlace.reviews.get(reviewId));
                                                }
                                            });
                                        }
                                        placeDetailsProgress.setVisibility(View.GONE);
                                        placeDetailsMessage.setVisibility(View.GONE);
                                        renderPlaceDetails();
                                    } else {
                                        placeDetailsProgress.setVisibility(View.GONE);
                                        placeDetailsMessage.setVisibility(View.GONE);
                                        renderPlaceDetails();
                                    }
                                }
                            });
                        } else {
                            placeDetailsProgress.setVisibility(View.GONE);
                            placeDetailsMessage.setVisibility(View.GONE);
                            renderPlaceDetails();
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
        ViewHelper.setTranslationY(placeDetailsImage, scrollY / 2);

//        float damping = 0.5f;
//        int dampedScroll = (int) (scrollY * damping);
//        int offset = lastDumpedScroll - dampedScroll;
//        placeDetailsImage.offsetTopAndBottom(-offset);
//
//        lastDumpedScroll = dampedScroll;

        //fab animation
        boolean isSignificantDelta = Math.abs(scrollY - lastScrollY) > mScrollThreshold;
        if (isSignificantDelta) {
            if (scrollY > lastScrollY) {
                //to bottom
                if (!wasAnimatedToBottom) {
                    int y = fakeFabLocation[1] - fabHeight / 2
                            - (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (int) (placeDetailsFab.getElevation() / 2) : 0)
                            + (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0);
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
                    int y = fabLocation[1] - fabHeight / 2 +
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (int) (placeDetailsFab.getElevation() / 2) : 0)
                            + (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0)
                            + (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? (int) (fabShadowSize / 2) : 0);
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
