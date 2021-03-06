package relish.permoveo.com.relish.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;
import relish.permoveo.com.relish.gps.GPSTracker;
import relish.permoveo.com.relish.util.SharedPrefsUtil;
import relish.permoveo.com.relish.util.TypefaceUtil;

public class LoginActivity extends RelishActivity {

    public static final int LOGIN_REQUEST = 321;
    private static final int LOCATION_PERMISSION_REQUEST = 111;

    @Bind(R.id.et_email)
    EditText emailEt;

    @Bind(R.id.et_password)
    EditText passwordEt;

    @Bind(R.id.btn_signin)
    Button signin;

    @Bind(R.id.btn_facebook)
    Button facebook;

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

    private CallbackManager callbackManager;
    private String facebookEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sloganLabel.setTypeface(TypefaceUtil.PROXIMA_NOVA);
        relishLabel.setTypeface(TypefaceUtil.BRANNBOLL_BOLD);
        relishLabel.setIncludeFontPadding(false);
        emailEt.setTypeface(TypefaceUtil.PROXIMA_NOVA);
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

        facebook.setTransformationMethod(null);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginWithFacebook();
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


        callbackManager = CallbackManager.Factory.create(); // declare it globally "CallbackManager callbackManager "

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult result) {
                // TODO Auto-generated method stub
                Log.d("SignupActivity", "On Success");

                showLoader(getString(R.string.logging_in_loader_text));

                final AccessToken token = result.getAccessToken();
                SharedPrefsUtil.get.saveFacebookAccessToken(token);

                GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                        facebookEmail = user.optString("email");
                        Log.d("LoginActivity", "Facebook Email -> " + facebookEmail);

                        ParseFacebookUtils.logInInBackground(token, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                hideLoader();

                                if (e == null) {
                                    Log.d("LoginActivity", "Successfully logged in via Facebook");

                                    Location location = GPSTracker.get.getLocation();
                                    if (location != null && location.getLatitude() != 0.0d && location.getLongitude() != 0.0d) {
                                        ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                                        ParseUser.getCurrentUser().saveInBackground();
                                    }

                                    ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                    installation.put("userId", parseUser.getObjectId());
                                    installation.saveInBackground();

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    setResult(RESULT_OK);
                                    finish();
                                } else {
                                    Log.d("Parse Error Code: ", "" + e.getCode());
                                    Snackbar.make(passwordEt, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                // TODO Auto-generated method stub
                Log.d("LoginActivity", "On Error");
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
                Log.d("LoginActivity", "On Cancel");
            }
        });

        updateStatusBar(getResources().getColor(R.color.main_color_dark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);
                return;
            }
        } else {
            GPSTracker.get.init(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loginWithFacebook() {
        if (SharedPrefsUtil.get.getFacebookAccessToken().isEmpty()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("public_profile, user_friends, email"));
            //loginWithFacebook();
        } else {

        }
    }

    private void auth() {
        if (validate()) {
            showLoader(getString(R.string.logging_in_loader_text));

            final String email = emailEt.getText().toString();
            final String password = passwordEt.getText().toString();
            Log.d("Username: ", email);
            Log.d("Password: ", password);

            // First, execute a Parse query for a user with this email
            ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
            userQuery.whereEqualTo("email", email);
            userQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> list, ParseException e) {

                    // Once a user with this email is found, get their username and try to log them in
                    if (e == null) {
                        if (!list.isEmpty() && list.size() == 1) {
                            ParseUser user = list.get(0);
                            String userName = user.getUsername();
                            ParseUser.logInInBackground(userName, password, new LogInCallback() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    hideLoader();
                                    if (e == null) {
                                        Location location = GPSTracker.get.getLocation();
                                        if (location != null && location.getLatitude() != 0.0d && location.getLongitude() != 0.0d) {
                                            ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
                                            ParseUser.getCurrentUser().saveInBackground();
                                        }
                                        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                                        installation.put("userId", parseUser.getObjectId());
                                        installation.saveInBackground();

                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        Log.d("Parse Error Code: ", "" + e.getCode());
                                        Snackbar.make(passwordEt, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

                                        // Toast.makeText(LoginActivity.this, e.getLocalizedMessage() + e.getCode(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            hideLoader();
                            Snackbar.make(passwordEt, getString(R.string.user_not_found), Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        hideLoader();
                        Snackbar.make(passwordEt, e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                    }
                }
            });


        }
    }

    private boolean validate() {
        if (TextUtils.isEmpty(emailEt.getText())) {
            Snackbar.make(passwordEt, getString(R.string.error_email_empty), Snackbar.LENGTH_LONG).show();

            //Toast.makeText(this, getString(R.string.error_email_empty), Toast.LENGTH_LONG).show();
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    GPSTracker.get.init(this);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                break;
        }
    }
}
