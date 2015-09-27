package relish.permoveo.com.relish.model.yelp;

import com.google.gson.annotations.SerializedName;
import com.parse.ParseGeoPoint;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.util.LocationUtil;

/**
 * Created by rom4ek on 29.07.2015.
 */
public class YelpPlace implements Serializable {
    public String id;
    public String name;
    public double distance;
    public float rating;
    @SerializedName("image_url")
    public String image;
    public String phone;
    @SerializedName("snippet_text")
    public String snippet;
    @SerializedName("mobile_url")
    public String url;

    @SerializedName("rating_img_url_large")
    public String rating_img_url;

    public PlaceLocation location;
    public ArrayList<Review> reviews;
    public ArrayList<String> weekdayText;

    public double getCalculatedDistance() {
        return distance * LocationUtil.milesConversion;
    }

    public String formatDistance() {
        double distanceInMiles = distance * LocationUtil.milesConversion;

        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (distanceInMiles == (long) distanceInMiles)
            return String.format("%d", (long) distanceInMiles);
        else
            return twoDForm.format(distanceInMiles);
    }

    public String getHours() {
        DateTime dateTime = new DateTime();
        String hours = "";
        if (weekdayText != null) {
            for (int i = 0; i < weekdayText.size(); i++) {
                if (i == dateTime.getDayOfWeek())
                    hours = weekdayText.get(i).substring(weekdayText.get(i).indexOf(':') + 2, weekdayText.get(i).length());
            }
        }
        return hours;
    }

    public String getOriginalImage() {
        return image.replace("/ms", "/o");
    }

    public String getLargeImage() {
        return image.replace("/ms", "/ls");
    }

    /**
     * Created by rom4ek on 03.08.2015.
     */
    public static class PlaceLocation implements Serializable {

        public PlaceLocation() {}

        public PlaceLocation(String address, ParseGeoPoint location) {
            this.address = address;
            this.lat = location.getLatitude();
            this.lng = location.getLongitude();
        }

        public String address;
        public double lat;
        public double lng;
    }
}
