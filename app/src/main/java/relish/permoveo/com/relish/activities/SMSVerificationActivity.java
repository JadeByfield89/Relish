package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseUser;

import java.lang.reflect.Type;
import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;

/**
 * Created by byfieldj on 8/25/15.
 */
public class SMSVerificationActivity extends RelishActivity {


    @Bind(R.id.verify_phone_number)
    Button verifyPhoneNumber;

    @Bind(R.id.sms_sub_one)
    TextView smsSubText;


    private AuthCallback authCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_validation);
        ButterKnife.bind(this);

        smsSubText.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession digitsSession, String number) {

                sendPhoneNumberToParse(number);
                Toast.makeText(getBaseContext(), "Success -> " + digitsSession.getAuthToken(), Toast.LENGTH_SHORT).show();
                Log.d("SMSVerification", "Number -> " + number);
            }

            @Override
            public void failure(DigitsException e) {
                Toast.makeText(getBaseContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        };

        verifyPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Digits.authenticate(authCallback, R.style.Theme_MyTheme);
            }
        });



    }

    private void sendPhoneNumberToParse(final String number){
        try{
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.put("phoneNumber", number);
            currentUser.saveInBackground();

            //Start MainActivity ONLY once parse user account was created successfully
            // and phone number has been verified via Digits and saved to Parse

            Intent intent = new Intent(SMSVerificationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch(NullPointerException e){
            e.printStackTrace();
        }
    }
}
