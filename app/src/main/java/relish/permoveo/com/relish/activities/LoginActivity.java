package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;

public class LoginActivity extends RelishActivity {

    public static final int LOGIN_REQUEST = 321;

    @Bind(R.id.et_username)
    EditText usernameEt;

    @Bind(R.id.et_password)
    EditText passwordEt;

    @Bind(R.id.btn_signin)
    Button signin;

    @Bind(R.id.signup_label)
    TextView signupLabel;

    @Bind(R.id.dont_have_account_label)
    TextView dontHaveAccountLabel;

    @Bind(R.id.btn_signup)
    LinearLayout signup;

    @Bind(R.id.relish_label)
    TextView relishLabel;

    @Bind(R.id.slogan_label)
    TextView sloganLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sloganLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        relishLabel.setTypeface(TypefaceUtil.BRANNBOLL_BOLD);
        relishLabel.setIncludeFontPadding(false);
        usernameEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        passwordEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        signin.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        signupLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        dontHaveAccountLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        signin.setTransformationMethod(null);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    auth();
                }
                return false;
            }
        });

        updateStatusBar(getResources().getColor(R.color.main_color_dark));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void auth() {
        if (validate()) {
            showLoader(getString(R.string.logging_in_loader_text));

            final String username = usernameEt.getText().toString();
            final String password = passwordEt.getText().toString();
            Log.d("Username: " , username);
            Log.d("Password: ", password);

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    hideLoader();
                    if (e == null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Log.d("Parse Error Code: ", ""+e.getCode());
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage() + e.getCode(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(usernameEt.getText())) {
            Toast.makeText(this, getString(R.string.error_username_empty), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(passwordEt.getText()) || passwordEt.getText().length() < 3) {
            Toast.makeText(this, getString(R.string.error_password), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
