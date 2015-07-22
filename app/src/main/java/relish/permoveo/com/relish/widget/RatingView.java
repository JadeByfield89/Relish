package relish.permoveo.com.relish.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import relish.permoveo.com.relish.R;

/**
 * Created by Roman on 22.07.15.
 */
public class RatingView extends LinearLayout {

    private int rating;
    private static final float RATING_STAR_MARGIN_IN_DP = 4.0f;
    private static final float RATING_STAR_SIZE_IN_DP = 10.0f;

    public RatingView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
    }

    public RatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
    }

    public RatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(HORIZONTAL);
    }

    public void setRating(int rating) {
        this.rating = rating;
        notifyRatingChanged();
    }

    private void notifyRatingChanged() {
        this.removeAllViews();
        StarBuilder builder = new StarBuilder();
        for (int i = 0; i < rating; i++) {
            this.addView(builder.build());
        }
        this.requestLayout();
    }


    class StarBuilder {

        public StarBuilder() {
        }

        public ImageView build() {
            ImageView star = new ImageView(getContext());
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RATING_STAR_SIZE_IN_DP, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams starParams = new LayoutParams(size, size);
            starParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RATING_STAR_MARGIN_IN_DP, getResources().getDisplayMetrics());
            star.setLayoutParams(starParams);
            star.setImageResource(R.drawable.ic_star);
            return star;
        }

    }

}
