package relish.permoveo.com.relish.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.digits.sdk.android.DigitsAuthButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

/**
 * Created by byfieldj on 8/25/15.
 */
public class SMSVerificationActivity extends  RelishActivity{

    @Bind(R.id.auth_button)
    DigitsAuthButton authButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_validation);
        ButterKnife.bind(this);
    }
}
