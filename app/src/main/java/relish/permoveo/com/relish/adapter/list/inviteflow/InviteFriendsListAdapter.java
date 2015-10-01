package relish.permoveo.com.relish.adapter.list.inviteflow;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.LocationUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 28.08.2015.
 */
public class InviteFriendsListAdapter extends RecyclerView.Adapter<InviteFriendsListAdapter.ViewHolder> implements Filterable {

    private static final int RIPPLE_ANIMATION_DURATION = 230;
    private ArrayList<Friend> dataset;
    private ArrayList<Friend> originalValues;
    private Context context;

    public InviteFriendsListAdapter(Context context) {
        super();
        this.context = context;
        dataset = new ArrayList<>();
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    public void swap(ArrayList<Friend> friends) {
        this.dataset = friends;
        notifyDataSetChanged();
    }

    @Override
    public InviteFriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friend_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InviteFriendsListAdapter.ViewHolder holder, int position) {
        final Friend friend = (Friend) getItem(position);

        if (TextUtils.isEmpty(friend.image)) {
            holder.friendImage.setImageResource(R.drawable.default_profile_image);
        } else {
            Picasso.with(context)
                    .load(friend.image)
                    .placeholder(R.drawable.default_profile_image).fit().centerCrop()
                    .error(R.drawable.default_profile_image)
                    .into(holder.friendImage);
        }

        String friendName = friend.name.substring(0, 1).toUpperCase() + friend.name.substring(1);

        holder.friendName.setText(friendName);
        holder.friendName.setIncludeFontPadding(false);
        holder.friendName.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        if ((friend.lat != 0.0d && friend.lng != 0.0d) && GPSTracker.get.getLocation() != null) {
            holder.friendLocationContainer.setVisibility(View.VISIBLE);

            //Only remove the zip code if this string contains a number
            if (friend.address != null) {
                if (friend.address.matches(".*\\d+.*")) {
                    friend.address = friend.address.substring(0, friend.address.length() - 5);
                }
            }

            double distanceTo = LocationUtil.distance(friend.lat, friend.lng, GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude());
            holder.friendLocation.setText(friend.formatDistance(distanceTo) +
                    " " +
                    context.getResources().getQuantityString(R.plurals.miles, distanceTo == 1.0d ? 1 : 2, distanceTo)
                    + (TextUtils.isEmpty(friend.address) ? "" : " in " + friend.address));
            holder.friendLocation.setIncludeFontPadding(false);
            holder.friendLocation.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        } else {
            holder.friendLocationContainer.setVisibility(View.GONE);
        }

        holder.rippleView.setRippleDuration(RIPPLE_ANIMATION_DURATION);
        holder.rippleView.setRippleColor(friend.isSelected ? android.R.color.white : R.color.drawer_item_pressed_background);
//        holder.rippleView.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
//            @Override
//            public void onComplete(RippleView rippleView) {
//
//            }
//        });

        holder.rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        friend.isSelected = !friend.isSelected;
                        holder.rippleView.setRippleColor(friend.isSelected ? android.R.color.white : R.color.drawer_item_pressed_background);
                        holder.root.setSelected(friend.isSelected);
                        holder.friendName.setSelected(friend.isSelected);
                    }
                }, RIPPLE_ANIMATION_DURATION - 75);
            }
        });

        holder.root.setSelected(friend.isSelected);
        holder.friendName.setSelected(friend.isSelected);
    }

    public Object getItem(final int position) {
        return dataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public ArrayList<Friend> getSelected() {
        ArrayList<Friend> selected = new ArrayList<>();
        for (Friend friend : dataset) {
            if (friend.isSelected)
                selected.add(friend);
        }
        return selected;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Friend> FilteredArrList = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(dataset);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = originalValues.size();
                    results.values = originalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        Friend friend = originalValues.get(i);
                        if (friend.name.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(friend);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataset = (ArrayList<Friend>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.friend_name)
        TextView friendName;

        @Bind(R.id.friend_image)
        CircleImageView friendImage;

        @Bind(R.id.friend_location)
        TextView friendLocation;

        @Bind(R.id.friend_location_container)
        LinearLayout friendLocationContainer;

        @Bind(R.id.root)
        RelativeLayout root;

        @Bind(R.id.ripple)
        RippleView rippleView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}