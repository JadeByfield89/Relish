package relish.permoveo.com.relish.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import relish.permoveo.com.relish.R;

/**
 * Created by byfieldj on 8/18/15.
 */
public class InviteCardFragment extends Fragment {

    public static InviteCardFragment newInstance(){
        InviteCardFragment fragment = new InviteCardFragment();


        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite_card, container, false);
        return v;
    }
}
