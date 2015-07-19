package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.parse.ParseUser;

import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.titanic.Titanic;
import relish.permoveo.com.relish.titanic.TitanicTextView;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 6000;
    private MediaPlayer mediaPlayer;
    private boolean isActivityOnScreen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TitanicTextView splashText = ButterKnife.findById(this, R.id.tv_splash);

        Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");
        splashText.setTypeface(satisfy);

        mediaPlayer = MediaPlayer.create(this, R.raw.wine_pour);
        Titanic titanic = new Titanic();
        titanic.start(splashText);
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isActivityOnScreen) {
                    if (ParseUser.getCurrentUser() == null) {
                        startActivity(new Intent(SplashActivity.this, SignupActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
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
