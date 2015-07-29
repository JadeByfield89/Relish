package relish.permoveo.com.relish.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
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
import com.parse.signpost.OAuthConsumer;
import com.parse.signpost.basic.DefaultOAuthConsumer;

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

    private static RequestQueue queue;

    public static void init(Context context) {
        OAuthConsumer consumer = new DefaultOAuthConsumer(ConstantUtil.YELP_CONSUMER_KEY, ConstantUtil.YELP_CONSUMER_SECRET);
        consumer.setTokenWithSecret(ConstantUtil.YELP_TOKEN, ConstantUtil.YELP_TOKEN_SECRET);
        queue = Volley.newRequestQueue(context, new OAuthStack(consumer));
    }


    private static String getSearchURL(String path) {
        return new Uri.Builder()
                .appendPath()
    }

    public static void search(final IRequestable callback) {
        GsonRequest<YelpPlacesResponse> request = new GsonRequest<YelpPlacesResponse>(
                getSearchURL("/"),
                YelpPlacesResponse.class,
                new Response.Listener<YelpPlacesResponse>() {
                    @Override
                    public void onResponse(YelpPlacesResponse response) {
                        callback.completed(response.places);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.failed(error.getLocalizedMessage());
                    }
                }
        );
        queue.add(request);
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
                //request.getUrl().put("radius", ConstantUtil.NEAREST_PLACES_RADIUS);
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
