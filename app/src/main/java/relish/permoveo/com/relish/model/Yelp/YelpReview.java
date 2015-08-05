package relish.permoveo.com.relish.model.yelp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import relish.permoveo.com.relish.model.Review;

/**
 * Created by Roman on 03.08.15.
 */
public class YelpReview implements Review, Serializable {
    @SerializedName("excerpt")
    private String text;
    @SerializedName("time_created")
    private long time;
    private String authorImage;
    private String authorName;
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
    public String getAuthorUrl() {
        return null;
    }
}
