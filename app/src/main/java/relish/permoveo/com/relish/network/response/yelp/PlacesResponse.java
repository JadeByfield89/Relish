package relish.permoveo.com.relish.network.response.yelp;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by Roman on 29.07.15.
 */
public class PlacesResponse extends YelpResponse {
    public int total;
    @SerializedName("businesses")
    public List<YelpPlace> restaurants;
}
