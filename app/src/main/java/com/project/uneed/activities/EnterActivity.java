package com.project.uneed.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.project.uneed.R;
import com.project.uneed.model.User;
import com.project.uneed.util.ConnectionDetector;
import com.project.uneed.util.SessionUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class EnterActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private String mLoginService;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private LinearLayout buttonsContent;
    private LinearLayout progressContent;
    private ProgressBar mProgressEnter;

    private User user;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        SessionUtil.printLog("GoogleApi initialize!");

        mProgressEnter = (ProgressBar) findViewById(R.id.progress_enter);

        callbackManager = CallbackManager.Factory.create();

        Button loginButton = (Button) findViewById(R.id.login_button);
        LoginManager.getInstance().registerCallback(callbackManager, callback);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrDismissLoading();
                mLoginService = "FACEBOOK";
                mProgressEnter.setVisibility(View.INVISIBLE);
                if (new ConnectionDetector(EnterActivity.this).isConnectingToInternet()) {
                    LoginManager.getInstance().logInWithReadPermissions(EnterActivity.this, Arrays.asList("public_profile", "user_friends"));
                } else {
                    Toast.makeText(EnterActivity.this, "Não há conexão com internet.", Toast.LENGTH_LONG).show();
                }
            }
        });

        SignInButton loginGoogle = (SignInButton) findViewById(R.id.sign_in_button);
        loginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // User clicked the sign-in button, so begin the sign-in process and automatically
                // attempt to resolve any errors that occur.
                showOrDismissLoading();
                mProgressEnter.setVisibility(View.VISIBLE);
                mShouldResolve = true;
                mLoginService = "GOOGLE";
                mGoogleApiClient.connect();

            }
        });

        buttonsContent = (LinearLayout) findViewById(R.id.buttons_content);
        progressContent = (LinearLayout) findViewById(R.id.progress_content);

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
                SessionUtil.printLog("Access token changed! oldToken : " + oldToken + " newToken : " + newToken);
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                SessionUtil.printLog("Profile changed! oldToken : " + oldProfile + " newToken : " + newProfile);
                saveProfile();
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }


    /**
     * Método para armazenar o Profile do facebook
     */
    public void saveProfile() {
        user = new User();
        user.setLoginService(mLoginService);

        if (mLoginService.equals("FACEBOOK")) {
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            Profile profile = Profile.getCurrentProfile();


            try {
                user.setPhoto(new DownloadImageProfile().execute(new URL(profile.getProfilePictureUri(90, 90).toString())).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                user.setFullName(profile.getFirstName() + " " + profile.getLastName());
                user.setAccessToken(accessToken.getToken());
                user.setApplicationId(accessToken.getApplicationId());
                user.setUserId(accessToken.getUserId());

                SessionUtil.currentUser = user;

                SessionUtil.printLog("Current facebook user loaded !");
            } catch (Exception e) {
                SessionUtil.printLog("Error saving facebook info. Profile = " + profile + " AND tokern = " + accessToken);
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        } else if (mLoginService.equals("GOOGLE")) {
            Person profile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

            try {
                user.setPhoto(new DownloadImageProfile().execute(new URL(profile.getImage().getUrl())).get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                user.setFullName(profile.getDisplayName());
                user.setUserId(profile.getId());
                user.setAccountName(Plus.AccountApi.getAccountName(mGoogleApiClient));

                SessionUtil.printLog("Current google user loaded !");

                SessionUtil.currentUser = user;
            } catch (Exception e) {
                SessionUtil.printLog("Error loading google user. Details: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }

        SharedPreferences mSharedPref = EnterActivity.this.getSharedPreferences(getString(R.string.accesstoken), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPref.edit();

        editor.putString(getString(R.string.service), user.getLoginService());
        editor.putString(getString(R.string.fullname), user.getFullName());
        editor.putString(getString(R.string.accesstoken), user.getAccessToken());
        editor.putString(getString(R.string.aplication_id), user.getApplicationId());
        editor.putString(getString(R.string.user_id), user.getUserId());
        editor.putString(getString(R.string.account_name), user.getAccountName());

        editor.commit();

        SessionUtil.printLog("Initializing MainActivity!");

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(EnterActivity.this, MainActivity.class));

                EnterActivity.this.finish();
                SessionUtil.printLog("Enter Activity finished!");
            }
        });
    }

    /**
     * Facebook Login Callback
     */
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();

            mLoginService = "FACEBOOK";

            SessionUtil.printLog("Facebook logged! Token and profile loaded! status: profile = " + profile + "\n token = " + accessToken);
            SessionUtil.printLog("Saving infos");

            if (profile != null) {
                saveProfile();
            }
        }

        @Override
        public void onCancel() {
            showOrDismissLoading();
            Toast.makeText(EnterActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(FacebookException e) {
            showOrDismissLoading();
            Toast.makeText(EnterActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    };

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        FacebookSdk.sdkInitialize(this);
        SessionUtil.printLog("FacebookSDK initialize!");
        mGoogleApiClient.connect();
        SessionUtil.printLog("GoogleAPI initialize!");
    }

    /**
     *
     */
    public void showOrDismissLoading() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (buttonsContent.getVisibility() == View.VISIBLE) {
                    buttonsContent.setVisibility(View.GONE);
                    progressContent.setVisibility(View.VISIBLE);

                } else {
                    progressContent.setVisibility(View.GONE);
                    buttonsContent.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    /**
     * Google integration methods
     */


    @Override
    public void onConnected(Bundle bundle) {
        //onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        SessionUtil.printLog("onConnected:" + bundle);
        mShouldResolve = false;

        Person profile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        if (profile != null) {
            saveProfile();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        SessionUtil.printLog("onConnectionFailed:" + connectionResult);

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    SessionUtil.printLog("Could not resolve ConnectionResult." + e.getLocalizedMessage());
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     *
     */
    private class DownloadImageProfile extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... params) {

            try {
                SessionUtil.printLog("Facebook User profile image downloading!");
                if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet()) {
                    URL img_value = params[0];
                    return BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                } else {
                    SessionUtil.printLog("No Internet conection detected!");
                    return BitmapFactory.decodeFile(getResources().getResourceEntryName(R.drawable.photo));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
