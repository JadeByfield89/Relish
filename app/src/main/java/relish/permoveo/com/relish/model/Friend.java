package relish.permoveo.com.relish.model;

import com.parse.ParseGeoPoint;

import java.io.Serializable;

/**
 * Created by rom4ek on 09.08.2015.
 */
public class Friend implements Serializable {
    public String id;
    public String image;
    public String name;
    public boolean isMyFriend;
    public ParseGeoPoint location;

}
