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

import java.util.ArrayList;

import relish.permoveo.com.relish.interfaces.IRequestable;
import relish.permoveo.com.relish.model.google.GooglePlace;
import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.network.request.google.GoogleAuthorRequest;
import relish.permoveo.com.relish.network.request.google.PlacesAutocompleteRequest;
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
    /*=========================== GOOGLE PLACES API ===========================*/
    private static HttpTransport transport = new ApacheHttpTransport();

    public static void init(Context context) {
        service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(ConstantUtil.YELP_CONSUMER_KEY).apiSecret(ConstantUtil.YELP_CONSUMER_SECRET).build();
        accessToken = new Token(ConstantUtil.YELP_TOKEN, ConstantUtil.YELP_TOKEN_SECRET);
    }

    public static void yelpSearch(int page, final IRequestable callback, boolean byCategory, ArrayList<String> categories) {
        if (byCategory) {
            new SearchRequest(callback, categories).execute(page);
        } else {
            new SearchRequest(callback).execute(page);

        }
    }

    public static void getYelpPlaceDetails(String id, final IRequestable callback) {
        new PlaceDetailsRequest(callback).execute(id);
    }

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

    private static ArrayList<GooglePlace> getFakeData() {
        GooglePlace place1 = new GooglePlace();
        place1.name = "Noah";
        place1.address = "Beloyarskaya Str. 19, Yekaterinburg";
        place1.rating = 4.0;

        GooglePlace place2 = new GooglePlace();
        place2.name = "Noah";
        place2.address = "Beloyarskaya Str. 19, Yekaterinburg";
        place2.rating = 4.0;

        GooglePlace place3 = new GooglePlace();
        place3.name = "Noah";
        place3.address = "Beloyarskaya Str. 19, Yekaterinburg";
        place3.rating = 4.0;

        ArrayList<GooglePlace> places = new ArrayList<>();
        places.add(place1);
        places.add(place2);
        places.add(place3);
        return places;
    }

    public static void googlePlacesAutocomplete(String input, IRequestable callback) {
        //callback.completed(getFakeData());
        new PlacesAutocompleteRequest(callback).execute(input);
    }

}
