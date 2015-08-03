package relish.permoveo.com.relish.network.request.yelp;

import com.google.gson.GsonBuilder;

import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.Restaurant;
import relish.permoveo.com.relish.model.Review;
import relish.permoveo.com.relish.network.API;
import relish.permoveo.com.relish.network.request.RelishRequest;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.deserializer.RestaurantLocationDeserializer;
import relish.permoveo.com.relish.util.deserializer.YelpReviewDeserializer;

/**
 * Created by Roman on 03.08.15.
 */
public class PlaceDetailsRequest extends RelishRequest<String, Void, Restaurant> {

    public PlaceDetailsRequest(IRequestable callback) {
        super(callback);
        gson = new GsonBuilder()
                .registerTypeAdapter(Restaurant.RestaurantLocation.class, new RestaurantLocationDeserializer())
                .registerTypeAdapter(Review.class, new YelpReviewDeserializer())
                .create();
    }

    @Override
    protected Restaurant doInBackground(String... params) {
        String id = params[0];
        OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACE_DETAILS + "/" + id.substring(1));

        API.service.signRequest(API.accessToken, request);
        Restaurant restaurant = null;
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
                restaurant = gson.fromJson(json.toString(), Restaurant.class);
                return restaurant;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (OAuthConnectionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Restaurant restaurant) {
        super.onPostExecute(restaurant);

    }
}
