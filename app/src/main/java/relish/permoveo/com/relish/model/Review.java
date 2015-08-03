package relish.permoveo.com.relish.model;

import java.io.Serializable;

/**
 * Created by rom4ek on 03.08.2015.
 */
public class Review implements Serializable {
    public String authorName;
    public String authorImage;
    public float rating;
    public String text;
    public long time;

    public String getLargeAuthorImage() {
        return "";
    }
}
