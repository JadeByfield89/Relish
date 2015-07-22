package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.etsy.android.grid.util.DynamicHeightImageView;
import com.etsy.android.grid.util.DynamicHeightTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Place;
import relish.permoveo.com.relish.widget.RatingView;

/**
 * Created by Roman on 20.07.15.
 */
public class PlacesAdapter extends BaseAdapter {

    private ArrayList<Place> dataset;
    private LayoutInflater inflater;
    private final Random random;
    private Context context;
    private static final SparseArray<Double> positionHeightRatios = new SparseArray<>();


    static class ViewHolder {
        @Bind(R.id.grid_item_place_image)
        public DynamicHeightImageView placeImage;

        @Bind(R.id.grid_item_place_name)
        public DynamicHeightTextView placeName;

        @Bind(R.id.grid_item_place_distance)
        public DynamicHeightTextView placeDistance;

        @Bind(R.id.grid_item_place_cost)
        public DynamicHeightTextView placeCost;

        @Bind(R.id.grid_item_rating_view)
        public RatingView placeRating;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public PlacesAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        random = new Random();
        dataset = new ArrayList<>();
    }

    public void swap(ArrayList<Place> places) {
        dataset = new ArrayList<>(places);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Place> places) {
        dataset.addAll(places);
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
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Place place = (Place) getItem(position);
        double positionHeight = getPositionRatio(position);

        viewHolder.placeImage.setHeightRatio(positionHeight);
        Picasso.with(context)
                .load(place.image)
                .resize(320, 320)
                .into(viewHolder.placeImage);

        viewHolder.placeCost.setText(place.priceRanking.toString());

        int quantity = place.distance == 1.0d ? 1 : 2;
        viewHolder.placeDistance.setText(place.formatDistance()
                + " "
                + context.getResources().getQuantityString(R.plurals.miles, quantity, place.distance));

        viewHolder.placeName.setText(place.name);
        viewHolder.placeRating.setRating(place.rating);

        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = positionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            positionHeightRatios.append(position, ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (random.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }
}
