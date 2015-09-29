package relish.permoveo.com.relish.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.twitter.sdk.android.core.models.User;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.activities.TipCalculatorActivity;
import relish.permoveo.com.relish.adapter.list.SettingsAdapter;
import relish.permoveo.com.relish.model.Setting;
import relish.permoveo.com.relish.util.RecyclerItemClickListener;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.util.UserUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @Bind(R.id.settings_recycler_view)
    RecyclerView settingsList;

    private SettingsAdapter adapter;
    private float appRating;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, v);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        settingsList.setLayoutManager(manager);
        settingsList.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                handleSettingsItemClick(view, position);
            }
        }));


        initSettings();

        return v;
    }


    private void handleSettingsItemClick(final View view, final int position) {
        switch (position) {

            // Push Notifications
            case 1:
                SharedPrefsUtil.get.togglePushNotifications();
                break;

            // Split the bill
            case 2:
                Intent intent = new Intent(getContext(), TipCalculatorActivity.class);
                startActivity(intent);
                break;

            // Location Sharing
            case 3:
                SharedPrefsUtil.get.toggleLocationSharing();
                break;

            //Sync with Google Calendar
            case 4:

                SharedPrefsUtil.get.toggleGoogleCalendarSync();
                break;

            // Relish on Twitter
            case 6:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + "jaybedreamin")));
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/" + "jaybedreamin")));
                }
                break;

            // Relish on Facebook
            case 7:
                String facebookScheme = "https://www.facebook.com/RelishApp";
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebookScheme));
                startActivity(facebookIntent);
                break;

            // Tell your friends
            case 8:
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.invite_contact_message));
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
                break;

            // Rate Relish
            case 9:
                showRatingDialog();
                break;

            // About Relish
            case 11:
                showAboutDialog();
                break;

            // Contact Support
            case 12:
                contactSupport();
                break;

            // Log out
            case 15:
                UserUtils.logoutUser(getContext());
                break;

        }
    }

    private void contactSupport() {
        String[] addresses = {"support@relishwith.us"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Relish Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void showAboutDialog() {
        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(getActivity().getSupportFragmentManager(), "About");
    }

    private void showRatingDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_rate_app, (ViewGroup) getView().getRootView(), false);

        View rootView = view.findViewById(R.id.rating_dialog_root);

        TextView dialogTitle = (TextView) view.findViewById(R.id.tvRateTitle);
        dialogTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        TextView dialogMessage = (TextView) view.findViewById(R.id.tvRateMessage);
        dialogMessage.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        final TextView dialogThanks = (TextView) view.findViewById(R.id.tvThanksOrStore);
        dialogThanks.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);


        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.rbRatingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                setAppRating(rating);
                if (rating > 3.0) {
                    dialogThanks.setVisibility(View.VISIBLE);
                    dialogThanks.setText(getString(R.string.settings_rating_dialog_store));
                } else {
                    dialogThanks.setVisibility(View.VISIBLE);
                    dialogThanks.setText(getString(R.string.settings_rating_dialog_thanks));

                }
            }
        });

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FF6724"), PorterDuff.Mode.SRC_ATOP);

        dialogBuilder.setView(view);


        final Dialog dialog = dialogBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialogThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void setAppRating(final float rating) {
        this.appRating = rating;
    }

    private void initSettings() {
        ArrayList<Setting> settings = new ArrayList<Setting>();

        //General Header
        String header_general = getString(R.string.settings_header_general);
        Setting generalHeaderSetting = new Setting(header_general, "", false);
        settings.add(generalHeaderSetting);


        //Push Notification Setting
        Setting setting_push_notifications = new Setting(getString(R.string.settings_item_push_notifications), "", true);
        settings.add(setting_push_notifications);


        //Tip Calculator Setting
        Setting setting_tip_calculator = new Setting(getString(R.string.settings_item_tip_calculator), "", false);
        settings.add(setting_tip_calculator);

        //Location Sharing Setting
        Setting setting_location_sub = new Setting(getString(R.string.settings_item_location_sharing), getString(R.string.settings_item_location_sharing_sub), true);
        settings.add(setting_location_sub);

        //Google Calendar Setting
        Setting setting_google_calendar = new Setting(getString(R.string.settings_item_google_calendar), getString(R.string.settings_item_google_calendar_sub), true);
        settings.add(setting_google_calendar);

        //Social Stuff Header
        Setting header_social_stuff = new Setting(getString(R.string.settings_header_social), "", false);
        settings.add(header_social_stuff);

        Setting relish_twitter = new Setting(getString(R.string.settings_item_twitter), "", false);
        settings.add(relish_twitter);

        Setting relish_facebook = new Setting(getString(R.string.settings_item_facebook), "", false);
        settings.add(relish_facebook);

        Setting relish_tell_friends = new Setting(getString(R.string.settings_item_tell_friends), "", false);
        settings.add(relish_tell_friends);

        Setting setting_rate = new Setting(getString(R.string.settings_item_rate_us), "", false);
        settings.add(setting_rate);

        Setting header_help = new Setting(getString(R.string.settings_header_support), "", false);
        settings.add(header_help);

        Setting setting_about = new Setting(getString(R.string.settings_item_about_relish), "", false);
        settings.add(setting_about);

        Setting setting_contact = new Setting(getString(R.string.settings_item_contact_support), "", false);
        settings.add(setting_contact);

        Setting setting_version = new Setting(getString(R.string.settings_item_version_number), getString(R.string.settings_item_version_number_sub), false);
        settings.add(setting_version);

        Setting setting_change_password = new Setting(getString(R.string.settings_item_change_password), "", false);
        settings.add(setting_change_password);

        Setting setting_logout = new Setting(getString(R.string.settings_item_logout), "", false);
        settings.add(setting_logout);

        adapter = new SettingsAdapter(getActivity(), settings);
        settingsList.setAdapter(adapter);


    }

}
