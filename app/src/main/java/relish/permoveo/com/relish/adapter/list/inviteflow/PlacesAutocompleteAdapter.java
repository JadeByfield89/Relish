package relish.permoveo.com.relish.adapter.list.inviteflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by rom4ek on 26.09.2015.
 */
public class PlacesAutocompleteAdapter extends RecyclerView.Adapter<PlacesAutocompleteAdapter.ViewHolder> {

    private ArrayList<GooglePlace> dataset;
    private Context context;

    public PlacesAutocompleteAdapter(Context context) {
        super();
        this.context = context;
        dataset = new ArrayList<>();
    }

    public void clear() {
        this.dataset.clear();
        notifyDataSetChanged();
    }

    public void swap(ArrayList<GooglePlace> places) {
        this.dataset = places;
        notifyDataSetChanged();
    }

    @Override
    public PlacesAutocompleteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.place_autocomplete_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlacesAutocompleteAdapter.ViewHolder holder, int position) {
        GooglePlace place = (GooglePlace) getItem(position);

        String placeName = place.name.substring(0, 1).toUpperCase() + place.name.substring(1);

        holder.placeName.setText(placeName);
        holder.placeName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        holder.placeLocation.setText(place.address.substring(0, place.address.lastIndexOf(',')));
        holder.placeLocation.setTypeface(TypefaceUtil.PROXIMA_NOVA);
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

        @Bind(R.id.place_name)
        TextView placeName;

        @Bind(R.id.place_location)
        TextView placeLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
