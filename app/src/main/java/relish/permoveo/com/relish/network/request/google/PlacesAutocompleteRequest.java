package relish.permoveo.com.relish.network.request.google;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;

import java.io.IOException;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.google.PlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by rom4ek on 26.09.2015.
 */
public class PlacesAutocompleteRequest extends RelishRequest<String, Void, PlacesResponse> {
    public PlacesAutocompleteRequest(IRequestable callback) {
        super(callback);
    }

    @Override
    protected PlacesResponse doInBackground(String... params) {
        String input = params[0];
        HttpRequestFactory httpRequestFactory = API.createRequestFactory();
        HttpRequest request = null;
        try {
            request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACES_SEARCH_URL));
            request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
            request.getUrl().put("location", restaurant.location.lat + "," + restaurant.location.lng);
            request.getUrl().put("name", restaurant.name);
            request.getUrl().put("radius", ConstantUtil.NEAREST_PLACES_RADIUS);
            request.getUrl().put("types", "food|restaurant");

            String json = request.execute().parseAsString();
            PlacesResponse response = gson.fromJson(json, PlacesResponse.class);
//            Log.d("API Response", response.results.toString());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(relish.permoveo.com.relish.network.response.google.PlacesResponse response) {
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
