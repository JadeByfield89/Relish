package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.InvitePerson;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 8/15/15.
 */
public class InvitesListAdapter extends RecyclerView.Adapter<InvitesListAdapter.ViewHolder> {

    private ArrayList<Invite> dataset;
    private Context context;

    public InvitesListAdapter(Context context) {
        this.dataset = new ArrayList<>();
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.invite_place_name)
        TextView invitePlaceName;

        @Bind(R.id.invite_place_address)
        TextView invitePlaceAddress;

        @Bind(R.id.invite_title)
        TextView inviteTitle;

        @Bind(R.id.invite_date_time)
        TextView inviteDateTime;

        @Bind(R.id.invite_map_snapshot)
        SelectableRoundedImageView inviteMapSnapshot;

        @Bind(R.id.first_person)
        CircleImageView firstPerson;

        @Bind(R.id.second_person)
        CircleImageView secondPerson;

        @Bind(R.id.third_person)
        CircleImageView thirdPerson;

        @Bind(R.id.more_persons)
        TextView morePersons;

        @Bind(R.id.invited_layout)
        LinearLayout inviteLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    public void swap(ArrayList<Invite> invites) {
        this.dataset = invites;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Invite invite = (Invite) getItem(position);

        setTypeface(holder);

        Picasso.with(context)
                .load(invite.mapSnapshot)
                .into(holder.inviteMapSnapshot);

        holder.inviteTitle.setText(invite.title);
        holder.invitePlaceName.setText(invite.name);
        holder.invitePlaceAddress.setText(invite.location.address);
        holder.inviteDateTime.setText(invite.getFormattedDateTime());

        renderInvited(holder, invite);
    }

    private void renderInvited(ViewHolder holder, Invite invite) {
        ArrayList<String> avatars = new ArrayList<>();
        for (InvitePerson person : invite.invited) {
            if (!TextUtils.isEmpty(person.image) && avatars.size() < 3) {
                avatars.add(person.image);
            }
        }

        holder.inviteLayout.setGravity(Gravity.LEFT | Gravity.START | Gravity.CENTER_VERTICAL);
        if (avatars.size() == 0) {
            holder.firstPerson.setVisibility(View.GONE);
            holder.secondPerson.setVisibility(View.GONE);
            holder.thirdPerson.setVisibility(View.GONE);
            holder.morePersons.setVisibility(View.VISIBLE);
            ((LinearLayout.LayoutParams) holder.morePersons.getLayoutParams()).leftMargin = 0;
            holder.morePersons.requestLayout();
            holder.morePersons.setText(invite.invited.size() +
                    " " + context.getResources().getQuantityString(R.plurals.persons, invite.invited.size()));
        } else if (avatars.size() == 1) {
            holder.firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.firstPerson);

            holder.secondPerson.setVisibility(View.GONE);
            holder.thirdPerson.setVisibility(View.GONE);
            if (invite.invited.size() - 1 != 0) {
                ((LinearLayout.LayoutParams) holder.morePersons.getLayoutParams()).leftMargin =
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, context.getResources().getDisplayMetrics());
                holder.morePersons.requestLayout();
                holder.morePersons.setVisibility(View.VISIBLE);
                holder.morePersons.setText("+ " + (invite.invited.size() - 1) + " " + context.getString(R.string.persons_more));
            } else {
                holder.morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 2) {
            holder.firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.firstPerson);

            holder.secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.secondPerson);

            holder.thirdPerson.setVisibility(View.GONE);
            if (invite.invited.size() - 2 != 0) {
                ((LinearLayout.LayoutParams) holder.morePersons.getLayoutParams()).leftMargin =
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, context.getResources().getDisplayMetrics());
                holder.morePersons.requestLayout();
                holder.morePersons.setVisibility(View.VISIBLE);
                holder.morePersons.setText("+ " + (invite.invited.size() - 2) + " " + context.getString(R.string.persons_more));
            } else {
                holder.morePersons.setVisibility(View.GONE);
            }
        } else if (avatars.size() == 3) {
            holder.firstPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(0))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.firstPerson);

            holder.secondPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(1))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.secondPerson);

            holder.thirdPerson.setVisibility(View.VISIBLE);
            Picasso.with(context)
                    .load(avatars.get(2))
                    .placeholder(R.drawable.relish_avatar_placeholder)
                    .fit()
                    .centerCrop()
                    .error(R.drawable.relish_avatar_placeholder)
                    .into(holder.thirdPerson);

            if (invite.invited.size() - 3 != 0) {
                ((LinearLayout.LayoutParams) holder.morePersons.getLayoutParams()).leftMargin =
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4.0f, context.getResources().getDisplayMetrics());
                holder.morePersons.requestLayout();
                holder.morePersons.setVisibility(View.VISIBLE);
                holder.morePersons.setText("+ " + (invite.invited.size() - 3) + " " + context.getString(R.string.persons_more));
            } else {
                holder.morePersons.setVisibility(View.GONE);
            }
        }
    }

    private void setTypeface(ViewHolder holder) {
        holder.invitePlaceName.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.inviteDateTime.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.invitePlaceAddress.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.morePersons.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        holder.inviteTitle.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public Object getItem(int position) {
        return dataset.get(position);
    }
}
