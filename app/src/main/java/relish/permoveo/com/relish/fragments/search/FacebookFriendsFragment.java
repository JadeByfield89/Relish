package relish.permoveo.com.relish.fragments.search;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFriendsFragment extends Fragment {


    public FacebookFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_friends, container, false);
    }


}
