package relish.permoveo.com.relish.interfaces;

import relish.permoveo.com.relish.model.Invite;
import relish.permoveo.com.relish.model.google.GooglePlace;

/**
 * Created by rom4ek on 02.09.2015.
 */
public interface InviteCreator {
    Invite getInvite();

    void updateInviteWithGooglePlace(GooglePlace googlePlace);
}
