package relish.permoveo.com.relish.fragments.search;


import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookFriendsFragment extends Fragment {


    @Bind(R.id.ivEmpty)
    ImageView emptyImage;

    @Bind(R.id.empty_message)
    TextView emptyMessage;

    public FacebookFriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facebook_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        emptyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacebookShareDialog();
            }
        });

        emptyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFacebookShareDialog();
            }
        });
    }

    private void showFacebookShareDialog() {

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("Relish")
                .setContentDescription("I'm using the #Relish app to discover new places to eat, and invite my friends to join me! Check it out guys. Get it now at http://relishwith.us")

                .setContentUrl(Uri.parse("http://relishwith.us"))
                .build();

        ShareDialog.show(this, linkContent);
    }


}
