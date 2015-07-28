package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
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
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.TypefaceUtil;

public class SignupActivity extends RelishActivity {

    @Bind(R.id.et_username)
    EditText usernameEt;

    @Bind(R.id.et_email)
    EditText emailEt;

    @Bind(R.id.et_password)
    EditText passwordEt;

    @Bind(R.id.btn_facebook)
    Button facebook;

    @Bind(R.id.btn_signup)
    Button signup;

    @Bind(R.id.or_label)
    TextView orLabel;

    @Bind(R.id.relish_label)
    TextView relishLabel;

    @Bind(R.id.slogan_label)
    TextView sloganLabel;

    @Bind(R.id.already_have_account_label)
    TextView alreadyHaveAccountLabel;

    @Bind(R.id.signin_label)
    TextView signInLabel;

    @Bind(R.id.btn_login)
    LinearLayout login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        usernameEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        passwordEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        emailEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        orLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        sloganLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        relishLabel.setTypeface(TypefaceUtil.BRANNBOLL_BOLD);
        relishLabel.setIncludeFontPadding(false);
        alreadyHaveAccountLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        signInLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);

        passwordEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signup();
                    return true;
                }
                return false;
            }
        });

        signup.setTransformationMethod(null);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        facebook.setTransformationMethod(null);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        updateStatusBar(getResources().getColor(R.color.main_color_dark));
    }

    private void signup() {
        if (validate()) {
            showLoader(getString(R.string.signing_up_loader_text));

            ParseUser user = new ParseUser();
            final String username = usernameEt.getText().toString();
            String email = emailEt.getText().toString();
            final String password = passwordEt.getText().toString();

            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(final ParseException e) {
                    if (e == null) {
                        ParseUser.logInInBackground(username, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                hideLoader();
                                if (e == null) {
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        hideLoader();
                        Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(usernameEt.getText())) {
            Toast.makeText(this, getString(R.string.error_username_empty), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(emailEt.getText()) || !Patterns.EMAIL_ADDRESS.matcher(emailEt.getText()).matches()) {
            Toast.makeText(this, getString(R.string.error_email), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(passwordEt.getText()) || passwordEt.getText().length() < 3) {
            Toast.makeText(this, getString(R.string.error_password), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginActivity.LOGIN_REQUEST && resultCode == RESULT_OK) {
            finish();
        }
    }
}
