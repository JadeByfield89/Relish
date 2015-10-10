package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.OnInviteSentListener;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.manager.InvitesManager;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateInviteFragment extends Fragment implements RenderCallbacks {

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

    @Bind(R.id.invite_send_card)
    RelativeLayout inviteSendCard;

    @Bind(R.id.invite_send_root)
    RelativeLayout inviteSendRoot;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progressBar;

    @Bind(R.id.invite_layout)
    LinearLayout inviteLayout;

    @Bind(R.id.map_snapshot)
    SelectableRoundedImageView mapSnapshot;

    public UpdateInviteFragment() {
        // Required empty public constructor
    }

    private InviteCreator creator;
    private OnInviteSentListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InviteCreator)
            creator = (InviteCreator) activity;
        if (activity instanceof OnInviteSentListener)
            mListener = (OnInviteSentListener) activity;
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
                sendButton.setText("");
                sendButton.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                InvitesManager.updateInvite(creator.getInvite(), new InvitesManager.InvitesManagerCallback<String, ParseException>() {
                    @Override
                    public void done(String s, ParseException e) {
                        if (e == null) {
                            if (isAdded())
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (creator.getInvite().reminder != 0) {
                                            PendingIntent pintent = PendingIntent.getBroadcast(getActivity(), 0, new Intent("relish.permoveo.com.relish.REMINDER")
                                                    .putExtra(ConstantUtil.INVITE_EXTRA, creator.getInvite()), 0);
                                            AlarmManager manager = (AlarmManager) (getActivity().getSystemService(Context.ALARM_SERVICE));

                                            DateTime time = new DateTime().withMillis(creator.getInvite().time);
                                            DateTime date = new DateTime().withMillis(creator.getInvite().date);
                                            DateTime when = new DateTime()
                                                    .withYear(date.getYear())
                                                    .withMonthOfYear(date.getMonthOfYear())
                                                    .withDayOfMonth(date.getDayOfMonth())
                                                    .withHourOfDay(time.getHourOfDay())
                                                    .withMinuteOfHour(time.getMinuteOfHour());
                                            manager.set(AlarmManager.RTC_WAKEUP, when.getMillis() - creator.getInvite().reminder * 1000, pintent);
                                        }

                                        mListener.onInviteSent(true);
                                    }
                                });
                        } else {
                            if (isAdded()) {
                                sendButton.setText(getString(R.string.invite_update));
                                sendButton.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                                Snackbar.make(inviteSendCard, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
//        setUpMapIfNeeded();
    }

    @Override
    public void render() {
        if (creator.getInvite() != null) {
            sendDate.setText(creator.getInvite().getFormattedDate());
            sendTime.setText(creator.getInvite().getFormattedTime());

            if (!TextUtils.isEmpty(creator.getInvite().note)) {
                sendNoteContainer.setVisibility(View.VISIBLE);
                sendNote.setText(creator.getInvite().note);
            } else {
                sendNoteContainer.setVisibility(View.GONE);
            }

            Picasso.with(getActivity())
                    .load(creator.getInvite().mapSnapshot)
                    .into(mapSnapshot);

            sendButton.setText(getString(R.string.invite_update));

//            if (!TextUtils.isEmpty(creator.getInvite().mapSnapshot)) {
//                snapshot.setVisibility(View.VISIBLE);
//                Picasso.with(getActivity())
//                        .load(new File(creator.getInvite().mapSnapshot))
//                        .into(snapshot);
//            } else {
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
        inviteLayout.setGravity(Gravity.RIGHT | Gravity.END | Gravity.CENTER_VERTICAL);
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
