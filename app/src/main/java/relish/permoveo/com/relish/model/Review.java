package relish.permoveo.com.relish.model;

import android.text.TextUtils;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Review))
            return false;
        Review other = (Review) o;
        return (!TextUtils.isEmpty(other.authorName) && !TextUtils.isEmpty(authorName) && other.authorName.equals(authorName));
    }
}
