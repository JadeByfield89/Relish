package relish.permoveo.com.relish.network.response.google;

import com.google.api.client.util.Key;

import java.util.List;

import relish.permoveo.com.relish.model.Place;

/**
 * Created by rom4ek on 25.07.2015.
 */
public class PlacesResponse extends GoogleResponse {
    @Key
    public List<Place> results;
}
