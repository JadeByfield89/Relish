package relish.permoveo.com.relish.model;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class Friend extends InvitePerson implements Serializable {
    public String id;
    public String address;
    public boolean isMyFriend;
    public boolean isSelected;
    public double lat;
    public double lng;

    public String formatDistance(double distance) {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
        if (distance == (long) distance)
            return String.format("%d", (long) distance);
        else
            return twoDForm.format(distance);
    }
}
