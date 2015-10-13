package relish.permoveo.com.relish.interfaces;

import relish.permoveo.com.relish.model.Invite;

/**
 * Created by rom4ek on 06.09.2015.
 */
public interface OnInviteSentListener {
    void onInviteSent(boolean success, Invite invite);
}
