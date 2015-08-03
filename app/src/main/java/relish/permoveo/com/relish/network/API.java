package relish.permoveo.com.relish.network;

import android.content.Context;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.Yelp.YelpPlace;
import relish.permoveo.com.relish.network.request.google.GoogleAuthorRequest;
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

    public static void googleSearch(YelpPlace restaurant, IRequestable callback) {
        new relish.permoveo.com.relish.network.request.google.SearchRequest(callback).execute(restaurant);
    }

    public static void getGooglePlaceDetails(String id, final IRequestable callback) {
        new relish.permoveo.com.relish.network.request.google.PlaceDetailsRequest(callback).execute(id);
    }


    public static void getGoogleAuthorImage(String id, IRequestable callback) {
        new GoogleAuthorRequest(callback).execute(id);
    }

}
