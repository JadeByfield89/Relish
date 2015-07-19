package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import com.romainpiel.titanic.library.Titanic;
import com.romainpiel.titanic.library.TitanicTextView;

import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

public class SplashActivity extends ActionBarActivity {

    private static final int SPLASH_DELAY = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TitanicTextView splashText = ButterKnife.findById(this, R.id.tv_splash);

        Typeface satisfy = Typeface.createFromAsset(getAssets(), "fonts/Satisfy-Regular.ttf");
        splashText.setTypeface(satisfy);

        Titanic titanic = new Titanic();
        titanic.start(splashText);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }

}
