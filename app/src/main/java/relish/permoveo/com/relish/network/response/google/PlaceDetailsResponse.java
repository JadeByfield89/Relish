package relish.permoveo.com.relish.network.response.google;

import com.google.gson.annotations.SerializedName;

import relish.permoveo.com.relish.model.google.GooglePlace;

/**
 * Created by rom4ek on 25.07.2015.
 */
public class PlaceDetailsResponse extends GoogleResponse {
    @SerializedName("result")
    public GooglePlace place;
}
