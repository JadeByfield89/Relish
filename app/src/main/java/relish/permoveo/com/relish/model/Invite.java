package relish.permoveo.com.relish.model;

import android.text.TextUtils;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;

import relish.permoveo.com.relish.model.yelp.YelpPlace;
import relish.permoveo.com.relish.util.StaticMapsUtil;

/**
 * Created by byfieldj on 8/15/15.
 */
public class Invite implements Serializable {
    // Place info
    public String image;
    public String name;
    public YelpPlace.PlaceLocation location;
    public float rating;
    public String phone;
    public String url;

    // Invite info
    public String id;
    public String inviteId;
    public String creatorId;
    public String title;
    public int reminder = -1;
    public long date = 0l;
    public long time = 0l;
    public boolean isSent = false;
    public String note;
    public String mapSnapshot;
    public InviteStatus status;
    public ArrayList<InvitePerson> invited = new ArrayList<>();
    public ArrayList<InvitePerson> accepted = new ArrayList<>();
    public ArrayList<InvitePerson> declined = new ArrayList<>();

    public static Invite from(YelpPlace place) {
        Invite invite = new Invite();
        invite.name = place.name;
        if (!TextUtils.isEmpty(place.image))
            invite.image = place.getOriginalImage();
        if (!TextUtils.isEmpty(place.phone))
            invite.phone = place.phone;
        invite.rating = place.rating;
        invite.location = place.location;
        invite.mapSnapshot = StaticMapsUtil.buildUrl(place.location.lat, place.location.lng);
        return invite;
    }

    public static Invite from(ParseObject parseObject) {
        Invite invite = new Invite();
        invite.id = parseObject.getObjectId();
        invite.date = parseObject.getLong("date");
        invite.time = parseObject.getLong("time");
        invite.title = parseObject.getString("title");
        if (parseObject.containsKey("placeImage"))
            invite.image = parseObject.getString("placeImage");
        if (parseObject.containsKey("note"))
            invite.note = parseObject.getString("note");
        if (parseObject.containsKey("placePhone"))
            invite.phone = parseObject.getString("placePhone");
        if (parseObject.containsKey("placeRating"))
            invite.rating = parseObject.getNumber("placeRating").floatValue();
        if (parseObject.containsKey("placeUrl"))
            invite.url = parseObject.getString("placeUrl");
        invite.name = parseObject.getString("placeName");
        invite.mapSnapshot = parseObject.getString("mapSnapshot");
        invite.creatorId = parseObject.getString("creatorId");
        invite.location = new YelpPlace.PlaceLocation(parseObject.getString("address"), (ParseGeoPoint) parseObject.get("location"));
        return invite;
    }

    public InvitePerson getPersonById(String id) {
        ArrayList<InvitePerson> common = new ArrayList<>();
        common.addAll(invited);
        common.addAll(declined);
        common.addAll(accepted);
        for (InvitePerson person : common) {
            if (id.equals(person.id))
                return person;
        }
        return null;
    }

    public String getFormattedDate() {
        DateTime jodaDate = new DateTime().withMillis(date);
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("E, MMMM d");
        return dateFormatter.print(jodaDate);
    }

    public String getFormattedTime() {
        DateTime jodaTime = new DateTime().withMillis(time);
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm a");
        return timeFormatter.print(jodaTime);
    }

    public String getFormattedDateTime() {
        DateTime jodaTime = new DateTime().withMillis(time);
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("h:mm a");

        DateTime jodaDate = new DateTime().withMillis(date);
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMM d");

        return dateFormatter.print(jodaDate) + " / " + timeFormatter.print(jodaTime);
    }

    public String getFormattedAddress() {
        return location.address.substring(0, location.address.indexOf(','));
    }

    public enum InviteType {
        RECEIVED, RESPONSE, UPDATE;

        public static InviteType parse(String type) {
            switch (type) {
                case "inviteReceived":
                    return RECEIVED;
                case "inviteResponse":
                    return RESPONSE;
                case "inviteUpdate":
                    return UPDATE;
            }
            return null;
        }

        @Override
        public String toString() {
            switch (this) {
                case RECEIVED:
                    return "inviteReceived";
                case RESPONSE:
                    return "inviteResponse";
                case UPDATE:
                    return "inviteUpdate";
            }
            return super.toString();
        }
    }

    public enum InviteStatus {
        PENDING, ACCEPTED, DECLINED;

        @Override
        public String toString() {
            switch (this) {
                case PENDING:
                    return "";
                case ACCEPTED:
                    return "Accepted";
                case DECLINED:
                    return "Declined";
            }
            return super.toString();
        }
    }
}
