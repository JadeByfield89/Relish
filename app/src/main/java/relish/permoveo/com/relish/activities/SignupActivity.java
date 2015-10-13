package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
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

    @Bind(R.id.et_fullname)
    EditText fullName;

    private String facebookName;
    private String facebookEmail;

    private CallbackManager callbackManager;

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
        fullName.setTypeface(TypefaceUtil.PROXIMA_NOVA);

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
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupWithFacebook();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        updateStatusBar(getResources().getColor(R.color.main_color_dark));

        callbackManager = CallbackManager.Factory.create(); // declare it globally "CallbackManager callbackManager "

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                // TODO Auto-generated method stub
                Log.d("SignupActivity", "On Success");

                final AccessToken token = result.getAccessToken();
                SharedPrefsUtil.get.saveFacebookAccessToken(token);


                GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                        facebookEmail = user.optString("email");
                        facebookName = user.optString("name");

                        Log.d("SignupActivity", "Facebook Name -> " + facebookName);
                        Log.d("SignupActivity", "Facebook Email -> " + facebookEmail);

                        loginWithFacebook(token);


                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d("SignupActivity", "On Error");
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Log.d("SignupActivity", "On Cancel");
            }
        });
    }


    private void signupWithFacebook() {
        if (SharedPrefsUtil.get.getFacebookAccessToken().isEmpty()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile, user_friends, email"));
            //loginWithFacebook();
        } else {

        }
    }

    private void loginWithFacebook(final AccessToken token) {

        ParseFacebookUtils.logInInBackground(token, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {

                if (parseUser != null) {

                    Log.d("SignupActivity", "Successfully logged in via Facebook");
                    parseUser.put("email", facebookEmail);
                    parseUser.put("fullName", facebookName);
                    //parseUser.saveInBackground();

                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                    installation.put("userId", parseUser.getObjectId());
                    installation.saveInBackground();

                    parseUser.signUpInBackground();

                    Intent intent = new Intent(SignupActivity.this, SMSVerificationActivity.class);
                    startActivity(intent);
                }

            }
        });

    }


    private void signup() {
        if (validate()) {
            showLoader(getString(R.string.signing_up_loader_text));

            ParseUser user = new ParseUser();
            final String username = usernameEt.getText().toString().toLowerCase();
            String email = emailEt.getText().toString();
            final String password = passwordEt.getText().toString();

            final String fullname = fullName.getText().toString();

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

                                //Save user's full name
                                parseUser.put("fullName", fullname);
                                parseUser.saveInBackground();

                                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                installation.put("userId", parseUser.getObjectId());
                                installation.saveInBackground();
                                if (e == null) {
                                    startActivity(new Intent(SignupActivity.this, SMSVerificationActivity.class));
                                    finish();
                                } else {
                                    Snackbar.make(passwordEt, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                    //Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        hideLoader();
                        Snackbar.make(passwordEt, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

                        //Toast.makeText(SignupActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean validate() {
        String fullname = fullName.getText().toString();
        String[] segments = fullname.split(" ");

        if (TextUtils.isEmpty(fullName.getText())) {
            Snackbar.make(passwordEt, "Please enter your full name", Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, "Please enter your full name", Toast.LENGTH_LONG).show();
            return false;
        }
        if (segments.length < 2) {
            Snackbar.make(passwordEt, "Please enter your full name", Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, "Please enter your full name", Toast.LENGTH_LONG).show();
            return false;
        }

        if (segments.length > 2) {
            Snackbar.make(passwordEt, "Please enter your first and last name only", Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, "Please enter your first and last name only", Toast.LENGTH_LONG).show();
            return false;
        }


        if (TextUtils.isEmpty(usernameEt.getText())) {
            Snackbar.make(passwordEt, getString(R.string.error_username_empty), Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, getString(R.string.error_username_empty), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(emailEt.getText()) || !Patterns.EMAIL_ADDRESS.matcher(emailEt.getText()).matches()) {
            Snackbar.make(passwordEt, getString(R.string.error_email), Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, getString(R.string.error_email), Toast.LENGTH_LONG).show();
            return false;
        } else if (TextUtils.isEmpty(passwordEt.getText()) || passwordEt.getText().length() < 3) {
            Snackbar.make(passwordEt, getString(R.string.error_password), Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, getString(R.string.error_password), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LoginActivity.LOGIN_REQUEST && resultCode == RESULT_OK) {
            finish();
        }
    }
}
