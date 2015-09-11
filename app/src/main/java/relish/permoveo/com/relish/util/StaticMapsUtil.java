package relish.permoveo.com.relish.util;

/**
 * Created by rom4ek on 10.09.2015.
 */
public class StaticMapsUtil {

    private static final String STATIC_MAPS_BASE_URL = "http://maps.googleapis.com/maps/api/staticmap?";

    private static final int ZOOM = 17;
    private static final String ZOOM_QUERY = "&zoom=" + ZOOM;

    private static final String CENTER_QUERY = "center=";

    private static final String MARKERS_COLOR = "color:red";
    private static final String MARKERS_SIZE = "size:large";
    private static final String MARKERS_QUERY = "&markers=" + MARKERS_COLOR + "%7C" + MARKERS_SIZE;

    private static final int SIZE_WIDTH = 480;
    private static final int SIZE_HEIGHT = 320;
    private static final String SIZE_QUERY = "&size=" + SIZE_WIDTH + "x" + SIZE_HEIGHT;

    private static final boolean SENSOR = false;
    private static final String SENSOR_QUERY = "&sensor=" + SENSOR;

    public static String buildUrl(double lat, double lng) {
        return new StringBuilder(STATIC_MAPS_BASE_URL)
                .append(CENTER_QUERY + String.valueOf(lat) + "," + String.valueOf(lng))
                .append(ZOOM_QUERY)
                .append(SIZE_QUERY)
                .append(MARKERS_QUERY + "%7C" + String.valueOf(lat) + "," + String.valueOf(lng))
                .append(SENSOR_QUERY)
                .toString();
    }
}
