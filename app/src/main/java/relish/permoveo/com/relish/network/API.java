package relish.permoveo.com.relish.network;

import android.content.Context;
import android.os.AsyncTask;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import java.io.IOException;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.network.request.yelp.PlaceDetailsRequest;
import relish.permoveo.com.relish.network.request.yelp.SearchRequest;
import relish.permoveo.com.relish.util.ConstantUtil;

/**
 * Created by Roman on 23.07.15.
 */
public class API {

    /*=========================== YELP API ===========================*/
    public static OAuthService service;
    public static Token accessToken;


    public static void init(Context context) {
        service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(ConstantUtil.YELP_CONSUMER_KEY).apiSecret(ConstantUtil.YELP_CONSUMER_SECRET).build();
        accessToken = new Token(ConstantUtil.YELP_TOKEN, ConstantUtil.YELP_TOKEN_SECRET);
    }

    public static void yelpSearch(int page, final IRequestable callback) {
        new SearchRequest(callback).execute(page);
    }

    public static void getYelpPlaceDetails(String id, final IRequestable callback) {
        new PlaceDetailsRequest(callback).execute(id);
    }

    /*=========================== GOOGLE PLACES API ===========================*/
    private static HttpTransport transport = new ApacheHttpTransport();

    public static HttpRequestFactory createRequestFactory() {

        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent(ConstantUtil.RELISH_USER_AGENT);
                request.setHeaders(headers);
            }
        });
    }

    @SuppressWarnings("Unused")
    private static class LoadPlaceDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpRequestFactory httpRequestFactory = createRequestFactory();
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


}
