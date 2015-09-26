package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Request;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.fragments.VenmoWebviewFragment;
import relish.permoveo.com.relish.network.response.VenmoResponse;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceSpan;
import relish.permoveo.com.relish.util.TypefaceUtil;
import relish.permoveo.com.relish.venmo.VenmoLibrary;
import relish.permoveo.com.relish.view.BounceProgressBar;

/**
 * Created by byfieldj on 9/23/15.
 */
public class SendMoneyActivity extends RelishActivity implements VenmoWebviewFragment.onVenmoAccessTokenRetrievedListener {

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

    @Bind(R.id.layout_pay)
    View payView;

    @Bind(R.id.bounce_progress)
    BounceProgressBar progress;

    @Bind(R.id.etAmount)
    EditText payAmount;

    @Bind(R.id.etEmailOrPhone)
    EditText emailOrPhone;

    @Bind(R.id.etNote)
    EditText note;

    @Bind(R.id.bSendMoney)
    Button sendMoney;

    private boolean payViewVisible;

    private static final int REQUEST_CODE_VENMO_APP_SWITCH = 89;
    private boolean paymentSent;


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
        sendMoney.setTypeface(TypefaceUtil.PROXIMA_NOVA_BOLD);

        emailOrPhone.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        payAmount.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        note.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        sendMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!paymentSent) {
                    if (validateFields()) {
                        makeVenmoPayment();
                    }
                }else{
                    finish();
                }
            }
        });

        payView.setVisibility(View.GONE);

        venmo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    handleVenmoPayment();

            }
        });


        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);


        titleSquareCash.startAnimation(in);
        titleVenmo.startAnimation(in);
        titleGoogleWallet.startAnimation(in);
    }

    private boolean validateFields() {

        if (TextUtils.isEmpty(emailOrPhone.getText().toString())) {
            Snackbar.make(payView, "Please enter a valid email address or phone number.", Snackbar.LENGTH_LONG).show();
            return false;
        }
        if(TextUtils.isEmpty(payAmount.getText().toString()) && TextUtils.isDigitsOnly(payAmount.getText().toString())){
            Snackbar.make(payView, "Please enter a valid amount to send.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if(TextUtils.isEmpty(note.getText().toString())){
            Snackbar.make(payView, "Please enter a note for this transaction.", Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void makeVenmoPayment() {
        emailOrPhone.setVisibility(View.GONE);
        payAmount.setVisibility(View.GONE);
        note.setVisibility(View.GONE);

        progress.setVisibility(View.VISIBLE);


        String amount = payAmount.getText().toString();
        String who = emailOrPhone.getText().toString();
        String payNote = note.getText().toString();


        new MakeVenmoPaymentTask(new onVenmoPaymentSentListener() {
            @Override
            public void onPaymentSent(boolean status) {
                progress.setVisibility(View.GONE);
                sendMoney.setText("Done");
                titleHeading.setText("Payment Sent!");
            }
        }, who, amount, payNote).execute();
    }

    private void handleVenmoPayment() {

        // User has Venmo already installed
        if (VenmoLibrary.isVenmoInstalled(this)) {
            String app_id = getString(R.string.venmo_app_id);
            String appName = getString(R.string.venmo_app_name);
            String recipient = "";


            Intent venmoIntent = VenmoLibrary.openVenmoPayment(app_id, appName, recipient, "", "", "");
            startActivityForResult(venmoIntent, REQUEST_CODE_VENMO_APP_SWITCH);


        }

        // Venmo is not installed
        else {
            if (TextUtils.isEmpty(SharedPrefsUtil.get.getVenmoAccessToken())) {
                VenmoWebviewFragment fragment = new VenmoWebviewFragment();
                fragment.show(getSupportFragmentManager(), "Venmo");
            } else {
                titleHeading.setText("Who do you want to pay?");
                hideViews();
                payView.setVisibility(View.VISIBLE);
                payViewVisible = true;
            }
        }
    }

    private void hideViews() {
        venmo.setVisibility(View.GONE);
        titleVenmo.setVisibility(View.GONE);

        squareIcon.setVisibility(View.GONE);
        titleSquareCash.setVisibility(View.GONE);

        googleWallet.setVisibility(View.GONE);
        titleGoogleWallet.setVisibility(View.GONE);
    }

    private void showViews() {
        venmo.setVisibility(View.VISIBLE);
        titleVenmo.setVisibility(View.VISIBLE);

        squareIcon.setVisibility(View.VISIBLE);
        titleSquareCash.setVisibility(View.VISIBLE);

        googleWallet.setVisibility(View.VISIBLE);
        titleGoogleWallet.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!payViewVisible) {
            squareIcon.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInLeft).duration(600).playOn(squareIcon);

            googleWallet.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInRight).duration(600).playOn(googleWallet);

            venmo.setVisibility(View.VISIBLE);
            YoYo.with(Techniques.SlideInDown).duration(600).playOn(venmo);
        }

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

    @Override
    public void onVenmoAccessTokenRetrieved(String token) {

        Toast.makeText(this, "Access Token -> " + token, Toast.LENGTH_LONG).show();
        SharedPrefsUtil.get.saveVenmoAccessToken(token);
        getSupportFragmentManager().popBackStack();
        titleHeading.setText("Who do you want to pay?");
        hideViews();
        payView.setVisibility(View.VISIBLE);
        payViewVisible = true;
    }

    private interface onVenmoPaymentSentListener {
        void onPaymentSent(boolean status);
    }

    private class MakeVenmoPaymentTask extends AsyncTask<Void, Void, String> {

        private onVenmoPaymentSentListener listener;
        private static final String VENMO_PAYMENTS_ENDPOINT = "https://api.venmo.com/v1/payments";

        String who;
        String amount;
        String note;


        public MakeVenmoPaymentTask(final onVenmoPaymentSentListener listener, final String who, final String amount, final String note) {
            this.listener = listener;

            this.who = who;
            this.amount = amount;
            this.note = note;
        }

        @Override
        protected String doInBackground(Void... params) {

            Request request = new Request(Verb.POST, VENMO_PAYMENTS_ENDPOINT);

            String whoParam = "email";

            // Email
            if (who.contains("@")) {
                if (who.contains(" ")) {
                    who = who.replace(" ", "");
                }
                whoParam = "email";
                Log.d("SendMoneyActivity", "Email -> " + who);
            }

            // Phone #
            else if (TextUtils.isDigitsOnly(who) && who.length() == 10) {
                whoParam = "phone";
            }

            // Phone # that was prefixed with "1"
            else if (TextUtils.isDigitsOnly(who) && who.length() == 11) {
                if (who.startsWith("1")) {
                    whoParam = "user_id";
                }
            }

            request.addBodyParameter(whoParam, who);
            request.addBodyParameter("amount", amount);
            request.addBodyParameter("note", note);
            Log.d("SendMoneyActivity", "Access Token -> " + SharedPrefsUtil.get.getVenmoAccessToken());
            request.addBodyParameter("access_token", SharedPrefsUtil.get.getVenmoAccessToken());

            Response response = request.send();
            String body = response.getBody();
            String status = "";

            try {
                JSONObject object = new JSONObject(body);
                JSONObject data = object.getJSONObject("data");
                Log.d("SendMoneyActivity", "Data Array -> " + data.toString());

                JSONObject payment = data.getJSONObject("payment");

                status = payment.getString("status");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("SendMoneyActivity", "Venmo Response -> " + body);

            /*VenmoResponse venmoResponse;
            Gson gson = new Gson();
            venmoResponse = gson.fromJson(body, VenmoResponse.class);*/
            return status;
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);

            if (status.contains("pending") || status.contains("settled")) {
                listener.onPaymentSent(true);
                paymentSent = true;
            } else {
                Toast.makeText(getBaseContext(), "There was an error sending payment to " + who, Toast.LENGTH_LONG).show();
            }
        }
    }
}
