package relish.permoveo.com.relish.model.google;

import com.google.gson.annotations.SerializedName;

import relish.permoveo.com.relish.model.Review;

/**
 * Created by Roman on 03.08.15.
 */
public class GoogleReview extends Review {
    @SerializedName("author_name")
    public String authorName;
    @SerializedName("author_url")
    public String authorUrl;


    @Override
    public String getLargeAuthorImage() {
        return authorImage.replace("?sz=50", "?sz=250");
    }

}
