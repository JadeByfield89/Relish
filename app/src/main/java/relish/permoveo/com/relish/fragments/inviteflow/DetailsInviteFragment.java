package relish.permoveo.com.relish.fragments.inviteflow;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.inviteflow.ReminderAdapter;
import relish.permoveo.com.relish.interfaces.InviteCreator;
import relish.permoveo.com.relish.interfaces.PagerCallbacks;
import relish.permoveo.com.relish.interfaces.RenderCallbacks;
import relish.permoveo.com.relish.model.Reminder;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsInviteFragment extends Fragment implements RenderCallbacks, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Bind(R.id.details_invite_title_desc)
    TextView inviteTitleDesc;

    @Bind(R.id.invite_details_title)
    EditText inviteTitle;

    @Bind(R.id.details_invite_location_desc)
    TextView inviteLocationDesc;

    @Bind(R.id.invite_details_location)
    TextView inviteLocation;

    @Bind(R.id.invite_details_date_desc)
    TextView inviteDateDesc;

    @Bind(R.id.invite_details_date)
    TextView inviteDate;

    @Bind(R.id.details_invite_time_desc)
    TextView inviteTimeDesc;

    @Bind(R.id.invite_details_time)
    TextView inviteTime;

    @Bind(R.id.details_invite_reminder_desc)
    TextView inviteReminderDesc;

    @Bind(R.id.reminder_spinner)
    Spinner reminderSpinner;

    @Bind(R.id.button_next)
    Button next;

    @Bind(R.id.invite_details_time_container)
    RelativeLayout timeContainer;

    @Bind(R.id.invite_details_date_container)
    RelativeLayout dateContainer;

    @Bind(R.id.details_invite_note)
    EditText inviteNote;

    @Bind(R.id.invite_details_card)
    RelativeLayout inviteDetailsCard;

    private ReminderAdapter reminderAdapter;
    private PagerCallbacks pagerCallbacks;
    private InviteCreator creator;

    public DetailsInviteFragment() {
        // Required empty public constructor
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
        ArrayList<Reminder> reminders = new ArrayList<>();
        String[] reminderTitles = getResources().getStringArray(R.array.reminder_titles);
        int[] reminderSeconds = getResources().getIntArray(R.array.reminder_seconds);
        for (int i = 0; i < reminderTitles.length; i++) {
            reminders.add(new Reminder(reminderTitles[i], reminderSeconds[i]));
        }
        reminderAdapter = new ReminderAdapter(getActivity(), reminders);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setDescriptionTypeface();
        setTypeface();
        reminderSpinner.setAdapter(reminderAdapter);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (!TextUtils.isEmpty(inviteNote.getText())) {
                        creator.getInvite().note = inviteNote.getText().toString();
                    }
                    creator.getInvite().title = inviteTitle.getText().toString();

                    if (pagerCallbacks != null)
                        pagerCallbacks.next();
                } else {
                    YoYo.with(Techniques.Shake)
                            .playOn(inviteDetailsCard);
                }
            }
        });

        inviteTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = new DateTime();
                if (creator.getInvite() != null && creator.getInvite().time != 0l) {
                    now = new DateTime().withMillis(creator.getInvite().time);
                }

                TimePickerDialog tpd = TimePickerDialog.newInstance(
                        DetailsInviteFragment.this,
                        now.getHourOfDay(),
                        now.getMinuteOfHour(),
                        false
                );
                tpd.show(getChildFragmentManager(), "TimePickerDialog");
            }
        });

        inviteDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime now = new DateTime();
                if (creator.getInvite() != null && creator.getInvite().date != 0l) {
                    now = new DateTime().withMillis(creator.getInvite().date);
                }

                DatePickerDialog dtp = DatePickerDialog.newInstance(
                        DetailsInviteFragment.this,
                        now.getYear(),
                        now.getMonthOfYear() - 1,
                        now.getDayOfMonth()
                );
                dtp.show(getChildFragmentManager(), "DatePickerDialog");
            }
        });

        timeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteTime.performClick();
            }
        });

        dateContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteDate.performClick();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void render() {
        if (creator.getInvite() != null) {
            if (creator.getInvite().location != null && !TextUtils.isEmpty(creator.getInvite().location.address)) {
                inviteLocation.setText(creator.getInvite().getFormattedAddress());
            } else {
                inviteLocation.setText("");
            }

            if (!TextUtils.isEmpty(creator.getInvite().title)) {
                inviteTitle.setText(creator.getInvite().title);
            } else {
                inviteTitle.setText("");
            }


            if (creator.getInvite().reminder != -1) {
                reminderSpinner.setSelection(reminderAdapter.getPosition(creator.getInvite().reminder));
            } else {
                reminderSpinner.setSelection(0);
            }

            if (creator.getInvite().date != 0l) {
                inviteDate.setText(creator.getInvite().getFormattedDate());
            } else {
                inviteDate.setText(getString(R.string.enter_date));
            }

            if (creator.getInvite().time != 0l) {
                inviteTime.setText(creator.getInvite().getFormattedTime());
            } else {
                inviteTime.setText(getString(R.string.enter_time));
            }
        } else {
//            inviteDate.setText(getString(R.string.enter_date));
//            inviteTime.setText(getString(R.string.enter_time));
//            reminderSpinner.setSelection(0);
//            inviteLocation.setText("");
//            inviteTitle.setText("");
            getActivity().finish();
        }
    }

    private void setDescriptionTypeface() {
        inviteTitleDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteLocationDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteDateDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteTimeDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteDateDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteReminderDesc.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        inviteNote.setTypeface(TypefaceUtil.PROXIMA_NOVA);
    }

    private void setTypeface() {
        inviteTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        inviteLocation.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        inviteDate.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        inviteTime.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        DateTime date = new DateTime().withDate(year, monthOfYear + 1, dayOfMonth);
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("E, MMMM d");
        inviteDate.setText(dateFormatter.print(date));
        creator.getInvite().date = date.getMillis();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        DateTime time = new DateTime()
                .withHourOfDay(hourOfDay).withMinuteOfHour(minute);
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm a");
        inviteTime.setText(timeFormatter.print(time));
        creator.getInvite().time = time.getMillis();
    }

    private boolean validate() {
        if (creator.getInvite() == null) {
            return false;
        } else {
            if (creator.getInvite().date == 0l) {
                String message = "";
                if (creator.getInvite().time == 0l || TextUtils.isEmpty(inviteTitle.getText())) {
                    message = getString(R.string.all_fields_error);
                } else {
                    message = String.format(getString(R.string.one_field_error), "date");
                }
                Snackbar.make(inviteDetailsCard, message, Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (creator.getInvite().time == 0l) {
                String message = "";
                if (creator.getInvite().date == 0l || TextUtils.isEmpty(inviteTitle.getText())) {
                    message = getString(R.string.all_fields_error);
                } else {
                    message = String.format(getString(R.string.one_field_error), "time");
                }
                Snackbar.make(inviteDetailsCard, message, Snackbar.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(inviteTitle.getText())) {
                String message = "";
                if (creator.getInvite().time == 0l || creator.getInvite().date == 0l) {
                    message = getString(R.string.all_fields_error);
                } else {
                    message = String.format(getString(R.string.one_field_error), "title");
                }
                Snackbar.make(inviteDetailsCard, message, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }
}
