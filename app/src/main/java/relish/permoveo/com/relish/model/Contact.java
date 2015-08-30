package relish.permoveo.com.relish.model;

import java.io.Serializable;

/**
 * Created by Roman on 13.08.15.
 */
public class Contact implements Serializable {
    public String number;
    public String name;
    public String image;
    public boolean isSelected;
    public long id;
    public boolean isInvited;
}
