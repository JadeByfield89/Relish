package relish.permoveo.com.relish.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitesFragment extends Fragment {


    public InvitesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invites, container, false);
    }


}
