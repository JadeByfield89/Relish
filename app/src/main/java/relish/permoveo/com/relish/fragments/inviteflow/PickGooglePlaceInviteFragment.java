package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.GooglePlaceDetailsActivity;
import relish.permoveo.com.relish.adapter.list.inviteflow.PlacesAutocompleteAdapter;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.util.RecyclerItemClickListener;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;
import relish.permoveo.com.relish.view.RatingView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickGooglePlaceInviteFragment extends Fragment {

    private static final String PLACE_FOR_INVITE = "place_for_invite";

    @Bind(R.id.invite_pick_place_card)
    RelativeLayout invitePickPlaceCard;

    @Bind(R.id.invite_place)
    EditText invitePlace;

    @Bind(R.id.search_icon)
    ImageView search;

    @Bind(R.id.close_icon)
    ImageView close;

    @Bind(R.id.bounce_progress)
    BounceProgressBar bounceProgressBar;

    @Bind(R.id.my_location_icon)
    ImageView myLocation;

    @Bind(R.id.invite_place_rating)
    RatingView ratingView;

    @Bind(R.id.invite_place_address)
    TextView invitePlaceAddress;

    @Bind(R.id.invite_place_hours)
    TextView invitePlaceHours;

    @Bind(R.id.invite_place_name)
    TextView invitePlaceName;

    @Bind(R.id.button_next)
    Button next;

    @Bind(R.id.search_container)
    RelativeLayout searchContainer;

    @Bind(R.id.invite_place_info_container)
    LinearLayout placeInfoContainer;

    @Bind(R.id.map)
    MapView mapView;

    @Bind(R.id.autcomplete_places_list)
    RecyclerView recyclerView;

    @Bind(R.id.bottom_shadow)
    View shadowBottom;

    private GoogleMap mMap;
    private Marker placeMarker;
    private PagerCallbacks pagerCallbacks;
    private int wholeHeight;
    private InviteCreator creator;
    private PlacesAutocompleteAdapter adapter;

    private TextWatcher searchQueryListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!TextUtils.isEmpty(s)) {
                close.setVisibility(View.GONE);
                bounceProgressBar.setVisibility(View.VISIBLE);
                API.googlePlacesAutocomplete(s.toString(), new IRequestable() {
                    @Override
                    public void completed(Object... params) {
                        close.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);

                        googlePlaces = (List<GooglePlace>) params[0];
                        if (googlePlaces != null && googlePlaces.size() != 0) {
                            adapter.swap(new ArrayList<>(googlePlaces));
                            collapseMapWithPlaces();
                        }
                    }

                    @Override
                    public void failed(Object... params) {
                        close.setVisibility(View.VISIBLE);
                        bounceProgressBar.setVisibility(View.GONE);

                        if (params != null && params.length > 0) {
                            YoYo.with(Techniques.Shake)
                                    .playOn(invitePickPlaceCard);
                            Snackbar.make(invitePickPlaceCard, (String) params[0], Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                expandMapWithPlaces(null);
//                    adapter.clear();
            }
        }
    };

    private GooglePlace googlePlace;
    private List<GooglePlace> googlePlaces;
    public PickGooglePlaceInviteFragment() {
        // Required empty public constructor
    }

    public static PickGooglePlaceInviteFragment newInstance(YelpPlace place) {
        PickGooglePlaceInviteFragment pickGooglePlaceInviteFragment = new PickGooglePlaceInviteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLACE_FOR_INVITE, place);
        pickGooglePlaceInviteFragment.setArguments(bundle);
        return pickGooglePlaceInviteFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof PagerCallbacks)
            pagerCallbacks = (PagerCallbacks) activity;

        if (activity instanceof InviteCreator)
            creator = (InviteCreator) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new PlacesAutocompleteAdapter(getActivity());
//        if (getArguments() != null) {
//            currentPlace = (YelpPlace) getArguments().getSerializable(PLACE_FOR_INVITE);
//        }

        googlePlace = new GooglePlace();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_google_place_invite, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Fragment fragment = (getFragmentManager().findFragmentById(R.id.map));
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        mapView.onCreate(savedInstanceState);

        invitePickPlaceCard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    invitePickPlaceCard.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    invitePickPlaceCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                wholeHeight = invitePickPlaceCard.getHeight() - searchContainer.getHeight() - next.getHeight();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                invitePlace.clearFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(invitePlace.getWindowToken(), 0);

                GooglePlace place = (GooglePlace) adapter.getItem(position);
                googlePlace = place;

                animateMapToPlace(place);

                invitePlace.setText(place.name);
                invitePlaceName.setText(place.name);
                invitePlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
                invitePlaceName.setIncludeFontPadding(false);

                invitePlaceAddress.setText(place.address);
                invitePlaceAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
                invitePlaceAddress.setIncludeFontPadding(false);

                ratingView.setRating((float) place.rating);

                invitePlaceHours.setText(place.getHours());
                invitePlaceHours.setTypeface(TypefaceUtil.PROXIMA_NOVA);
                invitePlaceHours.setIncludeFontPadding(false);
                placeInfoContainer.setVisibility(View.INVISIBLE);
                placeInfoContainer.requestLayout();
                expandMapWithPlaces(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        collapseMapWithDetails();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }
        }));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeInfoContainer.isShown() && googlePlace != null) {
                    creator.updateInviteWithGooglePlace(googlePlace);
                    if (pagerCallbacks != null)
                        pagerCallbacks.next();
                } else {
                    YoYo.with(Techniques.Shake)
                            .playOn(invitePickPlaceCard);
                    Snackbar.make(invitePlace, "Please select a location", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        invitePlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myLocation.setVisibility(View.GONE);
                    close.setVisibility(View.VISIBLE);
                }
            }
        });

        invitePlace.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    invitePlace.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(invitePlace.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        invitePlace.addTextChangedListener(searchQueryListener);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(invitePlace.getText())) {
                    invitePlace.setText("");
                    invitePlace.requestFocus();
                    if (placeMarker != null)
                        placeMarker.remove();

                    adapter.clear();
                    expandMapWithPlaces(null);
                    expandMapWithDetails();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(invitePlace, 0);
                } else {
                    invitePlace.clearFocus();
                    close.setVisibility(View.GONE);
                    myLocation.setVisibility(View.VISIBLE);

                    adapter.clear();
                    expandMapWithPlaces(null);

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(invitePlace.getWindowToken(), 0);
                }
            }
        });

        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GPSTracker.get.getLocation() != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude()), 17.0f));
                }
            }
        });

        invitePlace.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        invitePlace.setIncludeFontPadding(false);

        if (creator.getInvite() != null && creator.getInvite().location != null) {
            placeInfoContainer.setVisibility(View.VISIBLE);

            invitePlace.removeTextChangedListener(searchQueryListener);
            invitePlace.setText(creator.getInvite().name);
            invitePlaceName.setText(creator.getInvite().name);
            invitePlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
            invitePlaceName.setIncludeFontPadding(false);

            invitePlaceAddress.setText(creator.getInvite().location.address);
            invitePlaceAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            invitePlaceAddress.setIncludeFontPadding(false);

            ratingView.setRating(creator.getInvite().rating);

//            invitePlaceHours.setText(creator.getInvite().getHours());
//            invitePlaceHours.setTypeface(TypefaceUtil.PROXIMA_NOVA);
//            invitePlaceHours.setIncludeFontPadding(false);

            myLocation.setVisibility(View.GONE);
            googlePlace = GooglePlace.from(creator.getInvite());
//            placeInfoContainer.setVisibility(View.INVISIBLE);
//            placeInfoContainer.requestLayout();
//            expandMapWithPlaces(new Animator.AnimatorListener() {
//                @Override
//                public void onAnimationStart(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    collapseMapWithDetails();
//                }
//
//                @Override
//                public void onAnimationCancel(Animator animation) {
//
//                }
//
//                @Override
//                public void onAnimationRepeat(Animator animation) {
//
//                }
//            });
            invitePlace.addTextChangedListener(searchQueryListener);
        } else {
            placeInfoContainer.setVisibility(View.GONE);
            ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, R.id.button_next);
//            ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96.0f, getResources().getDisplayMetrics());
            mapView.requestLayout();

            ((RelativeLayout.LayoutParams) searchContainer.getLayoutParams()).height = RelativeLayout.LayoutParams.WRAP_CONTENT;
            searchContainer.requestLayout();

            invitePlace.clearFocus();
            invitePlace.setVisibility(View.VISIBLE);
            search.setVisibility(View.VISIBLE);
            close.setVisibility(View.GONE);
            myLocation.setVisibility(View.VISIBLE);
        }

        placeInfoContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googlePlace != null)
                    startActivity(new Intent(getActivity(), GooglePlaceDetailsActivity.class)
                            .putExtra(GooglePlaceDetailsActivity.PASSED_GOOGLE_PLACE, googlePlace));
            }
        });

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = mapView.getMap();
            MapsInitializer.initialize(this.getActivity());

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

        if (creator.getInvite() != null && creator.getInvite().location != null) {
            if (placeMarker == null) {
                placeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng), 17.0f), new GoogleMap.CancelableCallback() {
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
        } else {
            if (GPSTracker.get.getLocation() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude()), 17.0f));
            }
        }
    }

    private void expandMapWithPlaces(final Animator.AnimatorListener listener) {
        if (recyclerView.isShown()) {
            placeInfoContainer.setVisibility(View.GONE);
            int collapsedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 104.0f, getResources().getDisplayMetrics());
            int expandedHeight = mapView.getHeight() + recyclerView.getHeight();

            ValueAnimator mapHeightAnimator = ValueAnimator.ofInt(collapsedHeight, expandedHeight);
            mapHeightAnimator.setDuration(700);
            mapHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mapView.getLayoutParams().height = value.intValue();
                    mapView.requestLayout();
                }
            });
            mapHeightAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, R.id.button_next);
                    mapView.requestLayout();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(recyclerView, "alpha", 1, 0);
            ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(recyclerView, "translationY", 0, expandedHeight - collapsedHeight);
            fadeAnimator.setDuration(700);
            slideAnimator.setDuration(700);


            AnimatorSet set = new AnimatorSet();
            set.setDuration(700);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(View.GONE);
                    if (listener != null)
                        listener.onAnimationEnd(animation);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.playTogether(mapHeightAnimator, slideAnimator, fadeAnimator);
            set.start();
        }
    }

    private void animateMapToPlace(GooglePlace place) {
        mMap.clear();
        placeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(place.geometry.location.lat, place.geometry.location.lng)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.geometry.location.lat, place.geometry.location.lng), 17.0f), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });

    }

    private void collapseMapWithDetails() {
        if (!placeInfoContainer.isShown()) {
            shadowBottom.setVisibility(View.GONE);
            int collapsedHeight = placeInfoContainer.getHeight() * 2 + 2 * getResources().getDimensionPixelSize(R.dimen.place_info_container_height);
            int expandedHeight = mapView.getHeight();

            ValueAnimator mapHeightAnimator = ValueAnimator.ofInt(expandedHeight, collapsedHeight);
            mapHeightAnimator.setDuration(700);
            mapHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mapView.getLayoutParams().height = value.intValue();
                    mapView.requestLayout();
                }
            });
            mapHeightAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, 0);
                    mapView.requestLayout();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, R.id.invite_place_info_container);
                    mapView.requestLayout();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(placeInfoContainer, "alpha", 0, 1);
            ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(placeInfoContainer, "translationY", expandedHeight - collapsedHeight, 0);
            fadeAnimator.setDuration(700);
            slideAnimator.setDuration(700);


            AnimatorSet set = new AnimatorSet();
            set.setDuration(700);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    placeInfoContainer.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    shadowBottom.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.playTogether(mapHeightAnimator, slideAnimator, fadeAnimator);
            set.start();
        }
    }

    private void expandMapWithDetails() {
        if (placeInfoContainer.isShown()) {
            shadowBottom.setVisibility(View.GONE);
            int collapsedHeight = mapView.getHeight();
            int expandedHeight = wholeHeight;

            ValueAnimator mapHeightAnimator = ValueAnimator.ofInt(collapsedHeight, expandedHeight);
            mapHeightAnimator.setDuration(700);
            mapHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mapView.getLayoutParams().height = value.intValue();
                    mapView.requestLayout();
                }
            });
            mapHeightAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, 0);
                    mapView.requestLayout();
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, R.id.button_next);
                    mapView.requestLayout();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(placeInfoContainer, "alpha", 1, 0);
            ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(placeInfoContainer, "translationY", 0, expandedHeight - collapsedHeight);
            fadeAnimator.setDuration(700);
            slideAnimator.setDuration(700);


            AnimatorSet set = new AnimatorSet();
            set.setDuration(700);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    placeInfoContainer.setVisibility(View.GONE);
                    shadowBottom.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            set.playTogether(mapHeightAnimator, slideAnimator, fadeAnimator);
            set.start();
        }
    }

    private void collapseMapWithPlaces() {
        if (!recyclerView.isShown()) {
            placeInfoContainer.setVisibility(View.GONE);
            int collapsedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 104.0f, getResources().getDisplayMetrics());
            int expandedHeight = mapView.getHeight();

            ValueAnimator mapHeightAnimator = ValueAnimator.ofInt(expandedHeight, collapsedHeight);
            mapHeightAnimator.setDuration(700);
            mapHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    mapView.getLayoutParams().height = value.intValue();
                    mapView.requestLayout();
                }
            });
            mapHeightAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    ((RelativeLayout.LayoutParams) mapView.getLayoutParams()).addRule(RelativeLayout.ABOVE, 0);
                    mapView.requestLayout();
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
            });


            ObjectAnimator fadeAnimator = ObjectAnimator.ofFloat(recyclerView, "alpha", 0, 1);
            ObjectAnimator slideAnimator = ObjectAnimator.ofFloat(recyclerView, "translationY", expandedHeight - collapsedHeight, 0);
            fadeAnimator.setDuration(700);
            slideAnimator.setDuration(700);


            AnimatorSet set = new AnimatorSet();
            set.setDuration(700);
            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    recyclerView.setVisibility(View.VISIBLE);
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
            });
            set.playTogether(mapHeightAnimator, slideAnimator, fadeAnimator);
            set.start();
        }
    }
}
