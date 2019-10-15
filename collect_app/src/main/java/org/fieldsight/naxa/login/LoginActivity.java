package org.fieldsight.naxa.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.SignInButton;

import org.fieldsight.collect.android.BuildConfig;
import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.common.DialogFactory;
import org.fieldsight.naxa.common.FieldSightUserSession;
import org.fieldsight.naxa.common.SettingsActivity;
import org.fieldsight.naxa.migrate.MigrateFieldSightActivity;
import org.fieldsight.naxa.migrate.MigrationHelper;
import org.fieldsight.naxa.network.APIEndpoint;
import org.fieldsight.naxa.v3.ProjectListActivityV3;

import timber.log.Timber;

import static org.odk.collect.android.application.Collect.allowClick;

//import org.bcss.naxa.common.Login;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseLoginActivity implements LoginView {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private LoginPresenter loginPresenter;
    private Button mEmailSignInButton;
    private RelativeLayout rootLayout;
    private ImageButton btnChangeUrl;
    private SignInButton btnGmailLogin;
    private boolean isFromGooleSignin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        mPasswordView = findViewById(R.id.password);
        rootLayout = findViewById(R.id.root_layout_activity_login);

        btnChangeUrl = findViewById(R.id.btn_change_server_url);
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allowClick(getClass().getName())) {
                    hideKeyboardInActivity(LoginActivity.this);
                    attemptLogin();
                }
            }
        });

        btnGmailLogin = findViewById(R.id.btn_gmail_login);
        btnGmailLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isFromGooleSignin = true;
                showProgress(true);
                gmailSignIn();
                btnGmailLogin.setEnabled(false);
            }
        });

        if (!BuildConfig.BUILD_TYPE.equals("release")) {
            btnChangeUrl.setVisibility(View.VISIBLE);
            btnChangeUrl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (allowClick(getClass().getName())) {
                        hideKeyboardInActivity(LoginActivity.this);
                        startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
                    }
                }
            });
        }
        mLoginFormView = findViewById(R.id.logo);
        mProgressView = findViewById(R.id.login_progress);


        findViewById(R.id.tv_forgot_pwd).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allowClick(getClass().getName())) {
                    String url = FieldSightUserSession.getServerUrl(LoginActivity.this) + APIEndpoint.PASSWORD_RESET;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
            }
        });

        loginPresenter = new LoginPresenterImpl(this);


    }

    @Override
    public void gmailLoginSuccess(String googleAccessToken, String username) {
        loginPresenter.googleOauthCredentials(googleAccessToken, username);
        Timber.d("gmailLoginSuccess: Access tokenId %s", googleAccessToken);

    }

    @Override
    public void gmailLoginFailed(String errorMsg) {
        showError(errorMsg);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();


        loginPresenter.validateCredentials(email, password);

    }

    @Override
    public void showProgress(final boolean show) {

        runOnUiThread(() -> {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            mEmailSignInButton.setEnabled(!show);
        });

    }

    @Override
    public void showPasswordError(int resourceId) {
        mPasswordView.setError(getString(resourceId));
        mPasswordView.requestFocus();

    }

    @Override
    public void showUsernameError(int resourceId) {
        mEmailView.setError(getString(resourceId));
        mEmailView.requestFocus();
    }

    @Override
    public void successAction() {

        boolean hasOldAccount = new MigrationHelper(mEmailView.getText().toString()).hasOldAccount();

        if (hasOldAccount) {
            MigrateFieldSightActivity.start(this, mEmailView.getText().toString());
        } else {
            startActivity(new Intent(this, ProjectListActivityV3.class));
        }
        Toast.makeText(this, "Logged In!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void showError(String msg) {
        showProgress(false);
        showErrorDialog(msg);
    }

    private void showErrorDialog(String msg) {
        if(isFinishing()){
            return;
        }
        Dialog dialog = DialogFactory.createActionDialog(LoginActivity.this, "Login Failed", msg)
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        retryLogin();
                    }
                })
                .setNegativeButton(R.string.dialog_action_dismiss, null)
                .create();
       dialog.show();
    }

    /**
     * Only works from an activity, using getActivity() does not work
     *
     * @param activity
     */
    public void hideKeyboardInActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window TOKEN from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window TOKEN from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        }
    }

    private void retryLogin() {
        if (isFromGooleSignin) {
            gmailSignIn();
        } else {
            attemptLogin();
        }
    }
}

