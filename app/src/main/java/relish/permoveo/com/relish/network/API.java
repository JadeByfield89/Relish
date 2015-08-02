package relish.permoveo.com.relish.network;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ObjectParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.Restaurant;
import relish.permoveo.com.relish.model.RestaurantLocation;
import relish.permoveo.com.relish.network.response.yelp.PlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.deserializer.RestaurantLocationDeserializer;

/**
 * Created by Roman on 23.07.15.
 */
public class API {

    private static OAuthService service;
    private static Token accessToken;
    private static Gson gson;

    public static void init(Context context) {
        service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(ConstantUtil.YELP_CONSUMER_KEY).apiSecret(ConstantUtil.YELP_CONSUMER_SECRET).build();
        accessToken = new Token(ConstantUtil.YELP_TOKEN, ConstantUtil.YELP_TOKEN_SECRET);
        gson = new GsonBuilder()
                .registerTypeAdapter(RestaurantLocation.class, new RestaurantLocationDeserializer())
                .create();
    }

    public static void search(int page, final IRequestable callback) {
        new SearchTask(callback).execute(page);
    }

    public static void getPlaceDetails(String id, final IRequestable callback) {
        new GetPlaceTask(callback).execute(id);
    }

    private static class GetPlaceTask extends AsyncTask<String, Void, Restaurant> {

        private IRequestable callback;

        public GetPlaceTask() {
        }

        public GetPlaceTask(IRequestable callback) {
            this.callback = callback;
        }

        @Override
        protected Restaurant doInBackground(String... params) {
            String id = params[0];
            OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACE_DETAILS + "/" + id);

            service.signRequest(accessToken, request);
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
                    assert restaurant != null;
                    return restaurant;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (OAuthConnectionException e) {
                e.printStackTrace();
            }
            return restaurant;
        }

        @Override
        protected void onPostExecute(Restaurant restaurant) {
            super.onPostExecute(restaurant);
        }
    }

    private static class SearchTask extends AsyncTask<Integer, Void, PlacesResponse> {

        private IRequestable callback;

        public SearchTask() {
        }

        public SearchTask(IRequestable callback) {
            this.callback = callback;
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
            request.addQuerystringParameter("category_filter", "restaurants");

            service.signRequest(accessToken, request);
            PlacesResponse placesResponse = null;
            try {
                Response response = request.send();
                String json = response.getBody();
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


    /*=========================== GOOGLE PLACES API ATTEMPT ===========================*/
    private static HttpTransport transport = new ApacheHttpTransport();

    public static void getNearestPlaces(IRequestable callback) {
        new LoadPlacesTask(callback).execute();
    }

    private static HttpRequestFactory createRequestFactory(final HttpTransport transport) {

        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent(ConstantUtil.RELISH_USER_AGENT);
                request.setHeaders(headers);
                ObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }

    @SuppressWarnings("Unused")
    private static class LoadPlaceDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
            HttpRequest request = null;
            try {
                request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACE_DETAILS_URL));
                request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
                request.getUrl().put("reference", params[0]);

                String json = request.execute().parseAsString();
                System.out.println(json);
                return json;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    @SuppressWarnings("Unused")
    private static class LoadPlacesTask extends AsyncTask<Void, Void, relish.permoveo.com.relish.network.response.google.PlacesResponse> {

        private IRequestable callback;

        public LoadPlacesTask() {
        }

        public LoadPlacesTask(IRequestable callback) {
            this.callback = callback;
        }

        @Override
        protected relish.permoveo.com.relish.network.response.google.PlacesResponse doInBackground(Void... params) {
            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
            HttpRequest request = null;
            try {
                request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACES_SEARCH_URL));
                request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
                request.getUrl().put("location", GPSTracker.get.getLocation().getLatitude() + "," + GPSTracker.get.getLocation().getLongitude());
                request.getUrl().put("rankby", "distance");
                Log.d("API location", "Latitude -> " + GPSTracker.get.getLocation().getLatitude() + "Longitude -> " + GPSTracker.get.getLocation().getLongitude());
                Log.d("API URL -> ", request.getUrl().toString());
                request.getUrl().put("radius", ConstantUtil.NEAREST_PLACES_RADIUS);
                request.getUrl().put("types", "food|restaurant");

                relish.permoveo.com.relish.network.response.google.PlacesResponse response = request.execute().parseAs(relish.permoveo.com.relish.network.response.google.PlacesResponse.class);
                Log.d("API Response", response.results.toString());
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

}
