package relish.permoveo.com.relish.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import butterknife.Bind;
import butterknife.ButterKnife;
import relish.permoveo.com.relish.R;

public class LoginActivity extends RelishActivity {

    public static final int LOGIN_REQUEST = 321;

    @Bind(R.id.et_username)
    EditText usernameEt;

    @Bind(R.id.et_password)
    EditText passwordEt;

    @Bind(R.id.btn_login)
    Button login;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.button_login));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth();
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
    }

    private void auth() {
        if (validate()) {
            showLoader(getString(R.string.logging_in_loader_text));

            final String username = usernameEt.getText().toString();
            final String password = passwordEt.getText().toString();

            ParseUser.logInInBackground(username, password, new LogInCallback() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    hideLoader();
                    if (e == null) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
