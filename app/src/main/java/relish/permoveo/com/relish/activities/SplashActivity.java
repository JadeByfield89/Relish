package relish.permoveo.com.relish.activities;

import android.animation.Animator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.parse.ParseUser;

import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.titanic.Titanic;
import relish.permoveo.com.relish.titanic.TitanicTextView;
import relish.permoveo.com.relish.util.TypefaceUtil;

public class SplashActivity extends RelishActivity {

    private static final int SPLASH_DELAY = 100;
    private MediaPlayer mediaPlayer;
    private boolean isActivityOnScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        updateStatusBar(getResources().getColor(R.color.main_color_dark));


        TitanicTextView splashText = ButterKnife.findById(this, R.id.tv_splash);

        splashText.setTypeface(TypefaceUtil.BRANNBOLL_BOLD);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(2000);

        mediaPlayer = MediaPlayer.create(this, R.raw.wine_pour);
        Titanic titanic = new Titanic();
        titanic.setAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaPlayer.start();
                    }
                }, 1000);
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
        });
        splashText.startAnimation(in);
        titanic.start(splashText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isActivityOnScreen) {
                    if (ParseUser.getCurrentUser() == null) {
                        startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    }
                    finish();
                }
            }
        }, SPLASH_DELAY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityOnScreen = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        isActivityOnScreen = false;
    }
}
