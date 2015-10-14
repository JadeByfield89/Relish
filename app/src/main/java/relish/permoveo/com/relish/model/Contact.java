package relish.permoveo.com.relish.model;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;

/**
 * Created by Roman on 13.08.15.
 */
public class Contact extends InvitePerson implements Serializable {
    public boolean isSelected;
    public long longId;
    public String email;
    public boolean isInvited;
    public transient ParseFile imageFile;

    public String twitterUsername;

    public Contact() {
    }

    public Contact(String id) {
        this.id = id;
    }

    public static Contact from(ParseObject parseObject) {
        Contact contact = new Contact();
        contact.name = parseObject.getString("contactName");
        contact.id = parseObject.getObjectId();
        if (parseObject.containsKey("contactEmail"))
            contact.email = parseObject.getString("contactEmail");
        if (parseObject.containsKey("contactNumber"))
            contact.number = parseObject.getString("contactNumber");
        if (parseObject.containsKey("twitterUsername"))
            contact.twitterUsername = parseObject.getString("twitterUsername");
        if (parseObject.containsKey("avatar")) {
            ParseFile parseFile = (ParseFile) parseObject.get("avatar");
            contact.image = parseFile.getUrl();
        }

        return contact;
    }
}
