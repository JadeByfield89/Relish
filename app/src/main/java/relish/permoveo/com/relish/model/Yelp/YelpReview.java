package relish.permoveo.com.relish.model.Yelp;

import com.google.gson.annotations.SerializedName;

import relish.permoveo.com.relish.model.Review;

/**
 * Created by Roman on 03.08.15.
 */
public class YelpReview extends Review {
    @SerializedName("excerpt")
    public String text;
    @SerializedName("time_created")
    public long time;

    @Override
    public String getLargeAuthorImage() {
        return authorImage.replace("/ms", "/ls");
    }
}
