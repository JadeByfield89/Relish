package relish.permoveo.com.relish.model.google;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import relish.permoveo.com.relish.model.Review;

/**
 * Created by Roman on 03.08.15.
 */
public class GoogleReview implements Review, Serializable {
    @SerializedName("author_name")
    private String authorName;
    @SerializedName("author_url")
    private String authorUrl;
    private String authorImage;
    private float rating;
    private String text;
    private long time;


    @Override
    public String getLargeAuthorImage() {
        return authorImage.replace("?sz=50", "?sz=250");
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
        return authorUrl;
    }

}
