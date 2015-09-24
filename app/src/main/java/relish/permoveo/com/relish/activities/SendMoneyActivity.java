package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 9/23/15.
 */
public class SendMoneyActivity extends RelishActivity {

    @Bind(R.id.tvHeading)
    TextView titleHeading;

    @Bind(R.id.ivSquareCash)
    ImageView squareIcon;

    @Bind(R.id.tvSquareCash)
    TextView titleSquareCash;

    @Bind(R.id.ivVenmo)
    ImageView venmo;

    @Bind(R.id.tvVenmo)
    TextView titleVenmo;

    @Bind(R.id.ivGoogleWallet)
    ImageView googleWallet;

    @Bind(R.id.tvGoogleWallet)
    TextView titleGoogleWallet;

    @Bind(R.id.toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0.0f);

        SpannableString s = new SpannableString("Split & Send");
        s.setSpan(new TypefaceSpan(this, "ProximaNovaBold.ttf"), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(s);

        titleHeading.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        titleSquareCash.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        titleVenmo.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);
        titleGoogleWallet.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);


        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);



        titleSquareCash.startAnimation(in);
        titleVenmo.startAnimation(in);
        titleGoogleWallet.startAnimation(in);
    }

    @Override
    protected void onResume() {
        super.onResume();

        squareIcon.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInLeft).duration(600).playOn(squareIcon);

        googleWallet.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInRight).duration(600).playOn(googleWallet);

        venmo.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInDown).duration(600).playOn(venmo);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
