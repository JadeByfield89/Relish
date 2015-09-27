package relish.permoveo.com.relish.interfaces;

import relish.permoveo.com.relish.model.Invite;

/**
 * Created by rom4ek on 02.09.2015.
 */
public interface InviteCreator {
    Invite getInvite();

    void updateInvite(Invite invite);
}
