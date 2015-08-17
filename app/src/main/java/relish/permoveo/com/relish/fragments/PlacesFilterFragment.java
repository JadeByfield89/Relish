package relish.permoveo.com.relish.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import relish.permoveo.com.relish.R;

/**
 * Created by byfieldj on 8/16/15.
 */
public class PlacesFilterFragment extends Fragment{


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_filter_places, container, false);
        return v;
    }
}
