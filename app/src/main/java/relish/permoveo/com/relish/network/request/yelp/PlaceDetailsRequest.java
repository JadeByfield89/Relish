package relish.permoveo.com.relish.network.request.yelp;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.network.response.yelp.PlaceDetailsResponse;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.deserializer.RestaurantLocationDeserializer;
import relish.permoveo.com.relish.util.deserializer.YelpReviewDeserializer;

/**
 * Created by Roman on 03.08.15.
 */
public class PlaceDetailsRequest extends RelishRequest<String, Void, PlaceDetailsResponse> {

    public PlaceDetailsRequest(IRequestable callback) {
        super(callback);
        gson = new GsonBuilder()
                .registerTypeAdapter(YelpPlace.RestaurantLocation.class, new RestaurantLocationDeserializer())
                .registerTypeAdapter(Review.class, new YelpReviewDeserializer())
                .create();
    }

    @Override
    protected PlaceDetailsResponse doInBackground(String... params) {
        String id = params[0];
        OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACE_DETAILS + "/" + id);

        API.service.signRequest(API.accessToken, request);
        PlaceDetailsResponse placeDetailsResponse = null;
        try {
            Response response = request.send();
            InputStream stream = response.getStream();
            BufferedReader r = new BufferedReader(new InputStreamReader(stream));
            StringBuilder json = new StringBuilder();
            String line;
            try {
                while ((line = r.readLine()) != null) {
                    json.append(line);
                }
                String jsonString = json.toString();
                JsonParser parser = new JsonParser();
                if (!parser.parse(jsonString).getAsJsonObject().has("error")) {
                    jsonString = "{place:" + jsonString + "}";
                }
                placeDetailsResponse = gson.fromJson(jsonString, PlaceDetailsResponse.class);
                return placeDetailsResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (OAuthConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(PlaceDetailsResponse response) {
        super.onPostExecute(response);
        if (callback != null) {
            if (response == null) {
                callback.failed();
            } else if (!response.isSuccessful()) {
                callback.failed(response.error.text);
            } else {
                callback.completed(response.place);
            }
        }
    }
}
