package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Contact;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by Roman on 13.08.15.
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> implements Filterable {
    private ArrayList<Contact> dataset;
    private ArrayList<Contact> originalValues;
    private Context context;
    private ViewHolder.ContactsButtonClickListener mListener;

    public ContactsAdapter(Context context, ViewHolder.ContactsButtonClickListener listener) {
        super();
        this.context = context;
        this.mListener = listener;
        dataset = new ArrayList<>();
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    public void update(Contact contact) {
        int index = -1;
        for (int i = 0; i < dataset.size(); i++) {
            Contact c = dataset.get(i);
            if (c.longId == contact.longId) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            this.dataset.set(index, contact);
            notifyItemChanged(index);
        }
    }

    public void swap(ArrayList<Contact> contacts) {
        this.dataset = contacts;
        notifyDataSetChanged();
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_add_list_item, parent, false);
        return new ViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        Contact contact = (Contact) getItem(position);

        if (TextUtils.isEmpty(contact.image)) {
            holder.contactImage.setImageResource(R.drawable.relish_avatar_placeholder);
        } else {
            Picasso.with(context)
                    .load(contact.image)
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.contactImage);
        }

        holder.contactName.setText(contact.name);
        holder.contactName.setIncludeFontPadding(false);
        holder.contactName.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        /*holder.contactBtn.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.contactBtn.setIncludeFontPadding(true);
        holder.contactBtn.setIndeterminateProgressMode(true);
        holder.contactBtn.setTransformationMethod(null);*/

       /* holder.contactBtn.setCompleteText("Share");
        holder.contactBtn.setIdleText("Share");
        if (contact.isInvited) {
            holder.contactBtn.setProgress(100);
            holder.contactBtn.setText("Share");
        } else {
            holder.contactBtn.setProgress(0);
            holder.contactBtn.setText("Share");
        }*/
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
        @Bind(R.id.friend_name)
        TextView contactName;

        @Bind(R.id.friend_image)
        CircleImageView contactImage;

        @Bind(R.id.friend_btn)
        ImageButton contactBtn;

        public ViewHolder(View itemView, final ContactsButtonClickListener viewHolderClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            contactBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewHolderClickListener.onClick((View) v.getParent());
                }
            });
        }

        public static interface ContactsButtonClickListener {
            void onClick(View view);
        }
    }

}
