package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Invite;

/**
 * Created by byfieldj on 8/15/15.
 */
public class InvitesAdapter extends RecyclerView.Adapter<InvitesAdapter.InviteViewHolder> {

    private Context context;
    private ArrayList<Invite> invites;

    public static class InviteViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.invite_place_name)
        public TextView inviteName;


        @Bind(R.id.invite_date_time)
        public TextView inviteDateTime;


        @Bind(R.id.invite_title)
        public TextView inviteTitle;

        @Bind(R.id.invite_map_image)
        public ImageView inviteImage;

        public InviteViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public InvitesAdapter(Context context){
        super();
        this.context = context;
        this.invites = new ArrayList<Invite>();

    }

    @Override
    public void onBindViewHolder(InviteViewHolder holder, int position) {
        if(holder instanceof InviteViewHolder){
            InvitesAdapter.InviteViewHolder vh = (InviteViewHolder) holder;

            vh.inviteTitle.setText("Lunch with Friends");
        }
    }



    @Override
    public InvitesAdapter.InviteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_invite, parent, false);
        return new InviteViewHolder(view);
    }

    public Object getItem(final int position) {
        return invites.get(position);
    }

    @Override
    public int getItemCount() {
        Log.d("InvitesAdapter", "Invites count -> " + invites.size());
        return invites.size();
    }

    public void addAll(ArrayList<Invite> invites){
        int oldCount = getItemCount();
        if(oldCount > 0){
            invites.remove(oldCount - 1);
        }
        invites.addAll(invites);
        notifyItemRangeInserted(0, invites.size() + 1);
    }

    public void add(Invite invite){
        invites.add(invite);
        notifyItemInserted(invites.size() + 1);
    }
}
