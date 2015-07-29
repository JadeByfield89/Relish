package relish.permoveo.com.relish.model;

import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;

import relish.permoveo.com.relish.util.LocationUtil;

/**
 * Created by rom4ek on 29.07.2015.
 */
public class Restaurant {
    public String name;
    public double distance;
    public float rating;
    @SerializedName("image_url")
    public String image;

    public double getCalculatedDistance() {
        return distance * LocationUtil.milesConversion;
    }

    public String formatDistance() {
        double distanceInMiles = distance * LocationUtil.milesConversion;

        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (distance == (long) distance)
            return String.format("%d", (long) distance);
        else
            return twoDForm.format(distance);
    }
}
