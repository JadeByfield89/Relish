package relish.permoveo.com.relish.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.adapter.list.SettingsAdapter;
import relish.permoveo.com.relish.model.Setting;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    @Bind(R.id.settings_recycler_view)
    RecyclerView settingsList;

    private SettingsAdapter adapter;

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


        initSettings();

        return v;
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

        adapter = new SettingsAdapter(settings);
        settingsList.setAdapter(adapter);

    }


}
