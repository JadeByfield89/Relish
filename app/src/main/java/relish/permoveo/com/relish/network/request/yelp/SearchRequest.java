package relish.permoveo.com.relish.network.request.yelp;

import android.util.Log;

import com.google.gson.GsonBuilder;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.yelp.PlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.deserializer.RestaurantLocationDeserializer;

/**
 * Created by Roman on 03.08.15.
 */
public class SearchRequest extends RelishRequest<Integer, Void, PlacesResponse> {

    public SearchRequest(IRequestable callback) {
        super(callback);
        gson = new GsonBuilder()
                .registerTypeAdapter(YelpPlace.RestaurantLocation.class, new RestaurantLocationDeserializer())
                .create();
    }

    @Override
    protected PlacesResponse doInBackground(Integer... params) {
        Integer page = params[0];
        OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACES_SEARCH_URL);
        request.addQuerystringParameter("limit", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH));
        request.addQuerystringParameter("ll", String.valueOf(GPSTracker.get.getLocation().getLatitude()) + "," + String.valueOf(GPSTracker.get.getLocation().getLongitude()));
        request.addQuerystringParameter("offset", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH * page));
        request.addQuerystringParameter("sort", String.valueOf(ConstantUtil.PLACES_SORTING_ORDER));
//            request.addQuerystringParameter("radius_filter", String.valueOf(ConstantUtil.PLACES_RADIUS_SEARCH));
        request.addQuerystringParameter("term", "restaurants");
        request.addQuerystringParameter("category_filter", "thai");

        API.service.signRequest(API.accessToken, request);
        PlacesResponse placesResponse = null;
        try {
            Response response = request.send();
            String json = response.getBody();
            Log.d("Response: ",  json);

            placesResponse = gson.fromJson(json, PlacesResponse.class);
        } catch (OAuthConnectionException e) {
            e.printStackTrace();
        }
        return placesResponse;
    }

    @Override
    protected void onPostExecute(PlacesResponse placesResponse) {
        super.onPostExecute(placesResponse);
        if (callback != null) {
            if (placesResponse == null) {
                callback.failed();
            } else if (!placesResponse.isSuccessful()) {
                callback.failed(placesResponse.error.text);
            } else {
                callback.completed(placesResponse.total, placesResponse.restaurants);
            }
        }
    }
}
