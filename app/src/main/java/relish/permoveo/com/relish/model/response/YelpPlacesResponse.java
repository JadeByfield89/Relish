package relish.permoveo.com.relish.model.response;

import java.util.List;

import relish.permoveo.com.relish.model.Place;

/**
 * Created by Roman on 29.07.15.
 */
public class YelpPlacesResponse {

    /**
     * total : 10651
     * region : {"center":{"longitude":-122.4026213,"latitude":37.7861386},"span":{"latitude_delta":0,"longitude_delta":0}}
     * businesses : [{"rating_img_url_large":"http://media3.ak.yelpcdn.com/static/201012161053250406/img/ico/stars/stars_large_3.png","snippet_text":"Sometimes we ask questions without reading an email thoroughly as many of us did for the last event.  In honor of Yelp, the many questions they kindly...","phone":"4159083801","menu_date_updated":1317414369,"rating_img_url":"http://media1.ak.yelpcdn.com/static/201012161694360749/img/ico/stars/stars_3.png","menu_provider":"yelp","review_count":3347,"location":{"neighborhoods":["SOMA"],"cross_streets":"3rd St & Opera Aly","state_code":"CA","display_address":["140 New Montgomery St","(b/t Natoma St & Minna St)","SOMA","San Francisco, CA 94105"],"address":["140 New Montgomery St"],"postal_code":"94105","country_code":"US","city":"San Francisco"},"is_closed":false,"is_claimed":true,"rating_img_url_small":"http://media1.ak.yelpcdn.com/static/201012162337205794/img/ico/stars/stars_small_3.png","url":"http://www.yelp.com/biz/yelp-san-francisco","id":"yelp-san-francisco","image_url":"http://s3-media2.ak.yelpcdn.com/bphoto/7DIHu8a0AHhw-BffrDIxPA/ms.jpg","name":"Yelp","snippet_image_url":"http://s3-media2.ak.yelpcdn.com/photo/LjzacUeK_71tm2zPALcj1Q/ms.jpg","display_phone":"+1-415-908-3801","mobile_url":"http://m.yelp.com/biz/4kMBvIEWPxWkWKFN__8SxQ","categories":[["Local Flavor","localflavor"],["Mass Media","massmedia"]]}]
     */
    public int total;
    public RegionEntity region;
    public List<Place> places;

    public class RegionEntity {
        /**
         * center : {"longitude":-122.4026213,"latitude":37.7861386}
         * span : {"latitude_delta":0,"longitude_delta":0}
         */
        public CenterEntity center;
        public SpanEntity span;

        public class CenterEntity {
            /**
             * longitude : -122.4026213
             * latitude : 37.7861386
             */
            public double longitude;
            public double latitude;
        }

        public class SpanEntity {
            /**
             * latitude_delta : 0.0
             * longitude_delta : 0.0
             */
            public double latitude_delta;
            public double longitude_delta;
        }
    }

    public class BusinessesEntity {
        /**
         * rating_img_url_large : http://media3.ak.yelpcdn.com/static/201012161053250406/img/ico/stars/stars_large_3.png
         * snippet_text : Sometimes we ask questions without reading an email thoroughly as many of us did for the last event.  In honor of Yelp, the many questions they kindly...
         * phone : 4159083801
         * menu_date_updated : 1317414369
         * rating_img_url : http://media1.ak.yelpcdn.com/static/201012161694360749/img/ico/stars/stars_3.png
         * menu_provider : yelp
         * review_count : 3347
         * location : {"neighborhoods":["SOMA"],"cross_streets":"3rd St & Opera Aly","state_code":"CA","display_address":["140 New Montgomery St","(b/t Natoma St & Minna St)","SOMA","San Francisco, CA 94105"],"address":["140 New Montgomery St"],"postal_code":"94105","country_code":"US","city":"San Francisco"}
         * is_closed : false
         * is_claimed : true
         * rating_img_url_small : http://media1.ak.yelpcdn.com/static/201012162337205794/img/ico/stars/stars_small_3.png
         * url : http://www.yelp.com/biz/yelp-san-francisco
         * id : yelp-san-francisco
         * image_url : http://s3-media2.ak.yelpcdn.com/bphoto/7DIHu8a0AHhw-BffrDIxPA/ms.jpg
         * name : Yelp
         * snippet_image_url : http://s3-media2.ak.yelpcdn.com/photo/LjzacUeK_71tm2zPALcj1Q/ms.jpg
         * display_phone : +1-415-908-3801
         * mobile_url : http://m.yelp.com/biz/4kMBvIEWPxWkWKFN__8SxQ
         * categories : [["Local Flavor","localflavor"],["Mass Media","massmedia"]]
         */
        public String rating_img_url_large;
        public String snippet_text;
        public String phone;
        public int menu_date_updated;
        public String rating_img_url;
        public String menu_provider;
        public int review_count;
        public LocationEntity location;
        public boolean is_closed;
        public boolean is_claimed;
        public String rating_img_url_small;
        public String url;
        public String id;
        public String image_url;
        public String name;
        public String snippet_image_url;
        public String display_phone;
        public String mobile_url;
        public List<List<String>> categories;

        public class LocationEntity {
            /**
             * neighborhoods : ["SOMA"]
             * cross_streets : 3rd St & Opera Aly
             * state_code : CA
             * display_address : ["140 New Montgomery St","(b/t Natoma St & Minna St)","SOMA","San Francisco, CA 94105"]
             * address : ["140 New Montgomery St"]
             * postal_code : 94105
             * country_code : US
             * city : San Francisco
             */
            public List<String> neighborhoods;
            public String cross_streets;
            public String state_code;
            public List<String> display_address;
            public List<String> address;
            public String postal_code;
            public String country_code;
            public String city;
        }
    }
}
