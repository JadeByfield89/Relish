package relish.permoveo.com.relish.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.widget.RatingView;

/**
 * Created by Roman on 04.08.15.
 */
public class ReviewsAdapter extends BaseAdapter {

    private ArrayList<Review> reviews;
    private Context context;

    public ReviewsAdapter(Context context) {
        this.context = context;
        this.reviews = new ArrayList<>();
    }

    static class ViewHolder {
        @Bind(R.id.review_image)
        public CircleImageView reviewImage;

        @Bind(R.id.review_name)
        public TextView reviewName;

        @Bind(R.id.review_date)
        public TextView reviewDate;

        @Bind(R.id.review_text)
        public TextView reviewText;

        @Bind(R.id.review_rating)
        public RatingView reviewRating;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void add(Review review) {
        boolean exists = false;
        for (Review r : reviews) {
            if (r.equals(review))
                exists = true;
        }
        
        if (!exists)
            this.reviews.add(review);

        notifyDataSetChanged();
    }

    public void swap(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Review> reviews) {
        reviews.addAll(reviews);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return reviews.size();
    }

    @Override
    public Object getItem(int position) {
        return reviews.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.review_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Review review = (Review) getItem(position);

        if (TextUtils.isEmpty(review.authorImage)) {
            viewHolder.reviewImage.setImageResource(R.drawable.avatar_placeholder);
        } else {
            Picasso.with(context)
                    .load(review.getLargeAuthorImage())
                    .into(viewHolder.reviewImage);
        }

        if (TextUtils.isEmpty(review.text)) {
            viewHolder.reviewText.setVisibility(View.GONE);
        } else {
            viewHolder.reviewText.setVisibility(View.VISIBLE);
            viewHolder.reviewText.setText(review.text);
            viewHolder.reviewText.setTypeface(TypefaceUtil.PROXIMA_NOVA);
            viewHolder.reviewText.setIncludeFontPadding(false);
        }

        DateTime reviewDateTime = new DateTime().withMillis(review.time);
        DateTime now = new DateTime();
        Period period = new Period(reviewDateTime, now);

        PeriodFormatter formatter = new PeriodFormatterBuilder()
                .appendDays().appendSuffix(" days ago\n")
                .printZeroNever()
                .toFormatter();

        viewHolder.reviewDate.setText(formatter.print(period));
        viewHolder.reviewDate.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        viewHolder.reviewDate.setIncludeFontPadding(false);

        viewHolder.reviewName.setText(review.authorName);
        viewHolder.reviewName.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        viewHolder.reviewName.setIncludeFontPadding(false);

        if (review.rating == 0.0f) {
            viewHolder.reviewRating.setVisibility(View.GONE);
        } else {
            viewHolder.reviewRating.setVisibility(View.VISIBLE);
            viewHolder.reviewRating.setRating(review.rating);
        }

        return convertView;
    }
}
