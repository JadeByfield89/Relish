package relish.permoveo.com.relish.util;

/**
 * Created by rom4ek on 20.07.2015.
 */
public class ConstantUtil {
    /*========================= PARSE CONSTANTS ========================= */
    public static final String PARSE_APPLICATION_ID = "mfYDXykMfWxUzvS87AmceWD7oRQthFGskiolqBPU";
    public static final String PARSE_CLIENT_KEY = "HEwTJWpRhiD6Qh8WA5UZ5lkYGsDYNFHMLfxvvg2N";

    /*========================= YELP CONSTANTS ========================= */
    public static final String YELP_CONSUMER_KEY = "qPVCf4vyeQwZoaj-ZJMdXg";
    public static final String YELP_CONSUMER_SECRET = "NkpaNQabNmdsXceRjGiP3SeBKtM";
    public static final String YELP_TOKEN = "jLL5toN_88ddKKi0pZSmYlGSLwgKv8gj";
    public static final String YELP_TOKEN_SECRET = "dwwH_xBGH_YJGHZB2KVUrkAEMdM";

    /*========================= YELP PLACES API CONSTANTS ========================= */
    public static final String YELP_PLACES_SEARCH_URL = "http://api.yelp.com/v2/search";
    public static final String YELP_PLACE_DETAILS = "http://api.yelp.com/v2/business";
    public static final int PLACES_LIMIT_SEARCH = 20;
    public static final int PLACES_SORTING_ORDER = 1;
    public static final int PLACES_RADIUS_SEARCH = 4000;

    /*========================= PLACES API CONSTANTS ========================= */
    public static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String PLACE_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final String PLACES_SEARCH_TEXT_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    public static final String PLACES_AUTOCOMPLETE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    public static final String GOOGLE_PLUS_PERSON_URL = "https://www.googleapis.com/plus/v1/people";
    public static final String GOOGLE_API_KEY = "AIzaSyC9iqxOdLUvyfOlMKSJaFRaTnmtqiWbkeI";
    //public static final String GOOGLE_API_KEY = "AIzaSyBS3kWo2opsPBrqsCjYX1JSJA7D2P2t0F4";
    public static final String RELISH_USER_AGENT = "relish.permoveo.com.relish.API";
    public static final int NEAREST_PLACES_RADIUS = 4500;
    public static final int MATCHING_PLACE_RADIUS = 500;

    /*========================= PLACES PHOTOS API CONSTANTS ========================= */
    public static final int PLACE_PHOTO_WIDTH = 320;
    public static final int PLACE_PHOTO_HEIGHT = 320;
    public static final String PLACE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?";

    /*========================= APPLICAITON CONSTANTS ========================= */
    public static final String ACTION_GET_LOCATION = "relish.permoveo.com.relish.ACTION_GET_LOCATION";
    public static final String ACCEPT_ACTION = "relish.permoveo.com.relish.NOTIFICATION_ACCEPT";
    public static final String DECLINE_ACTION = "relish.permoveo.com.relish.NOTIFICATION_DECLINE";
    public static final String NOTIFICATION_ACTION_EXTRA = "extra_action_notification";
    public static final String INVITE_ID_EXTRA = "extra_invite_id";
    public static final String TO_INVITES_LIST = "extra_from_notification";
    public static final String NOTIFICATION_ID_EXTRA = "extra_notification_id";
    public static final String INVITE_EXTRA = "extra_invite";

    /*========================= PUSH JSON DATA KEYS ========================= */
    public static final String SENDER_IMAGE_KEY = "key_sender_image";
    public static final String INVITE_TITLE_KEY = "key_invite_title";
    public static final String INVITE_MESSAGE_KEY = "key_invite_message";

    /*========================Square & Goole Wallet Package names==============*/
    public static final String SQUARE_CASH_PACKAGE = "com.squareup.cash";
    public static final String GOOGLE_WALLET_PACKAGE = "com.google.android.apps.gmoney";

    /*========================Flurry API Key============================================*/
    public static final String FLURRY_API_KEY = "4PTHWNSP9NDVMJJWTZFQ";
}
