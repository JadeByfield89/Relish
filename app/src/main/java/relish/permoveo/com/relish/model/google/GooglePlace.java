package relish.permoveo.com.relish.model.google;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.util.ConstantUtil;
import relish.permoveo.com.relish.util.LocationUtil;

/**
 * Created by Roman on 20.07.15.
 */
public class GooglePlace implements Serializable {
    public String name;
    public String reference;
    public String id;
    private String image;
    @SerializedName("place_id")
    public String placeId;
    public double rating;
    @SerializedName("formatted_phone_number")
    public String phone;
    @SerializedName("formatted_address")
    public String address;
    @SerializedName("opening_hours")
    public OpeningHours openingHours;
    public PlaceGeometry geometry;
    public ArrayList<GoogleReview> reviews;
    public List<PlacePhoto> photos;
    private int priceLevel;

    public GooglePlace() {
        reviews = new ArrayList<>();
        priceLevel = -1;
    }

    public GooglePlace(String image, String name, double distance, double rating, int priceRanking) {
        this.name = name;
//        this.distance = distance;
        this.rating = rating;
        this.priceLevel = priceRanking;
    }

    public static GooglePlace from(Invite invite) {
        GooglePlace place = new GooglePlace();
        place.name = invite.name;
        place.rating = invite.rating;
        place.phone = invite.phone;
        place.address = invite.location.address;
        place.geometry = new PlaceGeometry(invite.location.lat, invite.location.lng);
        place.image = invite.image;
        return place;
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

    public String getHours() {
        DateTime dateTime = new DateTime();
        String hours = "";
        if (openingHours != null && openingHours.weekdayText != null && openingHours.weekdayText.size() > 0) {
            for (int i = 0; i < openingHours.weekdayText.size(); i++) {
                if (i == dateTime.getDayOfWeek())
                    hours = openingHours.weekdayText.get(i).substring(openingHours.weekdayText.get(i).indexOf(':') + 2,
                            openingHours.weekdayText.get(i).length());
            }
        }
        return hours;
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
        if (!TextUtils.isEmpty(image)) return image;
        if (photos == null || photos.size() == 0) return "";
        String url = ConstantUtil.PLACE_PHOTO_URL +
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

    public static class PlacePhoto implements Serializable {
        public int width;
        public int height;
        @SerializedName("photo_reference")
        public String reference;
    }

    public static class OpeningHours implements Serializable {
        @SerializedName("weekday_text")
        public ArrayList<String> weekdayText;
    }

    public static class PlaceGeometry implements Serializable {
        public Location location;

        public PlaceGeometry(double lat, double lng) {
            this.location = new Location(lat, lng);
        }
    }

    public static class Location implements Serializable {
        public double lat;
        public double lng;

        public Location(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

}
