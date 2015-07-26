package relish.permoveo.com.relish.util;

import java.util.ArrayList;

import relish.permoveo.com.relish.model.Place;

/**
 * Created by Roman on 22.07.15.
 */
public class FakeData {
    public static final String HEADER_IMAGE_URL = "http://parisgid.ru/wp-content/uploads/2013/01/Restaurant.jpg";
    public static final String HEADER_PLACE_NAME = "Resto";
    public static final int HEADER_PLACE_RATING = 4;
    public static final double HEADER_PLACE_DISTANCE = 2;
    public static final int HEADER_PRICE_RANKING = 2;

    public static String[] PLACES_IMAGES = new String[]{
            "https://static.cntraveller.ru/media/material/0001/42/4f7cf433812a956f78024f49616713b3503a9d67.jpeg",
            "http://www.funsochi.ru/gallery/albums/userpics/100001/FSM_7679.jpg",
            "http://www.villa-arte.ru/images/bg-restaurant.jpg",
            "http://goldenapplehotel.ru/images/editor/int_1183013204.jpg",
            "http://www.gallery-spb.ru/public/images/old/interiors/img/mal_didgest_2.jpg",
            "http://7ba.ru/tag/545498.jpg",
            "http://www.alexander-land.ru/upload/iblock/12c/12c557128f8d46f0b175ec3ae9849916.JPG",
            "http://www.uga-electro.ru/upload/wysiwyg/Clip_3.jpg",
            "http://muzey-rest.ru/wp-content/themes/muzey-rest/images/DSC_0047.jpeg",
            "http://mahagon-events.ru/wp-content/uploads/2014/02/2-Restoran-Lyuche-.jpg"
    };

    public static ArrayList<Place> getFakePlaces() {
        ArrayList<Place> fakePlaces = new ArrayList();

        for (int i = 0; i < PLACES_IMAGES.length; i++) {
            fakePlaces.add(new Place(PLACES_IMAGES[i], "Rest " + i, i, 3.5, 3));
        }

        return fakePlaces;
    }
}
