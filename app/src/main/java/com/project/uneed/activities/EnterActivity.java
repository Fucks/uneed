package com.project.uneed.activities;

import android.content.Context;
import android.content.Intent;
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
import com.project.uneed.R;
import com.project.uneed.model.User;
import com.project.uneed.util.ConnectionDetector;
import com.project.uneed.util.SessionUtil;

import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class EnterActivity extends ActionBarActivity {

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private LinearLayout buttonsContent;
    private LinearLayout progressContent;

    private User user;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        FacebookSdk.sdkInitialize(this);
        SessionUtil.printLog("FacebookSDK initialize!");

        callbackManager = CallbackManager.Factory.create();

        Button loginButton = (Button) findViewById(R.id.login_button);
        LoginManager.getInstance().registerCallback(callbackManager, callback);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOrDismissLoading();
                if (new ConnectionDetector(EnterActivity.this).isConnectingToInternet()) {
                    LoginManager.getInstance().logInWithReadPermissions(EnterActivity.this, Arrays.asList("public_profile", "user_friends"));
                } else {
                    Toast.makeText(EnterActivity.this, "Não há conexão com internet.", Toast.LENGTH_LONG).show();
                }
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
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Profile profile = Profile.getCurrentProfile();

        user = new User();
        try {
            user.setPhoto(new DownloadImageProfile().execute(profile).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        try {

            SharedPreferences mSharedPref = EnterActivity.this.getSharedPreferences(getString(R.string.accesstoken), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = mSharedPref.edit();

            editor.putString(getString(R.string.accesstoken), accessToken.getToken());
            editor.putString(getString(R.string.aplication_id), accessToken.getApplicationId());
            editor.putString(getString(R.string.user_id), accessToken.getUserId());

            editor.commit();

            user.setFirstName(profile.getFirstName());
            user.setLastName(profile.getLastName());

            SessionUtil.currentUser = user;
            SessionUtil.printLog("Current user loaded !");

            SessionUtil.printLog("Initializing MainActivity!");

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(EnterActivity.this, MainActivity.class));

                    EnterActivity.this.finish();
                    SessionUtil.printLog("Enter Activity finished!");
                }
            });
        } catch (Exception e) {
            SessionUtil.printLog("Error saving facebook info. Profile = " + profile + " AND tokern = " + accessToken);
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Facebook Login Callback
     */
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();

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
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        super.onPause();
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
     *
     */
    private class DownloadImageProfile extends AsyncTask<Profile, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Profile... params) {

            try {
                SessionUtil.printLog("Facebook User profile image downloading!");
                if (new ConnectionDetector(getApplicationContext()).isConnectingToInternet()) {
                    URL img_value = new URL(params[0].getProfilePictureUri(90, 90).toString());
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
