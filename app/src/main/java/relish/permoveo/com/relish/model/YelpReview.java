package relish.permoveo.com.relish.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Roman on 03.08.15.
 */
public class YelpReview extends Review {
    @SerializedName("excerpt")
    public String text;
    @SerializedName("time_created")
    public long time;

    public String getLargeAuthorImage() {
        return authorImage.replace("/ms", "/ls");
    }
}
