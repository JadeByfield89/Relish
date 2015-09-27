package relish.permoveo.com.relish.adapter.list.inviteflow;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 9/24/15.
 */
public class InviteTwitterListAdapter extends RecyclerView.Adapter<InviteTwitterListAdapter.ViewHolder> implements Filterable {

    private static final int RIPPLE_ANIMATION_DURATION = 400;
    private ArrayList<Contact> dataset;
    private ArrayList<Contact> originalValues;
    private Context context;

    public InviteTwitterListAdapter(Context context) {
        super();
        this.context = context;
        dataset = new ArrayList<>();

    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    public void swap(ArrayList<Contact> contacts) {
        this.dataset = contacts;
        Collections.sort(dataset, new CustomComparator());

        notifyDataSetChanged();
    }

    @Override
    public InviteTwitterListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("InviteTwitterAdapter", "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_twitter_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InviteTwitterListAdapter.ViewHolder holder, int position) {
        final Contact contact = (Contact) getItem(position);

        if (TextUtils.isEmpty(contact.image)) {
            holder.contactImage.setImageResource(R.drawable.relish_avatar_placeholder);
        } else {
            Picasso.with(context)
                    .load(contact.image)
                    .placeholder(R.drawable.relish_avatar_placeholder).fit().centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.contactImage);
        }

        String friendName = contact.name.substring(0, 1).toUpperCase() + contact.name.substring(1);

        holder.contactName.setText(friendName);
        holder.contactName.setIncludeFontPadding(false);
        holder.contactName.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.contactUsername.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.contactUsername.setText(contact.twitterUsername);


        holder.rippleView.setRippleDuration(RIPPLE_ANIMATION_DURATION);
        holder.rippleView.setRippleColor(contact.isSelected ? android.R.color.white : R.color.drawer_item_pressed_background);

        holder.rippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        contact.isSelected = !contact.isSelected;
                        holder.rippleView.setRippleColor(contact.isSelected ? android.R.color.white : R.color.drawer_item_pressed_background);
                        holder.root.setSelected(contact.isSelected);
                        holder.contactName.setSelected(contact.isSelected);
                    }
                }, RIPPLE_ANIMATION_DURATION - 75);
            }
        });

        holder.root.setSelected(contact.isSelected);
        holder.contactName.setSelected(contact.isSelected);
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
        Log.d("InviteTwitterList", "getItemCount -> " + dataset.size());

        return dataset.size();
    }

    public ArrayList<Contact> getSelected() {
        ArrayList<Contact> selected = new ArrayList<>();
        for (Contact contact : dataset) {
            if (contact.isSelected)
                selected.add(contact);
        }
        return selected;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Contact> FilteredArrList = new ArrayList<>();

                if (originalValues == null) {
                    originalValues = new ArrayList<>(dataset);
                }

                if (constraint == null || constraint.length() == 0) {
                    results.count = originalValues.size();
                    results.values = originalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < originalValues.size(); i++) {
                        Contact contact = originalValues.get(i);
                        if (contact.name.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(contact);
                        }
                    }
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dataset = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.twitter_follower_name)
        TextView contactName;

        @Bind(R.id.twitter_follower_image)
        CircleImageView contactImage;

        @Bind(R.id.root)
        RelativeLayout root;

        @Bind(R.id.ripple)
        RippleView rippleView;

        @Bind(R.id.twitter_follower_username)
        TextView contactUsername;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class CustomComparator implements Comparator<Contact> {
        @Override
        public int compare(Contact first, Contact second) {
            return first.name.compareTo(second.name);
        }
    }
}
