package relish.permoveo.com.relish.animation;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import relish.permoveo.com.relish.R;

/**
 * Created by byfieldj on 8/14/15.
 */
public class AnimationUtils {

    public static boolean wasAnimatedToTop;
    public static boolean wasAnimatedToBottom;

    public static void animateFAB(FloatingActionButton fab, final boolean toTop, final boolean toBottom, float y) {

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

    public static void animateFavoriteButton(final ParseUser user, final ImageView icon, final String placeId, final boolean isFavorite) {

        YoYo.with(Techniques.BounceIn)
                .duration(500)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        ArrayList<String> favorites = (ArrayList<String>) user.get("favoritePlaces");
                        if (favorites == null)
                            favorites = new ArrayList<>();
                        if ((Boolean) icon.getTag()) {
                            favorites.remove(placeId);
                            icon.setImageResource(R.drawable.ic_favorite);
                        } else {
                            favorites.add(placeId);
                            icon.setImageResource(R.drawable.ic_favorite_selected);
                        }
                        icon.setTag(!(Boolean) icon.getTag());
                        user.put("favoritePlaces", favorites);
                        user.saveEventually();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(icon);


    }
}
