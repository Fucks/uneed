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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.project.uneed.R;
import com.project.uneed.model.User;
import com.project.uneed.util.ConnectionDetector;
import com.project.uneed.util.SessionUtil;

import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ExecutionException;

public class SplashScreenActivity extends ActionBarActivity {

    private final int SPLASH_TIMEOUT = 3000;

    /**
     *
     */
    public void initializeUser() {
        if (!FacebookSdk.isInitialized() && SessionUtil.currentUser == null) {

            FacebookSdk.sdkInitialize(this);
            SessionUtil.printLog("Facebook SDK Initialized!");
            CallbackManager.Factory.create();

            updateWithToken(AccessToken.getCurrentAccessToken());
        } else {
            SessionUtil.printLog("Facebook SDK Already initialized!");
            Intent mIntent = new Intent(SplashScreenActivity.this, MainActivity.class);

            startActivity(mIntent);
            // make sure splash screen activity is gone
            SplashScreenActivity.this.finish();
        }
    }

    /**
     * @param currentAccessToken
     */
    private void updateWithToken(AccessToken currentAccessToken) {

        SessionUtil.printLog("Access Token update method! Status : token = " + currentAccessToken);

        SessionUtil.printLog("Getting shared access token");
        SharedPreferences mSharedPref = SplashScreenActivity.this.getSharedPreferences(getString(R.string.accesstoken), Context.MODE_PRIVATE);

        String mSharedAccessToken = mSharedPref.getString(getString(R.string.accesstoken), "");
        String mSharedApplicationId = mSharedPref.getString(getString(R.string.aplication_id), "");
        String mSharedUserId = mSharedPref.getString(getString(R.string.user_id), "");

        if (currentAccessToken != null || !mSharedAccessToken.isEmpty()) {

            SessionUtil.printLog("AccessToken is null, getting token by shared preferences");

            AccessToken mToken = new AccessToken(mSharedAccessToken, mSharedApplicationId, mSharedUserId, null, null, null, null, null);
            AccessToken.setCurrentAccessToken(mToken);

            Intent mIntent = new Intent(SplashScreenActivity.this, MainActivity.class);

            Profile.fetchProfileForCurrentAccessToken();
            Profile profile = Profile.getCurrentProfile();

            SessionUtil.printLog("Facebook profile status : " + profile);

            User user = new User();

            try {
                user.setPhoto(new DownloadImageProfile().execute(profile).get());
            } catch (ConcurrentModificationException | ExecutionException | InterruptedException e) {
            }

            try {

                user.setFirstName(profile.getFirstName());
                user.setLastName(profile.getLastName());

                SessionUtil.currentUser = user;

                startActivity(mIntent);

                // make sure splash screen activity is gone
                SplashScreenActivity.this.finish();
            } catch (Exception e) {
                SessionUtil.printLog("Error loading user");
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            SessionUtil.printLog("Not have access token, go to EnterActivity");

            Intent i = new Intent(SplashScreenActivity.this, EnterActivity.class);
            startActivity(i);

            // make sure splash screen activity is gone
            SplashScreenActivity.this.finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // our layout xml
        setContentView(R.layout.activity_spalsh_screen);

        SessionUtil.printLog("SplashScreen initialized!");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initializeUser();
            }
        }, SPLASH_TIMEOUT);
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
