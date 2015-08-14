package relish.permoveo.com.relish.animation;

import android.view.animation.AccelerateDecelerateInterpolator;

import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by byfieldj on 8/14/15.
 */
public class AnimationUtils {

    public static boolean wasAnimatedToTop;
    public static boolean wasAnimatedToBottom;

    public static void animateFAB(FloatingActionButton fab, final boolean toTop, final boolean toBottom, float y){

        ViewPropertyAnimator.animate(fab).setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(300)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        wasAnimatedToTop = toTop;
                        wasAnimatedToBottom = toBottom;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .y(y);

    }
}
