package relish.permoveo.com.relish.model;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.Serializable;
import java.util.ArrayList;

import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by byfieldj on 8/15/15.
 */
public class Invite implements Serializable {
    public String id;
    public String creatorId;
    public String name;
    public YelpPlace.PlaceLocation location;
    public String title;
    public int reminder = -1;
    public long date = 0l;
    public long time = 0l;
    public boolean isSent = false;
    public String note;
    public String mapSnapshot;
    public ArrayList<InvitePerson> invited = new ArrayList<>();
    public ArrayList<InvitePerson> accepted = new ArrayList<>();
    public ArrayList<InvitePerson> declined = new ArrayList<>();

    public static Invite from(ParseObject parseObject) {
        Invite invite = new Invite();
        invite.id = parseObject.getObjectId();
        invite.date = parseObject.getLong("date");
        invite.time = parseObject.getLong("time");
        invite.title = parseObject.getString("title");
        if (parseObject.containsKey("note"))
            invite.note = parseObject.getString("note");
        invite.name = parseObject.getString("placeName");
        invite.mapSnapshot = parseObject.getString("mapSnapshot");
        invite.creatorId = parseObject.getString("creatorId");
        invite.location = new YelpPlace.PlaceLocation(parseObject.getString("address"), (ParseGeoPoint) parseObject.get("location"));
        return invite;
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
}
