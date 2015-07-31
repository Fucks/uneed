package com.project.uneed.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
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
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;

public class SplashScreenActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final int SPLASH_TIMEOUT = 3000;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private User mUser;

    /**
     *
     */
    public void initializeUser() {
        SharedPreferences mSharedPref = SplashScreenActivity.this.getSharedPreferences(getString(R.string.accesstoken), Context.MODE_PRIVATE);
        mUser = new User();

        mUser.setLoginService(mSharedPref.getString(getString(R.string.service), ""));
        mUser.setUserId(mSharedPref.getString(getString(R.string.user_id), ""));
        mUser.setApplicationId(mSharedPref.getString(getString(R.string.aplication_id), ""));
        mUser.setAccessToken(mSharedPref.getString(getString(R.string.accesstoken), ""));
        mUser.setFullName(mSharedPref.getString(getString(R.string.fullname), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // our layout xml
        setContentView(R.layout.activity_spalsh_screen);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient = new GoogleApiClient.Builder(SplashScreenActivity.this)
                        .addConnectionCallbacks(SplashScreenActivity.this)
                        .addOnConnectionFailedListener(SplashScreenActivity.this)
                        .addApi(Plus.API)
                        .addScope(new Scope(Scopes.PROFILE))
                        .build();

                mGoogleApiClient.connect();
                SessionUtil.printLog("GoogleAPI initialize!");

                FacebookSdk.sdkInitialize(SplashScreenActivity.this);
                CallbackManager.Factory.create();
                SessionUtil.printLog("FacebookSDK initialize!");

                SessionUtil.printLog("SplashScreen initialized!");

                initilizeCallBacks();

                initializeUser();

                if (mUser.getLoginService().isEmpty()) {
                    SessionUtil.printLog("Not have access token, go to EnterActivity");

                    Intent i = new Intent(SplashScreenActivity.this, EnterActivity.class);
                    startActivity(i);

                    // make sure splash screen activity is gone
                    SplashScreenActivity.this.finish();
                } else if(mUser.getLoginService().equals("FACEBOOK")){
                    AccessToken accessToken = new AccessToken(mUser.getAccessToken(), mUser.getApplicationId(), mUser.getUserId(), null, null, null, null,  null);
                    AccessToken.setCurrentAccessToken(accessToken);
                    Profile.fetchProfileForCurrentAccessToken();
                }
            }
        }).start();
    }

    /**
     *
     */
    public void initilizeCallBacks() {
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
                Profile profile = Profile.getCurrentProfile();

                SessionUtil.printLog("Facebook profile status : " + profile);

                try {
                    mUser.setPhoto(new DownloadImageProfile().execute(new URL(profile.getProfilePictureUri(90,90).toString())).get());
                } catch (ConcurrentModificationException | ExecutionException | InterruptedException e) {
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                Intent mIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
                SessionUtil.currentUser = mUser;

                startActivity(mIntent);

                // make sure splash screen activity is gone
                SplashScreenActivity.this.finish();
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spalsh_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Google Callbacks
     */

    @Override
    public void onConnected(Bundle bundle) {
        Person profile = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        try {
            mUser.setPhoto(new DownloadImageProfile().execute(new URL(profile.getImage().getUrl())).get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        Intent mIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
        SessionUtil.currentUser = mUser;

        startActivity(mIntent);

        // make sure splash screen activity is gone
        SplashScreenActivity.this.finish();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
