package id.web.go_cak.drivergocak.service;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.greenrobot.eventbus.EventBus;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.activity.MainActivity;
import id.web.go_cak.drivergocak.session.UserSession;

public class GPSTracker extends Service {
    private static final int NOTIFICATION_ID = 2123;

    private static final String TAG = "GPSTracker";
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

    private UserSession sessionManager;
    private GoogleApiClient googleApiClient;
    private boolean started = false;

    public GPSTracker(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sessionManager = new UserSession(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (started) {
            Log.d(TAG, "Service already started ");
            return START_STICKY;
        }
        if (sessionManager.getIdUser() == null)
            stopTracking();
        else {
            started = true;
            startLocationUpdate();
        }

        return START_STICKY;
    }

    public void stopTracking() {
        stopForeground(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationListener);
            }

        }
        stopSelf();
    }

    public void startLocationUpdate() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .build();
        googleApiClient.connect();
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "On connected google API");
            if (ContextCompat.checkSelfPermission(GPSTracker.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                showNotification();
                locationListener.onLocationChanged(lastLocation);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                locationRequest.setInterval(MIN_TIME_BW_UPDATES);
                locationRequest.setSmallestDisplacement(MIN_DISTANCE_CHANGE_FOR_UPDATES);

                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                        locationListener);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "On suspended google API");
        }
    };

    private GoogleApiClient.OnConnectionFailedListener onConnectionFailedListener = new GoogleApiClient.OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(TAG, "On connection failed google API");
        }
    };

    private com.google.android.gms.location.LocationListener locationListener = new com.google.android.gms.location.LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed ");
            if (location != null) {
                sendLocationDataToWebsite(location);
            } else {
                Log.d(TAG, "onLocationChanged : null");
                EventBus.getDefault().post(new GpsEvent());
            }
        }
    };

    protected void sendLocationDataToWebsite(Location location) {
        new ServiceSendLocation(this).fetchService(sessionManager.getIdUser(), String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()),
                new ServiceSendLocation.CallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.e(TAG, "ServiceSendLocation : " + message);
                        Toast.makeText(GPSTracker.this, "Lokasi Akurat", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e(TAG, "failed: " + message);
                    }
                }
        );
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    public void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Gocak Driver")
                .setContentText(getString(R.string.app_active))
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        Notification notification = notificationBuilder.build();

        startForeground(NOTIFICATION_ID, notification);
    }

}