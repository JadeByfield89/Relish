package relish.permoveo.com.relish.network.request.google;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

import java.io.IOException;
import java.net.URLEncoder;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.google.PlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 26.09.2015.
 */
public class PlacesAutocompleteRequest extends RelishRequest<String, Void, PlacesResponse> {
    private static final int AUTOCOMPLETE_RADIUS = 50000;

    public PlacesAutocompleteRequest(IRequestable callback) {
        super(callback);
    }

    @Override
    protected PlacesResponse doInBackground(String... params) {
        String input = params[0];
        HttpRequestFactory httpRequestFactory = API.createRequestFactory();
        HttpRequest request = null;
        try {
            request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACES_SEARCH_TEXT_URL));
            request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
            if (GPSTracker.get.getLocation() != null) {
                request.getUrl().put("location", GPSTracker.get.getLocation().getLatitude() + "," + GPSTracker.get.getLocation().getLongitude());
                request.getUrl().put("radius", AUTOCOMPLETE_RADIUS);
            }
            request.getUrl().put("sensor", "false");
            request.getUrl().put("query", URLEncoder.encode(input, "utf-8"));
            request.getUrl().put("types", "food|restaurant");
//            request.getUrl().put("types", "geocode");
//            request.getUrl().put("input", URLEncoder.encode(input, "utf-8"));


            String url = request.getUrl().build();
            String json = request.execute().parseAsString();
            PlacesResponse response = gson.fromJson(json, PlacesResponse.class);
//            PlacesAutocompleteResponse response = gson.fromJson(json, PlacesAutocompleteResponse.class);
//            PlacesAutocompleteResponseWithDetails finalResponse = new PlacesAutocompleteResponseWithDetails();
//
//            if (response == null)
//                return null;
//            else if (!response.isSuccessful()) {
//                finalResponse.error = response.error;
//                return finalResponse;
//            } else {
//                for (GooglePlaceAutocompleteModel placeAutocomplete : response.predictions) {
//                    if (placeAutocomplete.types.contains("food") || placeAutocomplete.types.contains("restaurant")) {
//                        request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACE_DETAILS_URL));
//                        request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
//                        request.getUrl().put("reference", placeAutocomplete.reference);
//
//                        String jsonDetials = request.execute().parseAsString();
////            JsonParser parser = new JsonParser();
////            if (!parser.parse(json).getAsJsonObject().has("error_message")) {
////                JsonObject place = parser.parse(json).getAsJsonObject().get("result").getAsJsonObject();
////                json = gson.toJson(place);
////            }
//                        PlaceDetailsResponse placeDetailsResponse = gson.fromJson(jsonDetials, PlaceDetailsResponse.class);
//                        if (placeDetailsResponse == null)
//                            return null;
//                        else if (!placeDetailsResponse.isSuccessful()) {
//                            finalResponse.error = placeDetailsResponse.error;
//                            return finalResponse;
//                        } else {
//                            finalResponse.places.add(placeDetailsResponse.place);
//                        }
//                    }
//                }
//            }
//            Log.d("API Response", response.results.toString());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlacesResponse response) {
        super.onPostExecute(response);
        if (callback != null) {
            if (response == null) {
                callback.failed();
            } else if (!response.isSuccessful()) {
                callback.failed(response.error);
            } else {
                callback.completed(response.results);
            }
        }
    }
}
