package relish.permoveo.com.relish.view;

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

    private float rating;
    private boolean isLarge;
    private static final float RATING_STAR_MARGIN_IN_DP = 4.0f;
    private static final float RATING_STAR_SIZE_IN_DP = 12.0f;
    private static final float RATING_STAR_LARGE_SIZE_IN_DP = 16.0f;

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

    public void setRating(float rating) {
        this.rating = rating;
        notifyRatingChanged();
    }

    private void notifyRatingChanged() {
        this.removeAllViews();
        long integerPart = (long) rating;
        float floatingPart = rating - integerPart;
        StarBuilder builder = new StarBuilder();
        for (float i = 0; i < integerPart; i++) {
            this.addView(builder.build());
        }
        if (floatingPart != 0)
            this.addView(builder.isHalf(true).build());
        this.requestLayout();
    }

    public void setLarge(boolean isLarge) {
        this.isLarge = isLarge;
    }

    class StarBuilder {

        private boolean isHalf;

        public StarBuilder() {
        }

        public StarBuilder isHalf(boolean isHalf) {
            this.isHalf = isHalf;
            return this;
        }

        public ImageView build() {
            ImageView star = new ImageView(getContext());
            int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, isLarge ? RATING_STAR_LARGE_SIZE_IN_DP : RATING_STAR_SIZE_IN_DP, getResources().getDisplayMetrics());
            LinearLayout.LayoutParams starParams = new LayoutParams(size, size);
            starParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RATING_STAR_MARGIN_IN_DP, getResources().getDisplayMetrics());
            star.setLayoutParams(starParams);
            star.setImageResource(isHalf ? R.drawable.ic_star_half : R.drawable.ic_star);
            return star;
        }

    }

}
