package relish.permoveo.com.relish.network.response.yelp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import relish.permoveo.com.relish.model.Restaurant;
import relish.permoveo.com.relish.network.error.YelpError;

/**
 * Created by Roman on 29.07.15.
 */
public class PlacesResponse {

    public int total;
    @SerializedName("businesses")
    public List<Restaurant> restaurants;
    public YelpError error;

    public boolean isSuccessful() {
        return error == null;
    }
}
