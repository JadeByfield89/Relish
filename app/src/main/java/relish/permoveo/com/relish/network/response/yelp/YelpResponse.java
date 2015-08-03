package relish.permoveo.com.relish.network.response.yelp;

import relish.permoveo.com.relish.network.error.YelpError;

/**
 * Created by rom4ek on 03.08.2015.
 */
public class YelpResponse {
    public YelpError error;

    public boolean isSuccessful() {
        return error == null;
    }
}
