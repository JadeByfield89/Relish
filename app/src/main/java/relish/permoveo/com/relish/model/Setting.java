package relish.permoveo.com.relish.model;

import java.util.Map;

/**
 * Created by byfieldj on 9/3/15.
 */
public class Setting {

    private String title;
    private String subtitle;
    private boolean hasToggle;

    public Setting(String title, String subtitle, boolean hasToggle) {
        this.title = title;
        this.subtitle = subtitle;
        this.hasToggle = hasToggle;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public boolean hasToggle() {
        return hasToggle;
    }


}
