package relish.permoveo.com.relish.fragments.inviteflow;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.interfaces.OnInviteSentListener;

/**
 * Created by byfieldj on 8/18/15.
 */
public class EmptyCardFragment extends Fragment {

    private static final int ANIMATION_DURATION = 500;
    @Bind(R.id.button_animate)
    Button animateButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_invite_card, container, false);

        ButterKnife.bind(this, v);

        animateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSendAnimation(v);
            }
        });
        return v;
    }

    private void startSendAnimation(View view) {
        YoYo.with(Techniques.ZoomOutRight).duration(ANIMATION_DURATION).withListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
            }

            @Override
            public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

            }

            @Override
            public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

            }
        }).playOn(view);
    }


}
