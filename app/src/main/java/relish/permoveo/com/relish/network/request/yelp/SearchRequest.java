package relish.permoveo.com.relish.network.request.yelp;

import android.util.Log;

import com.google.gson.GsonBuilder;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.util.ArrayList;

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

    private ArrayList<String> categories = new ArrayList<String>();

    public SearchRequest(IRequestable callback) {
        super(callback);
        gson = new GsonBuilder()
                .registerTypeAdapter(YelpPlace.PlaceLocation.class, new RestaurantLocationDeserializer())
                .create();
    }

    public SearchRequest(IRequestable callback, ArrayList<String> categories) {
        super(callback);
        gson = new GsonBuilder()
                .registerTypeAdapter(YelpPlace.PlaceLocation.class, new RestaurantLocationDeserializer())
                .create();

        this.categories = categories;
    }


    @Override
    protected PlacesResponse doInBackground(Integer... params) {
        Integer page = params[0];
        OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACES_SEARCH_URL);
        request.addQuerystringParameter("limit", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH));
        request.addQuerystringParameter("ll", "34.0736" + "," + "-118.4");
       // request.addQuerystringParameter("ll", String.valueOf(GPSTracker.get.getLocation().getLatitude()) + "," + String.valueOf(GPSTracker.get.getLocation().getLongitude()));

        request.addQuerystringParameter("offset", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH * page));
        //request.addQuerystringParameter("sort", String.valueOf(ConstantUtil.PLACES_SORTING_ORDER));
//            request.addQuerystringParameter("radius_filter", String.valueOf(ConstantUtil.PLACES_RADIUS_SEARCH));
        request.addQuerystringParameter("term", "restaurants");
        StringBuilder builder = new StringBuilder();

        Log.d("SearchRequest", "Categories size " + categories);

        if (categories.size() == 1) {
            Log.d("SearchRequest", "Category size is 1");
            Log.d("SearchRequest", "Category filter is " + categories.get(0));
            request.addQuerystringParameter("category_filter", categories.get(0));

        } else if (categories.size() > 1) {
            Log.d("SearchRequest", "Category size is greater than 1");

            for (String category : categories) {
                if (categories.indexOf(category) == 0) {
                    builder.append(category);
                } else {
                    builder.append("," + category);
                }
                Log.d("SearchRequest", "Category filters are " + builder.toString());
            }

            request.addQuerystringParameter("category_filter", builder.toString());

        }


        API.service.signRequest(API.accessToken, request);
        PlacesResponse placesResponse = null;
        try {
            Response response = request.send();
            String json = response.getBody();
            Log.d("Response: ", json);

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
