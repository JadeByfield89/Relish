package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Place;

/**
 * Created by Roman on 20.07.15.
 */
public class PlacesAdapter extends BaseAdapter {

    private ArrayList<Place> dataset;
    private LayoutInflater inflater;


    static class ViewHolder {
        public ImageView placeImage;
        public TextView placeName;
        public TextView placeDistance;
    }

    public PlacesAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        dataset = new ArrayList<>();
    }

    public void swap(ArrayList<Place> places) {
        dataset = new ArrayList<>(places);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataset.size();
    }

    @Override
    public Object getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.place_grid_item, parent, false);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }
}
