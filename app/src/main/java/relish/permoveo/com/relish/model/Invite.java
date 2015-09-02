package relish.permoveo.com.relish.model;

import java.io.Serializable;

import relish.permoveo.com.relish.model.yelp.YelpPlace;

/**
 * Created by byfieldj on 8/15/15.
 */
public class Invite implements Serializable {
    public String name;
    public YelpPlace.PlaceLocation location;
    public String title;
    public int reminder = -1;
    public long date = 0l;
    public long time = 0l;
    public String note;
}
