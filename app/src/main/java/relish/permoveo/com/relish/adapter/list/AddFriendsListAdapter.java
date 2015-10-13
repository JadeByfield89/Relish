package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Friend;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 11.08.2015.
 */
public class AddFriendsListAdapter extends RecyclerView.Adapter<AddFriendsListAdapter.ViewHolder> {

    private ArrayList<Friend> dataset;
    private Context context;
    private ViewHolder.AddFriendButtonClickListener mListener;

    public AddFriendsListAdapter(Context context, ViewHolder.AddFriendButtonClickListener listener) {
        super();
        this.context = context;
        mListener = listener;
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
    public AddFriendsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_add_list_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(AddFriendsListAdapter.ViewHolder holder, int position) {
        Friend friend = (Friend) getItem(position);

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

        holder.friendBtn.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.friendBtn.setIncludeFontPadding(true);
        holder.friendBtn.setIndeterminateProgressMode(true);
        holder.friendBtn.setTransformationMethod(null);

        holder.friendBtn.setCompleteText("Friends");
        if (friend.isMyFriend) {
            holder.friendBtn.setProgress(100);
            holder.friendBtn.setText("Friends");

//            holder.friendBtn.setBackgroundResource(R.drawable.ic_invited);
//            holder.friendBtn.setTag(true);
        } else {
            holder.friendBtn.setProgress(0);

//            holder.friendBtn.setBackgroundResource(R.drawable.ic_content_send);
//            holder.friendBtn.setTag(false);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.friend_name)
        TextView friendName;

        @Bind(R.id.friend_image)
        CircleImageView friendImage;

        @Bind(R.id.friend_btn)
        CircularProgressButton friendBtn;

        public ViewHolder(View itemView, final AddFriendButtonClickListener viewHolderClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            friendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderClickListener.onClick((View) v.getParent());
                }
            });
        }

        public static interface AddFriendButtonClickListener {
            void onClick(View view);
        }
    }
}
