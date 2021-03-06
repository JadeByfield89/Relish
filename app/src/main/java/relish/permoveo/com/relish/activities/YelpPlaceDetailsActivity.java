package relish.permoveo.com.relish.activities;

//import android.animation.Animator;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.flurry.android.FlurryAgent;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.viewpagerindicator.CirclePageIndicator;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.pager.FakeInvitePagerAdapter;
import relish.permoveo.com.relish.animation.AnimationUtils;
import relish.permoveo.com.relish.animation.AnimatorPath;
import relish.permoveo.com.relish.animation.PathEvaluator;
import relish.permoveo.com.relish.animation.PathPoint;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.IRequestableDefaultImpl;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.google.GoogleReview;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.model.yelp.YelpReview;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.FlurryConstantUtil;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RatingView;

//import relish.permoveo.com.relish.animation.ViewPropertyAnimator;


public class YelpPlaceDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks {

    public static final String PASSED_YELP_PLACE = "passed_yelp_place_extra";
    public static final String EXTRA_START_DRAWING_LOCATION = "start_drawing_location_extra";
    public static final String SHARED_IMAGE_NAME = "YelpPlaceDetailsActivity:image";

    public final static float SCALE_FACTOR = 13f;
    public final static int ANIMATION_DURATION = 200;
    public final static int MINIMUN_X_DISTANCE = 200;
    private static final String FETCHED_PLACE = "fetched_place_extra";
    private static final int INVITE_FLOW_REQUEST = 228;
    private static final String GOOGLE_DEFAULT_IMAGE = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=250";
    @Bind(R.id.place_details_container)
    RelativeLayout activity_container;
    @Bind(R.id.reveal_container)
    RelativeLayout reveal_container;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.bounce_progress)
    BounceProgressBar bounceProgress;
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
    @Bind(R.id.layout_invite)
    View layoutInvite;
    @Bind(R.id.pager_invite)
    ViewPager invitePager;
    @Bind(R.id.pager_indicator)
    CirclePageIndicator pagerIndicator;
    @Bind(R.id.invite_share_card)
    CardView inviteShareCard;
    @Bind(R.id.ivRatingImage)
    ImageView ratingImage;
    private YelpPlace passedPlace;
    private YelpPlace fetchedPlace;
    private int parallaxImageHeight;
    private int lastScrollY;
    private int mScrollThreshold;
    private int fabHeight;
    private int fabShadowSize;
    private int[] fabLocation, fakeFabLocation;
    private Map<String, ImageView> reviewImageMap;
    private boolean mRevealFlag;
    private FloatingActionButton animatedFab;
    private FakeInvitePagerAdapter invitePagerAdapter;
    private ObjectAnimator revealAnimator;
    private ViewPropertyAnimator fabAnimator;
    private boolean fromInvite;
    private int drawingStartLocation;
    private AnimatorListenerAdapter mEndRevealListener = new AnimatorListenerAdapter() {

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            Log.d("onAnimationEnd", "onAnimationEnd");
            revealAnimator.removeAllListeners();
            revealAnimator.end();
            revealAnimator.cancel();
            fabAnimator.setListener(null);
            fabAnimator.cancel();


            animatedFab.setVisibility(View.INVISIBLE);
            mRevealFlag = false;
            //activity_container.setBackgroundColor(getResources()
            // .getColor(R.color.main_color));
            reveal_container.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);

            /*for (int i = 0; i < activity_container.getChildCount(); i++) {
                View v = activity_container.getChildAt(i);
                android.view.ViewPropertyAnimator animator = v.animate()
                        .scaleX(1).scaleY(1)
                        .setDuration(ANIMATION_DURATION);

                animator.setStartDelay(i * 50);
                animator.start();
            }

            if(!alphaAnimationStarted) {
                Animation in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(500);
                invitePager.setAnimation(in);
                alphaAnimationStarted = true;
            }*/
//            for (int i = 0; i < activity_container.getChildCount(); i++) {
//                View v = activity_container.getChildAt(i);
//                android.view.ViewPropertyAnimator animator = v.animate()
//                        .scaleX(1).scaleY(1)
//                        .setDuration(ANIMATION_DURATION);
//
//                animator.setStartDelay(i * 50);
//                animator.start();
//            }

            startActivityForResult(new Intent(YelpPlaceDetailsActivity.this, InviteFlowActivity.class)
                    .putExtra(InviteFlowActivity.PLACE_FOR_INVITE_EXTRA, fetchedPlace), INVITE_FLOW_REQUEST);
        }
    };

    public static void launch(Activity activity, View transitionImage, YelpPlace place) {
        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
                        Pair.create(transitionImage, SHARED_IMAGE_NAME));
        Intent intent = new Intent(activity, YelpPlaceDetailsActivity.class);
        intent.putExtra(YelpPlaceDetailsActivity.PASSED_YELP_PLACE, place);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void launch(Activity activity, int location, YelpPlace place) {
        Intent intent = new Intent(activity, YelpPlaceDetailsActivity.class);
        intent.putExtra(YelpPlaceDetailsActivity.PASSED_YELP_PLACE, place);
        intent.putExtra(YelpPlaceDetailsActivity.EXTRA_START_DRAWING_LOCATION, location);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);
        ButterKnife.bind(this);

        placeDetailsImage.setVisibility(View.GONE);
        ratingView.setVisibility(View.GONE);
        placeDetailsName.setVisibility(View.GONE);
        placeDetailsAddress.setVisibility(View.GONE);
        placeDetailsPhone.setVisibility(View.GONE);
        placeDetailsReviewsContainer.setVisibility(View.GONE);

//        ViewCompat.setTransitionName(placeDetailsImage, SHARED_IMAGE_NAME);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(FETCHED_PLACE))
                fetchedPlace = (YelpPlace) savedInstanceState.getSerializable(FETCHED_PLACE);
            passedPlace = (YelpPlace) savedInstanceState.getSerializable(PASSED_YELP_PLACE);
        } else if (getIntent().hasExtra(PASSED_YELP_PLACE)) {
            if (getIntent().hasExtra(EXTRA_START_DRAWING_LOCATION)) {
                drawingStartLocation = getIntent().getIntExtra(EXTRA_START_DRAWING_LOCATION, 0);
                placeDetalsScrollView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        placeDetalsScrollView.getViewTreeObserver().removeOnPreDrawListener(this);
                        startIntroAnimation();
                        return true;
                    }
                });
            }

            passedPlace = (YelpPlace) getIntent().getSerializableExtra(PASSED_YELP_PLACE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15.0f);


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

        placeDetalsScrollView.setDrawingCacheEnabled(true);
        placeDetalsScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        reviewImageMap = new HashMap<>();

        toolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.main_color)));
        updateToolbar(toolbar);
        renderFavorite();

        invitePagerAdapter = new FakeInvitePagerAdapter(getSupportFragmentManager());
        invitePager.setAdapter(invitePagerAdapter);
        pagerIndicator.setViewPager(invitePager);
        animatedFab = placeDetailsFab;
    }


    private void startIntroAnimation() {
        placeDetalsScrollView.setScaleY(0.1f);
        placeDetalsScrollView.setPivotY(drawingStartLocation);
//        llAddComment.setTranslationY(100);

        placeDetalsScrollView.animate()
                .scaleY(1)
                .setDuration(500)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {
                        super.onAnimationEnd(animation);
                        animateContent();
                    }
                })
                .start();
    }

    private void animateContent() {
        ArrayList<View> views = new ArrayList<>();
        views.add(placeDetailsImage);
        views.add(ratingView);
        views.add(placeDetailsName);
        views.add(placeDetailsAddress);
        views.add(placeDetailsPhone);
        views.add(placeDetailsReviewsContainer);

        for (int i = 0; i < views.size(); i++) {
            runEnterAnimation(views.get(i), i);
        }
    }

    private void runEnterAnimation(final View view, int position) {
        view.setTranslationY(100);
        view.setAlpha(0.f);
        view.animate()
                .translationY(0).alpha(1.f)
                .setStartDelay(50 * position)
                .setInterpolator(new DecelerateInterpolator(2.f))
                .setListener(new android.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(android.animation.Animator animation) {
                        view.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(android.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(android.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(android.animation.Animator animation) {

                    }
                })
                .setDuration(500)
                .start();
    }

    @OnClick(R.id.yelp_logo)
    public void yelpLogoClicked() {
        openWebView();
    }

    private void renderFavorite() {
        ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(final ParseUser user, ParseException e) {
                if (e == null && user != null) {
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
                    final boolean isFav = isFavorite;

                    placeLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AnimationUtils.animateFavoriteButton(user, placeLike, passedPlace.id, isFav);
                        }
                    });
                } else {
//                    Toast.makeText(PlaceDetailsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void renderPlaceDetails() {
        if (TextUtils.isEmpty(fetchedPlace.image)) {
            placeDetailsImage.setBackgroundColor(getResources().getColor(R.color.photo_placeholder));
        } else {
            Log.d("PlaceDetailsActivity", "Place Image -> " +fetchedPlace.getOriginalImage());
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

        placeDetailsDistance.setText(fetchedPlace.formatDistance() +
                " " +
                getResources().getQuantityString(R.plurals.miles, fetchedPlace.getCalculatedDistance() == 1 ? 1 : 2, fetchedPlace.getCalculatedDistance()));
        placeDetailsDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        placeDetailsDistance.setIncludeFontPadding(false);

        if (!TextUtils.isEmpty(fetchedPlace.phone)) {
            placeDetailsPhone.setVisibility(View.VISIBLE);
            placeDetailsPhone.setPaintFlags(placeDetailsPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(fetchedPlace.phone);
            PhoneNumberUtils.formatNumber(stringBuilder, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
            placeDetailsPhone.setText(stringBuilder.toString());
            placeDetailsPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            placeDetailsPhone.setIncludeFontPadding(false);
            placeDetailsPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + fetchedPlace.phone));
                    YelpPlaceDetailsActivity.this.startActivity(intent);
                }
            });
        } else {
            placeDetailsPhone.setVisibility(View.GONE);
        }
        //ratingView.setLarge(true);
        //ratingView.setRating(fetchedPlace.rating);

        Picasso.with(this).load(fetchedPlace.rating_img_url).fit().into(ratingImage);

        renderReviews();

        placeDetailsReviews.removeAllViews();
        if (fetchedPlace.reviews != null && fetchedPlace.reviews.size() > 0) {
            for (Review review : fetchedPlace.reviews) {
                addReview(review);
            }
        }

        placeDetalsScrollView.setOnTouchListener(null);
        placeDetalsScrollView.setScrollViewCallbacks(this);
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
            } else if (review.getLargeAuthorImage().equals(GOOGLE_DEFAULT_IMAGE)) {
                reviewImage.setImageResource(R.drawable.avatar_placeholder);
            } else {
                Picasso.with(this)
                        .load(review.getLargeAuthorImage()).placeholder(R.drawable.avatar_placeholder).fit().centerCrop()
                        .into(reviewImage);
                Log.d("PlaceDetailsActivity", "Review author image -> " + review.getLargeAuthorImage());
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
        if (fromInvite) {
            animateFromInvite();
            fromInvite = false;
        }

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
                                    if (params.length > 1 && params[1] != null) {
                                        final ArrayList<String> weekdayText = (ArrayList<String>) params[1];
                                        fetchedPlace.weekdayText = weekdayText;
                                    }
                                    if (googleReviews != null && googleReviews.size() > 0) {
                                        for (int i = 0; i < googleReviews.size(); i++) {
                                            final GoogleReview googleReview = (GoogleReview) googleReviews.get(i);
                                            fetchedPlace.reviews.add(googleReview);
                                            final int reviewId = fetchedPlace.reviews.size() - 1;
                                            if (googleReview.getAuthorUrl() != null) {
                                                API.getGoogleAuthorImage(googleReview.getAuthorUrl().substring(googleReview.getAuthorUrl().lastIndexOf('/') + 1), new IRequestableDefaultImpl() {
                                                    @Override
                                                    public void completed(Object... params) {
                                                        fetchedPlace.reviews.get(reviewId).setAuthorImage((String) params[0]);
                                                        renderReviewImage(fetchedPlace.reviews.get(reviewId));
                                                    }
                                                });
                                            }
                                        }
                                        bounceProgress.setVisibility(View.GONE);
                                        placeDetailsMessage.setVisibility(View.GONE);
                                        renderPlaceDetails();
                                    } else {
                                        bounceProgress.setVisibility(View.GONE);
                                        placeDetailsMessage.setVisibility(View.GONE);
                                        renderPlaceDetails();
                                    }
                                }
                            });
                        } else {
                            bounceProgress.setVisibility(View.GONE);
                            placeDetailsMessage.setVisibility(View.GONE);
                            renderPlaceDetails();
                        }
                    }
                });
            }

            @Override
            public void failed(Object... params) {
                bounceProgress.setVisibility(View.GONE);
                placeDetailsMessage.setVisibility(View.VISIBLE);
                if (params == null || params.length == 0) {
                    Toast.makeText(YelpPlaceDetailsActivity.this, getString(R.string.problems_with_loading), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(YelpPlaceDetailsActivity.this, (String) params[0], Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
//        reveal_container.setVisibility(View.GONE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(PASSED_YELP_PLACE, passedPlace);
        if (fetchedPlace != null)
            outState.putSerializable(FETCHED_PLACE, fetchedPlace);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
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
                if (!AnimationUtils.wasAnimatedToBottom || alpha < 0.1f) {
                    int y = fakeFabLocation[1] - fabHeight / 2
                            - (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (int) (placeDetailsFab.getElevation() / 2) : 0)
                            + (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0);
                    AnimationUtils.animateFAB(animatedFab, false, true, y);
                }
            } else {
                //to top
                if (!AnimationUtils.wasAnimatedToTop && alpha < 0.1f) {
                    int y = fabLocation[1] - fabHeight / 2 +
                            (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? (int) (placeDetailsFab.getElevation() / 2) : 0)
                            + (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0)
                            + (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? (int) (fabShadowSize / 2) : 0);
                    AnimationUtils.animateFAB(animatedFab, true, false, y);
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

    @OnClick({R.id.fake_fab_place_details, R.id.fab_place_details})
    public void fabClicked() {

        FlurryAgent.logEvent(FlurryConstantUtil.EVENT.PLACE_DETAILS_FAB_TOUCHED);

        invitePager.setVisibility(View.VISIBLE);
        pagerIndicator.setVisibility(View.VISIBLE);
        inviteShareCard.setVisibility(View.GONE);
        //BlurBuilder.Blur.fastblur(this, placeDetalsScrollView.getDrawingCache(), 5, true);
        animatedFab = AnimationUtils.wasAnimatedToBottom ? fakePlaceDetailsFab : placeDetailsFab;
        if (AnimationUtils.wasAnimatedToBottom) {
            placeDetailsFab.setVisibility(View.GONE);
            fakePlaceDetailsFab.setVisibility(View.VISIBLE);
        } else {
            placeDetailsFab.setVisibility(View.VISIBLE);
            fakePlaceDetailsFab.setVisibility(View.GONE);
        }
        final float startX = animatedFab.getX();

        AnimatorPath path = new AnimatorPath();
        path.moveTo(0, 0);
        path.curveTo(-200, 200, -400, 100, -600, 50);


        revealAnimator = ObjectAnimator.ofObject(this, "fabLoc",
                new PathEvaluator(), path.getPoints().toArray());

        revealAnimator.setInterpolator(new AccelerateInterpolator());
        revealAnimator.setDuration(ANIMATION_DURATION);
        revealAnimator.start();

        revealAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                animatedFab.setImageDrawable(null);
                if (Math.abs(startX - animatedFab.getX()) > MINIMUN_X_DISTANCE) {

                    if (!mRevealFlag) {
                        activity_container.setY(activity_container.getY());

                        fabAnimator = animatedFab.animate()
                                .scaleXBy(SCALE_FACTOR)
                                .scaleYBy(SCALE_FACTOR)
                                .setListener(mEndRevealListener)
                                .setDuration(ANIMATION_DURATION);

                        mRevealFlag = true;
                    }
                }
            }
        });
    }

    /**
     * We need this setter to translate between the information the animator
     * produces (a new "PathPoint" describing the current animated location)
     * and the information that the button requires (an xy location). The
     * setter will be called by the ObjectAnimator given the 'fabLoc'
     * property string.
     */
    public void setFabLoc(PathPoint newLoc) {
        animatedFab.setTranslationX(newLoc.mX);

        if (mRevealFlag)
            animatedFab.setTranslationY(newLoc.mY);
        else
            animatedFab.setTranslationY(newLoc.mY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INVITE_FLOW_REQUEST) {
            fromInvite = true;

            if (data != null) {

                boolean isSent = data.getBooleanExtra(InviteFlowActivity.IS_INVITE_SENT_EXTRA, false);
                if (isSent) {
                    invitePager.setVisibility(View.GONE);
                    pagerIndicator.setVisibility(View.GONE);
                    inviteShareCard.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void animateFromInvite() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                reveal_container.setVisibility(View.INVISIBLE);
                animatedFab.setScaleX(SCALE_FACTOR);
                animatedFab.setScaleY(SCALE_FACTOR);
                animatedFab.setVisibility(View.VISIBLE);

                ViewPropertyAnimator scaleAnimator = animatedFab.animate()
                        .scaleXBy(-SCALE_FACTOR + 1.0f)
                        .scaleYBy(-SCALE_FACTOR + 1.0f)
                        .setDuration(500);
                animateCurveFromInvite();
            }
        }, 500);
    }

    private void animateCurveFromInvite() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimatorPath path = new AnimatorPath();
                path.moveTo(-600, 50);
                path.curveTo(-200, 200, -400, 100, 0, 0);

                revealAnimator = ObjectAnimator.ofObject(YelpPlaceDetailsActivity.this, "fabLoc",
                        new PathEvaluator(), path.getPoints().toArray());

                revealAnimator.setInterpolator(new DecelerateInterpolator());
                revealAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        animatedFab.setImageResource(R.drawable.ic_send);
                        Animation toolbarAnimation = new AlphaAnimation(0.0f, 1.0f);
                        toolbarAnimation.setDuration(300);
                        toolbarAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                toolbar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        toolbar.setAnimation(toolbarAnimation);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                revealAnimator.setDuration(ANIMATION_DURATION);
                revealAnimator.start();
            }
        }, 300);
    }

    private class PlaceDetailsCallback extends IRequestableDefaultImpl {

        @Override
        public void failed(Object... params) {
            bounceProgress.setVisibility(View.GONE);
            placeDetailsMessage.setVisibility(View.GONE);
            renderPlaceDetails();
        }
    }
}

