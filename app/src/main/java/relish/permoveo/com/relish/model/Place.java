package relish.permoveo.com.relish.model;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.LocationUtil;

/**
 * Created by Roman on 20.07.15.
 */
public class Place implements Serializable {
    @Key
    public String name;

    @Key
    public String reference;

    @Key
    public String id;

    @Key(value = "place_id")
    public String placeId;

    @Key
    public double rating;

    @Key(value = "price_level")
    private int priceLevel;

    @Key
    public PlaceGeometry geometry;

    @Key
    public List<PlacePhoto> photos;

    public Place() {
        priceLevel = -1;
    }

    public Place(String image, String name, double distance, double rating, int priceRanking) {
        this.name = name;
//        this.distance = distance;
        this.rating = rating;
        this.priceLevel = priceRanking;
    }


    public String getPriceLevel() {
        switch (priceLevel) {
            case -1:
                return "";
            case 0:
                return "Free";
            case 1:
                    return "$";
            case 2:
                    return "$$";
            case 3:
                    return "$$$";
            case 4:
                return "$$$$";
            }
        return "";
    }

    public double getCalculatedDistance() {
        return LocationUtil.distance(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude(),
                geometry.location.lat, geometry.location.lng);
    }

    public String formatDistance() {
        double distance = LocationUtil.distance(GPSTracker.get.getLocation().getLatitude(), GPSTracker.get.getLocation().getLongitude(),
                geometry.location.lat, geometry.location.lng);

        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (distance == (long) distance)
            return String.format("%d", (long) distance);
        else
            return twoDForm.format(distance);
    }

    public String getImage() {
        if (photos == null || photos.size() == 0) return "";

        return ConstantUtil.PLACE_PHOTO_URL +
                new StringBuilder()
                .append("maxwidth=" + ConstantUtil.PLACE_PHOTO_WIDTH)
                .append("&")
                .append("maxheight=" + ConstantUtil.PLACE_PHOTO_HEIGHT)
                .append("&")
                .append("photoreference=" + photos.get(0).reference)
                .append("&")
                .append("key=" + ConstantUtil.GOOGLE_API_KEY)
                .toString();
    }

    public String getLargeImage() {
        if (photos == null || photos.size() == 0) return "";
        String url =  ConstantUtil.PLACE_PHOTO_URL +
                new StringBuilder()
                        .append("maxwidth=" + photos.get(0).width)
                        .append("&")
                        .append("maxheight=" + photos.get(0).height)
                        .append("&")
                        .append("photoreference=" + photos.get(0).reference)
                        .append("&")
                        .append("key=" + ConstantUtil.GOOGLE_API_KEY)
                        .toString();

        return url;
    }

    public static class PlacePhoto {
        @Key
        public int width;

        @Key
        public int height;

        @Key(value = "photo_reference")
        public String reference;
    }

    public static class PlaceGeometry {
        @Key
        public Location location;
    }

    public static class Location {
        @Key
        public double lat;

        @Key
        public double lng;
    }

}
