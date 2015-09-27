package relish.permoveo.com.relish.network.response.google;

import java.util.List;

import relish.permoveo.com.relish.model.google.GooglePlaceAutocompleteModel;

/**
 * Created by rom4ek on 27.09.2015.
 */
public class PlacesAutocompleteResponse extends GoogleResponse {
    public List<GooglePlaceAutocompleteModel> predictions;
}
