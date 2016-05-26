package id.web.go_cak.drivergocak.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import id.web.go_cak.drivergocak.session.UserSession;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private UserSession sessionManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new UserSession(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (sessionManager.checkLogin()) {
//            sessionManager.checkLogin();
        } else {
            startTracking();
        }

        return START_NOT_STICKY;
    }

    private void startTracking() {
        Log.d(TAG, "startTracking");

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    protected void sendLocationDataToWebsite(Location location) {
//        String Alamat = Utils.getCompleteAddressString(this, location.getLatitude(), location.getLongitude());
//        String log = "sessionManager " + sessionManager.getUsername() +
//                " Update " + Alamat + " Lat " + Double.toString(location.getLatitude())
//                + " Long " + Double.toString(location.getLongitude());
//        Log.wtf(TAG, "sendLocationDataToWebsite: " + log);

        new ServiceSendLocation(this).fetchService(sessionManager.getIdUser(), String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                new ServiceSendLocation.CallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.e("ServiceSendLocation", ": " + message);
                        Toast.makeText(LocationService.this, "Lokasi Akurat", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e("ServiceSendLocation", "failed: " + message);
                    }
                }
        );
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
                stopLocationUpdates();
                sendLocationDataToWebsite(location);
                Log.wtf(TAG, "onLocationChanged: " + "sessionManager " + sessionManager.getUsername() + " Update location ");
                //}
            }
        }

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended ");
    }
}
