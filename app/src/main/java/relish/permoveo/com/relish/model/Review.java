package relish.permoveo.com.relish.model;

/**
 * Created by rom4ek on 03.08.2015.
 */
public interface Review {
    String getLargeAuthorImage();

    String getAuthorName();

    String getAuthorImage();

    String getText();

    long getTime();

    float getRating();

    void setAuthorImage(String image);

    void setAuthorName(String name);

    String getAuthorUrl();
}
