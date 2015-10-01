package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.IRequestableDefaultImpl;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.google.GoogleReview;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RatingView;

public class GooglePlaceDetailsActivity extends RelishActivity implements ObservableScrollViewCallbacks {

    public static final String PASSED_GOOGLE_PLACE = "passed_google_place_extra";
    private static final String GOOGLE_DEFAULT_IMAGE = "https://lh3.googleusercontent.com/-XdUIqdMkCWA/AAAAAAAAAAI/AAAAAAAAAAA/4252rscbv5M/photo.jpg?sz=250";

    private static final String FETCHED_PLACE = "fetched_place_extra";
    @Bind(R.id.place_details_container)
    RelativeLayout activity_container;
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
    @Bind(R.id.map)
    MapView map;

    private GoogleMap mMap;
    private GooglePlace passedPlace;
    private int parallaxImageHeight;
    private Map<String, ImageView> reviewImageMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_place_details);
        ButterKnife.bind(this);

        map.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            passedPlace = (GooglePlace) savedInstanceState.getSerializable(PASSED_GOOGLE_PLACE);
        } else if (getIntent().hasExtra(PASSED_GOOGLE_PLACE)) {
            passedPlace = (GooglePlace) getIntent().getSerializableExtra(PASSED_GOOGLE_PLACE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(15.0f);


        SpannableString s = new SpannableString(passedPlace.name);
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        parallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.featured_image_size);

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null && map.isShown()) {
            mMap = map.getMap();
            MapsInitializer.initialize(this);

            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        if (passedPlace.geometry != null) {
            toolbar.setBackgroundColor(getResources().getColor(R.color.main_color));
            mMap.addMarker(new MarkerOptions().position(new LatLng(passedPlace.geometry.location.lat, passedPlace.geometry.location.lng)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(passedPlace.geometry.location.lat, passedPlace.geometry.location.lng), 17.0f), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
//                        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
//                            @Override
//                            public void onSnapshotReady(Bitmap bitmap) {
//                                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
//                                // path to /data/data/yourapp/app_data/imageDir
//                                File directory = cw.getDir("images", Context.MODE_PRIVATE);
//                                File snapshotPath = new File(directory, "snapshot.png");
//
//                                FileOutputStream fos = null;
//                                try {
//                                    fos = new FileOutputStream(snapshotPath);
//                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
//                                    fos.close();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                creator.getInvite().mapSnapshot = snapshotPath.getAbsolutePath();
//                            }
//                        });
                }

                @Override
                public void onCancel() {

                }
            });
        }
    }

    @Override
    protected void onResume() {
        map.onResume();
        super.onResume();
        API.getGooglePlaceDetails(passedPlace.reference, new PlaceDetailsCallback() {
            @Override
            public void completed(Object... params) {
                final ArrayList<Review> googleReviews = (ArrayList<Review>) params[0];
                if (googleReviews != null && googleReviews.size() > 0) {
                    for (int i = 0; i < googleReviews.size(); i++) {
                        final GoogleReview googleReview = (GoogleReview) googleReviews.get(i);
                        passedPlace.reviews.add(googleReview);
                        final int reviewId = passedPlace.reviews.size() - 1;
                        if (googleReview.getAuthorUrl() != null) {
                            API.getGoogleAuthorImage(googleReview.getAuthorUrl().substring(googleReview.getAuthorUrl().lastIndexOf('/') + 1), new IRequestableDefaultImpl() {
                                @Override
                                public void completed(Object... params) {
                                    passedPlace.reviews.get(reviewId).setAuthorImage((String) params[0]);
                                    renderReviewImage(passedPlace.reviews.get(reviewId));
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
        setUpMapIfNeeded();
    }

    private void renderPlaceDetails() {
        if (TextUtils.isEmpty(passedPlace.getLargeImage())) {
            placeDetailsImage.setVisibility(View.GONE);
            map.setVisibility(View.VISIBLE);
            setUpMapIfNeeded();
        } else {
            Log.d("PlaceDetailsActivity", "Place Image -> " + passedPlace.getLargeImage());
            Picasso.with(this)
                    .load(passedPlace.getLargeImage())
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

        placeDetailsName.setText(passedPlace.name);
        placeDetailsName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        placeDetailsName.setIncludeFontPadding(false);

        placeDetailsAddress.setText(passedPlace.address);
        placeDetailsAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        placeDetailsAddress.setIncludeFontPadding(false);

        placeDetailsDistance.setText(passedPlace.formatDistance() +
                " " +
                getResources().getQuantityString(R.plurals.miles, passedPlace.getCalculatedDistance() == 1 ? 1 : 2, passedPlace.getCalculatedDistance()));
        placeDetailsDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        placeDetailsDistance.setIncludeFontPadding(false);

        if (!TextUtils.isEmpty(passedPlace.phone)) {
            placeDetailsPhone.setVisibility(View.VISIBLE);
            placeDetailsPhone.setPaintFlags(placeDetailsPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(passedPlace.phone);
            PhoneNumberUtils.formatNumber(stringBuilder, PhoneNumberUtils.getFormatTypeForLocale(Locale.US));
            placeDetailsPhone.setText(stringBuilder.toString());
            placeDetailsPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            placeDetailsPhone.setIncludeFontPadding(false);
            placeDetailsPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + passedPlace.phone));
                    GooglePlaceDetailsActivity.this.startActivity(intent);
                }
            });
        } else {
            placeDetailsPhone.setVisibility(View.GONE);
        }
        ratingView.setLarge(true);
        ratingView.setRating((float) passedPlace.rating);

        renderReviews();

        placeDetailsReviews.removeAllViews();
        if (passedPlace.reviews != null && passedPlace.reviews.size() > 0) {
            for (Review review : passedPlace.reviews) {
                addReview(review);
            }
        }

        placeDetalsScrollView.setOnTouchListener(null);
        placeDetalsScrollView.setScrollViewCallbacks(this);
    }

    private void renderReviews() {
        if (passedPlace.reviews != null && passedPlace.reviews.size() > 0) {
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

        readMore.setVisibility(View.GONE);
        yelpReviewRating.setVisibility(View.GONE);

        if (review.getRating() == 0.0f) {
            reviewRating.setVisibility(View.GONE);
        } else {
            reviewRating.setVisibility(View.VISIBLE);
            reviewRating.setRating(review.getRating());
        }

        placeDetailsReviews.addView(reviewView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);

        outState.putSerializable(PASSED_GOOGLE_PLACE, passedPlace);
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
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

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
