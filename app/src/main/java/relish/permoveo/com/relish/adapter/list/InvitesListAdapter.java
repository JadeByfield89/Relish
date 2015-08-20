package relish.permoveo.com.relish.adapter.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Invite;

/**
 * Created by byfieldj on 8/15/15.
 */
public class InvitesListAdapter extends BaseAdapter {

    private ArrayList<Invite> invites;
    private Context context;
    private LayoutInflater inflater;

    public InvitesListAdapter(ArrayList<Invite> invites, Context context){
        this.invites = invites;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 0;
    }

    static class ViewHolder{

    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_invite, parent, false);
        }
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int i) {
        return invites.get(i);
    }
}
