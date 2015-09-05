package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.RatingView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PickPlaceInviteFragment extends Fragment {

    private static final String PLACE_FOR_INVITE = "place_for_invite";

    @Bind(R.id.invite_place)
    EditText invitePlace;

    @Bind(R.id.close_icon)
    ImageView close;

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

    private YelpPlace currentPlace;
    private GoogleMap mMap;
    private Marker placeMarker;
    private PagerCallbacks pagerCallbacks;
    private InviteCreator creator;

    public PickPlaceInviteFragment() {
        // Required empty public constructor
    }

    public static PickPlaceInviteFragment newInstance(YelpPlace place) {
        PickPlaceInviteFragment pickPlaceInviteFragment = new PickPlaceInviteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(PLACE_FOR_INVITE, place);
        pickPlaceInviteFragment.setArguments(bundle);
        return pickPlaceInviteFragment;
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
        if (getArguments() != null) {
            currentPlace = (YelpPlace) getArguments().getSerializable(PLACE_FOR_INVITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pick_place_invite, container, false);
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
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pagerCallbacks != null)
                    pagerCallbacks.next();
            }
        });

        invitePlace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    myLocation.setVisibility(View.GONE);
                    //close.setVisibility(View.VISIBLE);
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(invitePlace.getText())) {
                    invitePlace.setText("");
                    invitePlace.requestFocus();
                    placeMarker.remove();

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(invitePlace, 0);
                } else {
                    invitePlace.clearFocus();
                    close.setVisibility(View.GONE);
                    //myLocation.setVisibility(View.VISIBLE);

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

        if (currentPlace != null) {
            invitePlace.setText(currentPlace.name);
            invitePlaceName.setText(currentPlace.name);
            invitePlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
            invitePlaceName.setIncludeFontPadding(false);

            invitePlaceAddress.setText(currentPlace.location.address);
            invitePlaceAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            invitePlaceAddress.setIncludeFontPadding(false);

            ratingView.setRating(currentPlace.rating);

            invitePlaceHours.setText(currentPlace.getHours());
            invitePlaceHours.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            invitePlaceHours.setIncludeFontPadding(false);

            //close.setVisibility(View.VISIBLE);
            myLocation.setVisibility(View.GONE);
        } else {
            invitePlace.clearFocus();
            close.setVisibility(View.GONE);
            //myLocation.setVisibility(View.VISIBLE);
        }

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
        if (mMap != null) {
            setUpMap();
        }
    }

    private void setUpMap() {
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        if (currentPlace != null) {
            if (placeMarker == null) {
                placeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentPlace.location.lat, currentPlace.location.lng)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentPlace.location.lat, currentPlace.location.lng), 17.0f), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        mMap.snapshot(new GoogleMap.SnapshotReadyCallback() {
                            @Override
                            public void onSnapshotReady(Bitmap bitmap) {
                                ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
                                // path to /data/data/yourapp/app_data/imageDir
                                File directory = cw.getDir("images", Context.MODE_PRIVATE);
                                File snapshotPath = new File(directory, "snapshot.png");

                                FileOutputStream fos = null;
                                try {
                                    fos = new FileOutputStream(snapshotPath);
                                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
                                    fos.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                creator.getInvite().mapSnapshot = snapshotPath.getAbsolutePath();
                            }
                        });
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
}
