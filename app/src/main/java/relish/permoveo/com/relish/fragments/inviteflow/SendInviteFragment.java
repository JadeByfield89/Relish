package relish.permoveo.com.relish.fragments.inviteflow;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendInviteFragment extends Fragment implements RenderCallbacks {

    @Bind(R.id.invite_send_date_desc)
    TextView sendDateDesc;

    @Bind(R.id.invite_send_time_desc)
    TextView sendTimeDesc;

    @Bind(R.id.invite_send_sendto_desc)
    TextView sendSendToDesc;

    @Bind(R.id.send_invite_note)
    EditText sendNote;

    @Bind(R.id.invite_send_date)
    TextView sendDate;

    @Bind(R.id.invite_send_time)
    TextView sendTime;

    @Bind(R.id.first_person)
    CircleImageView firstPerson;

    @Bind(R.id.second_person)
    CircleImageView secondPerson;

    @Bind(R.id.third_person)
    CircleImageView thirdPerson;

    @Bind(R.id.more_persons)
    TextView morePersons;

    @Bind(R.id.button_send)
    Button sendButton;

    @Bind(R.id.invite_send_note_container)
    RelativeLayout sendNoteContainer;

    @Bind(R.id.snapshot)
    SelectableRoundedImageView snapshot;

    private InviteCreator creator;
    private GoogleMap mMap;
    private Marker placeMarker;

    public SendInviteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof InviteCreator)
            creator = (InviteCreator) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_invite, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setDescriptionTypeface();
        setTypeface();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(false);

        if (creator.getInvite().location != null) {
            if (placeMarker == null) {
                placeMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(creator.getInvite().location.lat, creator.getInvite().location.lng), 18.0f));
            }
        } else {
            if (GPSTracker.get.getLocation() != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude()), 18.0f));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public void render() {
        if (creator.getInvite() != null) {
            DateTime date = new DateTime().withMillis(creator.getInvite().date);
            DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("E, MMMM d");
            sendDate.setText(dateFormatter.print(date));

            DateTime time = new DateTime().withMillis(creator.getInvite().time);
            DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm a");
            sendTime.setText(timeFormatter.print(time));

            if (!TextUtils.isEmpty(creator.getInvite().note)) {
                sendNoteContainer.setVisibility(View.VISIBLE);
                sendNote.setText(creator.getInvite().note);
            } else {
                sendNoteContainer.setVisibility(View.GONE);
            }

//            if (!TextUtils.isEmpty(creator.getInvite().mapSnapshot)) {
//                snapshot.setVisibility(View.VISIBLE);
//                Picasso.with(getActivity())
//                        .load(new File(creator.getInvite().mapSnapshot))
//                        .into(snapshot);
//            } else {
                snapshot.setVisibility(View.GONE);
//            }

            ArrayList<String> avatars = new ArrayList<>();
            for (InvitePerson person : creator.getInvite().invited) {
                if (!TextUtils.isEmpty(person.image) && avatars.size() < 3) {
                    avatars.add(person.image);
                }
            }
            renderInvited(avatars);
        } else {
            getActivity().finish();
        }
    }

    private void renderInvited(ArrayList<String> avatars) {
        if (avatars.size() == 0) {
            firstPerson.setVisibility(View.GONE);
            secondPerson.setVisibility(View.GONE);
            thirdPerson.setVisibility(View.GONE);
            morePersons.setVisibility(View.VISIBLE);
            morePersons.setText(creator.getInvite().invited.size() +
                    " " + getResources().getQuantityString(R.plurals.persons, creator.getInvite().invited.size()));
        } else if (avatars.size() == 1) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.GONE);
            thirdPerson.setVisibility(View.GONE);
            if (creator.getInvite().invited.size() - 1 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 1) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 2) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(secondPerson);

            thirdPerson.setVisibility(View.GONE);
            if (creator.getInvite().invited.size() - 2 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 2) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 3) {
            firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(firstPerson);

            secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(secondPerson);

            thirdPerson.setVisibility(View.VISIBLE);
            Picasso.with(getActivity())
                    .load(avatars.get(2))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(thirdPerson);

            if (creator.getInvite().invited.size() - 3 != 0) {
                morePersons.setVisibility(View.VISIBLE);
                morePersons.setText("+ " + (creator.getInvite().invited.size() - 3) + " " + getString(R.string.persons_more));
            } else {
                morePersons.setVisibility(View.GONE);
            }
        }
    }

    private void setDescriptionTypeface() {
        sendDateDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        sendSendToDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        sendTimeDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
    }

    private void setTypeface() {
        sendNote.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        morePersons.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        sendDate.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        sendTime.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
    }
}
