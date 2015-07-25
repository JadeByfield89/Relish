package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Place;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.widget.DynamicHeightImageView;
import relish.permoveo.com.relish.widget.RatingView;

/**
 * Created by Roman on 20.07.15.
 */
public class PlacesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Place> dataset;
    private Context context;
    private Random random;
    private static final SparseArray<Double> positionHeightRatios = new SparseArray<>();


    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.place_grid_item_root)
        public CardView placeRoot;

        @Bind(R.id.grid_item_place_image)
        public DynamicHeightImageView placeImage;

        @Bind(R.id.grid_item_place_name)
        public TextView placeName;

        @Bind(R.id.grid_item_place_distance)
        public TextView placeDistance;

        @Bind(R.id.grid_item_place_cost)
        public TextView placeCost;

        @Bind(R.id.grid_item_rating_view)
        public RatingView placeRating;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public PlacesAdapter(Context context) {
        super();
        this.random = new Random();
        this.context = context;
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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_grid_item, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.place_grid_header, viewGroup, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof PlacesAdapter.ViewHolder) {
            Place place = (Place) getItem(position);
            PlacesAdapter.ViewHolder vh = (PlacesAdapter.ViewHolder) viewHolder;

            vh.placeRoot.setPreventCornerOverlap(false);

            double positionHeight = getPositionRatio(position);
            vh.placeImage.setHeightRatio(positionHeight);
            if (TextUtils.isEmpty(place.getImage())) {
                vh.placeImage.setImageDrawable(null);
            } else {
                Picasso.with(context)
                        .load(place.getImage())
                        .into(vh.placeImage);
            }

            vh.placeCost.setText(place.getPriceLevel());
            vh.placeCost.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            vh.placeCost.setIncludeFontPadding(false);

            int quantity = (int) Math.floor(place.getCalculatedDistance());
            vh.placeDistance.setText(place.formatDistance() +
                    " " +
                    context.getResources().getQuantityString(R.plurals.miles, quantity, place.getCalculatedDistance()));
            vh.placeDistance.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            vh.placeDistance.setIncludeFontPadding(false);

            vh.placeName.setText(place.name);
            vh.placeName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
            vh.placeName.setIncludeFontPadding(false);

            vh.placeRating.setRating((int) place.rating);
        } else if (viewHolder instanceof HeaderViewHolder) {
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) viewHolder.itemView.getLayoutParams();
            if (params == null) {
                params = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                        context.getResources().getDimensionPixelSize(R.dimen.featured_image_size));
            }
            params.setFullSpan(true);
            viewHolder.itemView.setLayoutParams(params);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return position == 0 ? 0 : 1;
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
        return (random.nextDouble() / 2.0) + 0.6; // height will be 1.0 - 1.5
        // the width
    }
}
