package relish.permoveo.com.relish.model;

/**
 * Created by rom4ek on 03.08.2015.
 */
public interface Review {
    String getLargeAuthorImage();

    String getAuthorName();

    void setAuthorName(String name);

    String getAuthorImage();

    void setAuthorImage(String image);

    String getText();

    long getTime();

    float getRating();

    String getAuthorUrl();

    String getRatingImage();
}
