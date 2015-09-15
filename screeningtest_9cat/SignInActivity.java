package weinianlim.screeningtest_9cat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;
import java.util.List;

/**
 * The 9Cat Coding Assignment program implements an application that displays list of
 * all people currently logged in and sort them by distance, starting from the closest
 * one. This program uses mongodb as database platform and store all data in mongolab.
 *
 * @author William Lim
 * @version 1.0
 * @since 2015-08-21
 */

public class SignInActivity extends ActionBarActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ProfileTracker profileTracker;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    Double userLat = 0.0;
    Double userLng = 0.0;
    String userName;
    Bundle MABundle;
    String[] userInfoArray = new String[3];
    ImageButton mainButton;

    /**
     * This is the main method that creates the User Interface, initialize facebook sdk
     * and triggers the profile and accesstoken tracker.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_sign_in);
        userlocation();
        callbackManager = CallbackManager.Factory.create();

        // using profiletracker to track the personal information changes made by user on facebook
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Profile.setCurrentProfile(currentProfile);
                this.stopTracking();
            }
        };

        profileTracker.startTracking();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                AccessToken.setCurrentAccessToken(currentAccessToken);
            }
        };
        accessTokenTracker.startTracking();

        // Create the Facebook Login Button
        createLoginButton();
    }

    /**
     * This method stores the user's name,latitude and longitude in mongolab.
     *
     * @param array
     * @return Nothing
     */
    private void SaveDataMongoDB(String[] array) {

        SaveDataAsyncTask task = new SaveDataAsyncTask();
        task.execute(array);

    }

    /**
     * This method triggers the database storing command and startup
     * the Main activity class. Once this method completed, user is brought
     * to the Main activity class user interface.
     *
     *
     * @return Nothing
     */
    private void startMainActivity() {

        SaveDataMongoDB(userInfoArray);
        try {
            Thread.sleep(3000);                 //sleep 2 seconds
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        MABundle = new Bundle();
        MABundle.putString("Name", userName);
        MABundle.putString("Latitude", Double.toString(userLat));
        MABundle.putString("Longitude", Double.toString(userLng));
        Intent i = new Intent(this, MainActivity.class);
        i.putExtras(MABundle);
        startActivity(i);
    }

    /**
     * This method searches for the latitude and longitude location of the user's mobile device
     * according to the network provider' location.
     *
     * @return Nothing
     */
    private void userlocation() {

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                if (location != null) {

                    userLng = location.getLongitude();
                    userLat = location.getLatitude();

                    try {
                        Thread.sleep(2000);                 //sleep 2 seconds
                    } catch(InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }

                    userInfoArray[1] = Double.toString(userLng);
                    userInfoArray[2] = Double.toString(userLat);

                }

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        // or use LocationManager.GPS_PROVIDER
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

    }

    /**
     * This method creates the Facebook Login Button.
     *
     * @return Nothing
     */

    private void createLoginButton() {

        loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissionNeeds = Arrays.asList("user_location", "email", "user_birthday", "public_profile");
        loginButton.setReadPermissions(permissionNeeds);
        loginButton.registerCallback(callbackManager, fCallBack);

    }

    FacebookCallback<LoginResult> fCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            // App code
            accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();
            if (profile != null){
                //txtName = (TextView) findViewById(R.id.txtName);
                mainButton = (ImageButton) findViewById(R.id.imageButton);
                //txtName.setText("Welcome");
                userName = profile.getName();
                userInfoArray[0] = userName;

                mainButton.setClickable(true);
                mainButton.setVisibility(View.VISIBLE);

            }
        }

        @Override
        public void onCancel() {
            // App code
            Log.v("LoginActivity", "cancel");
        }

        // Check for Internet Connectivity
        @Override
        public void onError(FacebookException exception) {
            // App code
            //Log.v("LoginActivity", exception.getCause().toString());
            internetConfimationDialog();
        }
    };

    /**
     * This method display an alert dialog when internet connectivity is not available,
     * notifying user to connect their device to internet.
     *
     * @return Nothing
     */
    private void internetConfimationDialog() {

        Log.e("dialog", "pop-up");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Check Your Internet Connectivity");
        builder.setCancelable(true);
        builder.setNegativeButton("ok",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void main (View v){
        startMainActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        profileTracker.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        CharSequence text = "Press Home button to exit";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}