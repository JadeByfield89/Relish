package relish.permoveo.com.relish.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

/**
 * Created by byfieldj on 7/31/15.
 */
public class WelcomeFragment extends Fragment {

    @Bind(R.id.image_welcome)
    ImageView imageView;

    public static final String  EXTRA_IMAGE_ID = "image_id";
    public static final String EXTRA_TITLE = "title";
    private String pageTitle;
    private int imageId;


    public WelcomeFragment(){

    }

    public static WelcomeFragment newInstance(String title, int drawableId){
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_TITLE, title);
        bundle.putInt(EXTRA_IMAGE_ID, drawableId);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageTitle = getArguments().getString(EXTRA_TITLE);
        imageId = getArguments().getInt(EXTRA_IMAGE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, v);

        imageView.setBackgroundResource(imageId);
        return v;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }



}
