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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 30.08.2015.
 */
public class InviteEmailListAdapter extends RecyclerView.Adapter<InviteEmailListAdapter.ViewHolder> implements Filterable {

    private static final int RIPPLE_ANIMATION_DURATION = 230;
    private ArrayList<Contact> dataset;
    private ArrayList<Contact> originalValues;
    private Context context;

    public InviteEmailListAdapter(Context context) {
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
        notifyDataSetChanged();
    }

    @Override
    public InviteEmailListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_email_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final InviteEmailListAdapter.ViewHolder holder, int position) {
        final Contact contact = (Contact) getItem(position);

        if (TextUtils.isEmpty(contact.image)) {
            holder.emailContactImage.setImageResource(R.drawable.default_profile_image);
        } else {
            Picasso.with(context)
                    .load(contact.image)
                    .placeholder(R.drawable.default_profile_image).fit().centerCrop()
                    .error(R.drawable.default_profile_image)
                    .into(holder.emailContactImage);
        }

        String friendName = contact.name.substring(0, 1).toUpperCase() + contact.name.substring(1);

        holder.emailContactName.setText(friendName);
        holder.emailContactName.setIncludeFontPadding(false);
        holder.emailContactName.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.emailContactEmail.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.emailContactEmail.setText(contact.email);

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
                        holder.emailContactName.setSelected(contact.isSelected);
                    }
                }, RIPPLE_ANIMATION_DURATION - 75);
            }
        });

        holder.root.setSelected(contact.isSelected);
        holder.emailContactName.setSelected(contact.isSelected);
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
        @Bind(R.id.email_contact_name)
        TextView emailContactName;

        @Bind(R.id.email_contact_image)
        CircleImageView emailContactImage;

        @Bind(R.id.root)
        RelativeLayout root;

        @Bind(R.id.ripple)
        RippleView rippleView;

        @Bind(R.id.email_contact_email_address)
        TextView emailContactEmail;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}