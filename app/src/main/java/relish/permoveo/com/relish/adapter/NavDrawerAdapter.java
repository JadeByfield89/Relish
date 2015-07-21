package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.NavDrawerItem;

/**
 * Created by Roman on 21.07.15.
 */
public class NavDrawerAdapter extends BaseAdapter {

    private enum NavDrawerItemType {ITEM, DIVIDER}

    private ArrayList<NavDrawerItem> navDrawerItems;
    private LayoutInflater inflater;

    public NavDrawerAdapter(Context context,
                            ArrayList<NavDrawerItem> navDrawerItems) {
        this.inflater = LayoutInflater.from(context);
        this.navDrawerItems = navDrawerItems;
    }

    static class ViewHolder {
        @Bind(R.id.nav_drawer_item_icon)
        public ImageView icon;
        @Bind(R.id.nav_drawer_item_title)
        public TextView title;
        @Bind(R.id.nav_drawer_item_badge)
        public TextView badge;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public int getCount() {
        return navDrawerItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return TextUtils.isEmpty(navDrawerItems.get(position).title) ? NavDrawerItemType.DIVIDER.ordinal() : NavDrawerItemType.ITEM.ordinal();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        NavDrawerItemType type = NavDrawerItemType.values()[getItemViewType(position)];
        if (convertView == null) {
            switch (type) {
                case ITEM:
                    convertView = inflater.inflate(R.layout.nav_drawer_item, parent, false);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                    break;
                case DIVIDER:
                    convertView = inflater.inflate(R.layout.nav_drawer_item_section, parent, false);
                    break;
            }
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (type == NavDrawerItemType.ITEM) {
            assert viewHolder != null;

            NavDrawerItem item = (NavDrawerItem) getItem(position);
            viewHolder.title.setText(item.title);
            viewHolder.icon.setImageResource(item.icon);

            if (item.counter > 0) {
                viewHolder.badge.setVisibility(View.VISIBLE);
                viewHolder.badge.setText(item.counter + " new");
            } else {
                viewHolder.badge.setVisibility(View.GONE);
            }
        }

        return convertView;
    }
}
