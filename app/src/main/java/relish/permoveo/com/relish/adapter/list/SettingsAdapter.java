package relish.permoveo.com.relish.adapter.list;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.andexert.library.RippleView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.services.calendar.CalendarScopes;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Setting;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 9/3/15.
 */
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int NORMAL = 1;
    private static final int TOGGLE = 2;
    private static final int RIPPLE_ANIMATION_DURATION = 400;

    private ArrayList<Setting> settings;
    private Context context;


    public SettingsAdapter(final Context context, final ArrayList<Setting> settings) {
        this.settings = settings;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return settings.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == NORMAL) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_item, parent, false);
            return new SettingsViewHolder(v);
        } else if (viewType == HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_header, parent, false);
            return new SettingsHeaderViewHolder(v);
        } else if (viewType == TOGGLE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_toggle_item, parent, false);
            return new SettingsToggleViewHolder(v);
        } else
            throw new RuntimeException("Could not inflate layout");
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SettingsHeaderViewHolder) {
            ((SettingsHeaderViewHolder) holder).header.setText(settings.get(position).getTitle());
        } else if (holder instanceof SettingsViewHolder) {
            ((SettingsViewHolder) holder).title.setText(settings.get(position).getTitle());
            if (settings.get(position).getSubtitle().isEmpty()) {
                ((SettingsViewHolder) holder).subtitle.setVisibility(View.GONE);
                ((SettingsViewHolder) holder).rippleView.setRippleDuration(RIPPLE_ANIMATION_DURATION);
                ((SettingsViewHolder) holder).rippleView.setRippleColor(R.color.drawer_item_pressed_background);


            } else {
                ((SettingsViewHolder) holder).subtitle.setVisibility(View.VISIBLE);
                ((SettingsViewHolder) holder).subtitle.setText(settings.get(position).getSubtitle());
                ((SettingsViewHolder) holder).rippleView.setRippleDuration(RIPPLE_ANIMATION_DURATION);
                ((SettingsViewHolder) holder).rippleView.setRippleColor(R.color.drawer_item_pressed_background);


            }

        } else if (holder instanceof SettingsToggleViewHolder) {
            ((SettingsToggleViewHolder) holder).toggle.setTag(position);
            //((SettingsToggleViewHolder) holder).toggle.setOnCheckedChangeListener(new OnSettingsCheckedChangeListener());
            ((SettingsToggleViewHolder) holder).rippleView.setRippleDuration(RIPPLE_ANIMATION_DURATION);
            ((SettingsToggleViewHolder) holder).rippleView.setRippleColor(R.color.drawer_item_pressed_background);

            if (position == 4) {
                ((SettingsToggleViewHolder) holder).toggle.setChecked(false);
            }
            ((SettingsToggleViewHolder) holder).title.setText(settings.get(position).getTitle());
            if (settings.get(position).getSubtitle().isEmpty()) {
                ((SettingsToggleViewHolder) holder).subtitle.setVisibility(View.GONE);
            } else {
                ((SettingsToggleViewHolder) holder).subtitle.setVisibility(View.VISIBLE);
                ((SettingsToggleViewHolder) holder).subtitle.setText(settings.get(position).getSubtitle());

                ((SettingsToggleViewHolder) holder).rippleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((SettingsToggleViewHolder) holder).rippleView.setRippleColor(R.color.drawer_item_pressed_background);
                    }
                });
            }

        }

    }


    public static class OnSettingsCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("SettingsAdapter", "Toggle checked, position -> " + buttonView.getTag());
            int tag = (int) buttonView.getTag();
            switch (tag) {
                case 4:
                    SharedPrefsUtil.get.toggleGoogleCalendarSync();
                    break;

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else if (position == 5) {
            return HEADER;
        } else if (position == 4) {
            return TOGGLE;
        } else if (position == 10) {
            return HEADER;
        } else if (position == 1) {
            return TOGGLE;
        } else if (position == 3) {
            return TOGGLE;
        } else {
            return NORMAL;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class SettingsViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.settings_title)
        public TextView title;

        @Nullable
        @Bind(R.id.settings_subtitle)
        public TextView subtitle;

        @Nullable
        @Bind(R.id.settings_toggle)
        public ToggleButton toggleButton;

        @Bind(R.id.ripple)
        RippleView rippleView;

        public SettingsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            title.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            subtitle.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        }

    }

    public static class SettingsHeaderViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.settings_header)
        public TextView header;

        public SettingsHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            header.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        }
    }

    public static class SettingsToggleViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @Bind(R.id.settings_toggle)
        SwitchCompat toggle;

        @Nullable
        @Bind(R.id.settings_title)
        TextView title;

        @Nullable
        @Bind(R.id.settings_subtitle)
        TextView subtitle;

        @Bind(R.id.ripple)
        RippleView rippleView;

        public SettingsToggleViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            title.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            subtitle.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            toggle.setOnCheckedChangeListener(new OnSettingsCheckedChangeListener());

        }
    }


}
