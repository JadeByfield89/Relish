package relish.permoveo.com.relish.model;

import java.io.Serializable;

public class NavDrawerItem implements Serializable {
    public String title;
    public int icon;
    public int counter;

    public NavDrawerItem() {
    }

    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

}