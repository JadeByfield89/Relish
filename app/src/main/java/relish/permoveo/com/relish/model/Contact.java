package relish.permoveo.com.relish.model;

import java.io.Serializable;

/**
 * Created by Roman on 13.08.15.
 */
public class Contact extends InvitePerson implements Serializable {
    public boolean isSelected;
    public long id;
    public boolean isInvited;
}
