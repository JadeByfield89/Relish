package relish.permoveo.com.relish.model;

import com.parse.ParseGeoPoint;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class Friend implements Serializable {
    public String id;
    public String image;
    public String name;
    public String group;
    public String address;
    public ParseGeoPoint location;

    public String formatDistance(double distance) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (distance == (long) distance)
            return String.format("%d", (long) distance);
        else
            return twoDForm.format(distance);
    }
}
