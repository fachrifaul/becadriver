package id.web.go_cak.drivergocak.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.Utils;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private UserSession sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();

        currentlyProcessingLocation = false;
        sessionManager = new UserSession(this);
        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
            currentlyProcessingLocation = false;
        }

        /*if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        } else {
            currentlyProcessingLocation = false;
        }*/

        Log.e(TAG, "hasil berada di Oncreate currentlyProcessingLocation " + currentlyProcessingLocation);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            startTracking();
            Log.e(TAG, "hasil berada di onStartCommand Tanpa cekFalse currentlyProcessingLocation " + currentlyProcessingLocation);
        }
        /*else {

            if(cekFalse==40) {
                currentlyProcessingLocation = false;
                cekFalse = 0;
            }

            cekFalse = (cekFalse+1);

            Log.e(TAG, "hasil berada di onStartCommand cekFalse "+cekFalse+" currentlyProcessingLocation "+currentlyProcessingLocation);

        }*/

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            currentlyProcessingLocation = true;
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            currentlyProcessingLocation = false;
            Log.e(TAG, "startTracking " + currentlyProcessingLocation);
        }
        Log.e(TAG, "startTracking " + currentlyProcessingLocation);
    }

    protected void sendLocationDataToWebsite(Location location) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        float totalDistanceInMeters = sharedPreferences.getFloat("totalDistanceInMeters", 0f);
        boolean firstTimeGettingPosition = sharedPreferences.getBoolean("firstTimeGettingPosition", true);

        if (firstTimeGettingPosition) {
            editor.putBoolean("firstTimeGettingPosition", false);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(sharedPreferences.getFloat("previousLatitude", 0f));
            previousLocation.setLongitude(sharedPreferences.getFloat("previousLongitude", 0f));

            float distance = location.distanceTo(previousLocation);
            totalDistanceInMeters += distance;
            editor.putFloat("totalDistanceInMeters", totalDistanceInMeters);
        }

        editor.putFloat("previousLatitude", (float) location.getLatitude());
        editor.putFloat("previousLongitude", (float) location.getLongitude());
        editor.apply();

        Log.e(TAG, "Result First " + currentlyProcessingLocation);

        Double speedInMilesPerHour = location.getSpeed() * 2.2369;
        String Alamat = Utils.getCompleteAddressString(this, location.getLatitude(), location.getLongitude());
        String log = "sessionManager " + sessionManager.getUsername() +
                " Update " + Alamat + " Lat " + Double.toString(location.getLatitude())
                + " Long " + Double.toString(location.getLongitude());
        Log.wtf(TAG, "sendLocationDataToWebsite: " + log);

        new ServiceSendLocation(this).fetchService(sessionManager.getIdUser(), String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()), String.valueOf(speedInMilesPerHour.intValue()),
                new ServiceSendLocation.CallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.e("ServiceSendLocation", message);
                        currentlyProcessingLocation = false;
                        stopSelf();
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e("ServiceSendLocation", message);
                    }
                }
        );

        Log.e(TAG, "Result Last " + currentlyProcessingLocation);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.e(TAG, "hasil position: " + location.getLatitude() + ", " + location.getLongitude() +
                    " accuracy: " + location.getAccuracy());

            if (sessionManager.isUserLoggedIn()) {
                //if (location.getAccuracy() < 500.0f) {
                currentlyProcessingLocation = true;
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
                Log.wtf(TAG, "onLocationChanged: " + "sessionManager " + sessionManager.getUsername() + " Update location ");
                //}
            }
        } else {
            currentlyProcessingLocation = false;
        }
        Log.e(TAG, "hasil berada di onLocationChanged currentlyProcessingLocation " + currentlyProcessingLocation);
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);

        Log.e(TAG, "onConnected " + currentlyProcessingLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + currentlyProcessingLocation);
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended " + currentlyProcessingLocation);
    }
}
