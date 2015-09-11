package relish.permoveo.com.relish.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 7/31/15.
 */
public class WelcomeFragment extends Fragment {

    @Bind(R.id.image_welcome)
    ImageView imageView;

    @Bind(R.id.text_header)
    TextView headerText;

    @Bind(R.id.text_sub)
    TextView subText;

    public static final String EXTRA_IMAGE_ID = "image_id";
    public static final String EXTRA_POSITION = "position";
    private int pagePosition;
    private int imageId;


    public WelcomeFragment() {

    }

    public static WelcomeFragment newInstance(int position, int drawableId) {
        WelcomeFragment fragment = new WelcomeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_POSITION, position);
        bundle.putInt(EXTRA_IMAGE_ID, drawableId);
        fragment.setArguments(bundle);

        return fragment;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pagePosition = getArguments().getInt(EXTRA_POSITION);
        imageId = getArguments().getInt(EXTRA_IMAGE_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, v);

        imageView.setBackgroundResource(imageId);

        headerText.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        subText.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        YoYo.with(Techniques.FadeInDown).playOn(headerText);
        headerText.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeInUp).playOn(subText);
        subText.setVisibility(View.VISIBLE);

        switch (pagePosition) {
            case 0:

                headerText.setText(R.string.welcome_1_header);
                subText.setText(R.string.welcome_1_sub);


                break;
            case 1:
                headerText.setText(R.string.welcome_2_header);
                subText.setText(R.string.welcome_2_sub);
                break;
            case 2:
                headerText.setText(R.string.welcome_3_header);
                subText.setText(R.string.welcome_3_sub);
                break;
            case 3:
                headerText.setText(R.string.welcome_4_header);
                subText.setText(R.string.welcome_4_sub);
                // If this is the last page of the welcome viewpager
                // We need to move the notification imageview down a bit
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

//                params.topMargin = 500;
//                imageView.setLayoutParams(params);
                break;
        }
        return v;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }


}
