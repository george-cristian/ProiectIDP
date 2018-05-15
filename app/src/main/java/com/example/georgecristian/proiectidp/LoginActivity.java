package com.example.georgecristian.proiectidp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String EMAIL = "email";

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager callbackManager;

    private String facebookUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Set the dimensions of the sign-in button.
        SignInButton googleSignInButton = findViewById(R.id.google_sign_in_button);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        googleSignInButton.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginButton facebookSignInButton = findViewById(R.id.facebook_sign_in_button);
        facebookSignInButton.setReadPermissions(Arrays.asList("public_profile" , "email"));
        //facebookSignInButton.setReadPermissions(Arrays.asList(EMAIL));

        // Callback registration
        facebookSignInButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {

                if (Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                            this.stopTracking();
                            Profile.setCurrentProfile(currentProfile);
                            insertUserInDatabase();
                        }
                    };
                } else {
                    insertUserInDatabase();
                }

                // App code
                changeToMainActivity();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        // Update the UI if the user is already logged in

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signInGoogle();
                break;
        }
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

        changeToMainActivity();
    }

    private void changeToMainActivity() {
        Intent signInToMain = new Intent("com.example.georgecristian.proiectidp.MainActivity");

        signInToMain.putExtra(BackendService.PARAM_USER_ID, facebookUserID);

        startActivity(signInToMain);
    }

    private void insertUserInDatabase() {

        Intent insertUserIntent = new Intent(LoginActivity.this, BackendService.class);

        Profile facebookProfile = Profile.getCurrentProfile();
        facebookUserID = facebookProfile.getId();
        Log.d("BackendService", "USER ID IS - login activity - insert user in db: " + facebookUserID);
        insertUserIntent.putExtra(BackendService.PARAM_OPERATION_TYPE, BackendService.OPERATION_INSERT_USER);
        insertUserIntent.putExtra(BackendService.PARAM_USER_ID, facebookProfile.getId());
        insertUserIntent.putExtra(BackendService.PARAM_USER_FIRST_NAME, facebookProfile.getFirstName());
        insertUserIntent.putExtra(BackendService.PARAM_USER_LAST_NAME, facebookProfile.getLastName());

        startService(insertUserIntent);

    }
}
