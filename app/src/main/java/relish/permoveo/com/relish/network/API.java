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

import org.scribe.builder.ServiceBuilder;
import org.scribe.exceptions.OAuthConnectionException;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.response.PlacesResponse;
import relish.permoveo.com.relish.model.response.YelpPlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

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
        gson = new Gson();
    }

    public static void search(int page, final IRequestable callback) {
        new SearchTask(callback).execute(page);
    }

    public static void getPlaceDetails(String id, final IRequestable callback) {
        new GetPlaceTask(callback).execute(id);
    }

    private static class GetPlaceTask extends AsyncTask<String, Void, Void> {

        private IRequestable callback;

        public GetPlaceTask() {
        }

        public GetPlaceTask(IRequestable callback) {
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(String... params) {
            String id = params[0];
            OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACE_DETAILS + "/" + id);

            service.signRequest(accessToken, request);
            YelpPlacesResponse yelpPlacesResponse = null;
            try {
                Response response = request.send();
                String json = response.getBody();
//                yelpPlacesResponse = gson.fromJson(json, YelpPlacesResponse.class);
            } catch (OAuthConnectionException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class SearchTask extends AsyncTask<Integer, Void, YelpPlacesResponse> {

        private IRequestable callback;

        public SearchTask() {
        }

        public SearchTask(IRequestable callback) {
            this.callback = callback;
        }

        @Override
        protected YelpPlacesResponse doInBackground(Integer... params) {
            Integer page = params[0];
            OAuthRequest request = new OAuthRequest(Verb.GET, ConstantUtil.YELP_PLACES_SEARCH_URL);
            request.addQuerystringParameter("limit", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH));
            request.addQuerystringParameter("ll", String.valueOf(GPSTracker.get.getLocation().getLatitude()) + "," + String.valueOf(GPSTracker.get.getLocation().getLongitude()));
            request.addQuerystringParameter("offset", String.valueOf(ConstantUtil.PLACES_LIMIT_SEARCH * page));
            request.addQuerystringParameter("sort", String.valueOf(ConstantUtil.PLACES_SORTING_ORDER));
//            request.addQuerystringParameter("radius_filter", String.valueOf(ConstantUtil.PLACES_RADIUS_SEARCH));
            request.addQuerystringParameter("category_filter", "restaurants");

            service.signRequest(accessToken, request);
            YelpPlacesResponse yelpPlacesResponse = null;
            try {
                Response response = request.send();
                String json = response.getBody();
                yelpPlacesResponse = gson.fromJson(json, YelpPlacesResponse.class);
            } catch (OAuthConnectionException e) {
                e.printStackTrace();
            }
            return yelpPlacesResponse;
        }

        @Override
        protected void onPostExecute(YelpPlacesResponse yelpPlacesResponse) {
            super.onPostExecute(yelpPlacesResponse);
            if (callback != null) {
                if (yelpPlacesResponse == null) {
                    callback.failed();
                } else if (!yelpPlacesResponse.isSuccessful()) {
                    callback.failed(yelpPlacesResponse.error.text);
                } else {
                    callback.completed(yelpPlacesResponse.total, yelpPlacesResponse.restaurants);
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
    private static class LoadPlacesTask extends AsyncTask<Void, Void, PlacesResponse> {

        private IRequestable callback;

        public LoadPlacesTask() {
        }

        public LoadPlacesTask(IRequestable callback) {
            this.callback = callback;
        }

        @Override
        protected PlacesResponse doInBackground(Void... params) {
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

                PlacesResponse response = request.execute().parseAs(PlacesResponse.class);
                Log.d("API Response", response.results.toString());
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

}
