package relish.permoveo.com.relish.adapter.list;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 9/3/15.
 */
public class SettingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int HEADER = 0;
    private static final int NORMAL = 1;
    private static final int TOGGLE = 2;

    private static final int SETTINGS_COUNT = 12;
    @Override
    public int getItemCount() {
        return SETTINGS_COUNT;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if(viewType == NORMAL){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_item, parent, false);
            return new SettingsViewHolder(v);
        }
        else if(viewType == HEADER){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_header, parent, false);
            return new SettingsHeaderViewHolder(v);
        }

        else if(viewType == TOGGLE){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settings_toggle_item, parent, false);
            return new SettingsToggleViewHolder(v);
        }

        else
            throw new RuntimeException("Could not inflate layout");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SettingsHeaderViewHolder){
//            ((SettingsHeaderViewHolder) holder).header.setText("HEADER");
        }else if( holder instanceof SettingsViewHolder){
           // holder.title.setText("Title");
            //holder.subtitle.setText("Subtitle");
        }
        else if(holder instanceof  SettingsToggleViewHolder){
           // ((SettingsToggleViewHolder) holder).title.setText("Title");
            //((SettingsToggleViewHolder) holder).title.setText("Subtitle");

        }

    }

    public static class SettingsViewHolder extends RecyclerView.ViewHolder{

        @Nullable
        @Bind(R.id.settings_title)
        public TextView title;

        @Nullable
        @Bind(R.id.settings_subtitle)
        public TextView subtitle;

        @Nullable
        @Bind(R.id.settings_toggle)
        public ToggleButton toggleButton;

        public SettingsViewHolder(View view){
            super(view);
            ButterKnife.bind(this,view);
            title.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            subtitle.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        }

    }

    public static class SettingsHeaderViewHolder extends  RecyclerView.ViewHolder{

        @Nullable
        @Bind(R.id.settings_header)
        public TextView header;

        public SettingsHeaderViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            header.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        }
    }

    public static class SettingsToggleViewHolder extends  RecyclerView.ViewHolder{

        @Nullable
        @Bind(R.id.settings_toggle)
        SwitchCompat toggle;

        @Nullable
        @Bind(R.id.settings_title)
        TextView title;

        @Nullable
        @Bind(R.id.settings_subtitle)
        TextView subtitle;

        public SettingsToggleViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            title.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            subtitle.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return HEADER;
        }
        else if (position == 4){
            return HEADER;
        }
        else if(position == 9){
            return HEADER;
        }
        else if(position == 1){
            return TOGGLE;
        }
        else{
            return NORMAL;
        }
    }
}
