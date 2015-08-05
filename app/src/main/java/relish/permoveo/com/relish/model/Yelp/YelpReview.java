package relish.permoveo.com.relish.model.yelp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import relish.permoveo.com.relish.model.BaseReview;

/**
 * Created by Roman on 03.08.15.
 */
public class YelpReview extends BaseReview implements Serializable {
    @SerializedName("excerpt")
    private String text;
    @SerializedName("time_created")
    private long time;
    private String authorImage;
    private String authorName;
    @SerializedName("rating_image_large_url")
    private String ratingImage;
    private float rating;

    @Override
    public String getLargeAuthorImage() {
        return authorImage.replace("/ms", "/ls");
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public String getAuthorImage() {
        return authorImage;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public float getRating() {
        return rating;
    }

    @Override
    public void setAuthorImage(String image) {
        this.authorImage = image;
    }

    @Override
    public void setAuthorName(String name) {
        this.authorName = name;
    }

    @Override
    public String getRatingImage() {
        return ratingImage;
    }
}
