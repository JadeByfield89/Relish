package relish.permoveo.com.relish.network;

import android.os.AsyncTask;

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

import java.io.IOException;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.response.PlacesResponse;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by Roman on 23.07.15.
 */
public class API {

    private static HttpTransport transport = new ApacheHttpTransport();
    private static String json;

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

    private static class LoadPlaceDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
            HttpRequest request = null;
            try {
                request = httpRequestFactory.buildGetRequest(new GenericUrl(ConstantUtil.PLACE_DETAILS_URL));
                request.getUrl().put("key", ConstantUtil.GOOGLE_API_KEY);
                request.getUrl().put("reference", params[0]);

                json = request.execute().parseAsString();
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
//                request.getUrl().put("radius", ConstantUtil.NEAREST_PLACES_RADIUS);
                request.getUrl().put("types", "restaurant");

                PlacesResponse response = request.execute().parseAs(PlacesResponse.class);
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
