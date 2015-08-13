package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.ParseGeoPoint;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 12.08.2015.
 */
public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private ArrayList<Friend> dataset;
    private Context context;

    public FriendsListAdapter(Context context) {
        super();
        this.context = context;
        dataset = new ArrayList<>();
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

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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
    public FriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendsListAdapter.ViewHolder holder, int position) {
        Friend friend = (Friend) getItem(position);

        if (TextUtils.isEmpty(friend.image)) {
            holder.friendImage.setImageResource(R.drawable.avatar_placeholder);
        } else {
            Picasso.with(context)
                    .load(friend.image)
                    .placeholder(R.drawable.avatar_placeholder)
                    .error(R.drawable.avatar_placeholder)
                    .into(holder.friendImage);
        }

        holder.friendName.setText(friend.name);
        holder.friendName.setIncludeFontPadding(false);
        holder.friendName.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        if (friend.location != null && GPSTracker.get.getLocation() != null) {
            holder.friendLocationContainer.setVisibility(View.VISIBLE);

            double distanceTo = friend.location.distanceInMilesTo(new ParseGeoPoint(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude()));
            holder.friendLocation.setText(friend.formatDistance(distanceTo) +
                    " " +
                    context.getResources().getQuantityString(R.plurals.miles, distanceTo == 1.0d ? 1 : 2, distanceTo)
                    + (TextUtils.isEmpty(friend.address) ? "" : " in " + friend.address));
            holder.friendLocation.setIncludeFontPadding(false);
            holder.friendLocation.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        } else {
            holder.friendLocationContainer.setVisibility(View.GONE);
        }
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
}
