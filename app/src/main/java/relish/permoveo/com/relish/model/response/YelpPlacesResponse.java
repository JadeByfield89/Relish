package relish.permoveo.com.relish.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import relish.permoveo.com.relish.model.Restaurant;
import relish.permoveo.com.relish.model.YelpError;

/**
 * Created by Roman on 29.07.15.
 */
public class YelpPlacesResponse {

    public int total;
    @SerializedName("businesses")
    public List<Restaurant> restaurants;
    public YelpError error;

    public boolean isSuccessful() {
        return error == null;
    }
}
