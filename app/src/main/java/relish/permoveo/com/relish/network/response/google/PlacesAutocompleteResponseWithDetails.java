package relish.permoveo.com.relish.network.response.google;

import java.util.ArrayList;
import java.util.List;

import relish.permoveo.com.relish.model.google.GooglePlace;

/**
 * Created by rom4ek on 27.09.2015.
 */
public class PlacesAutocompleteResponseWithDetails extends GoogleResponse {
    public List<GooglePlace> places;

    public PlacesAutocompleteResponseWithDetails() {
        this.places = new ArrayList<>();
    }
}
