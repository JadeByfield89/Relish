package relish.permoveo.com.relish.dialogs;


import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendGroupsDialog extends DialogFragment {


    public FriendGroupsDialog() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_friends_group, container, false);
        // Inflate the layout for this fragment
        return v;
    }


}
