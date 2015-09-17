package relish.permoveo.com.relish.model;

import com.parse.ParseFile;

import java.io.Serializable;

/**
 * Created by Roman on 13.08.15.
 */
public class Contact extends InvitePerson implements Serializable {
    public boolean isSelected;
    public long longId;
    public String email;
    public boolean isInvited;
    public ParseFile imageFile;

    public Contact() {}

    public Contact(String id) {
        this.id = id;
    }
}
