package id.web.go_cak.drivergocak.tracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.Const;
//import id.web.go_cak.drivergocak.session.SiklulasiSession;

//import com.google.android.gms.common.GooglePlayServicesUtil;

public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "LocationService";

    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;

    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    // Session Manager Class
    private UserSession sessionManager;
    //private int cekFalse=0;
    //private SiklulasiSession siklulasiSession;

    @Override
    public void onCreate() {
        super.onCreate();

        currentlyProcessingLocation = false;
        sessionManager = new UserSession(this);
        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
            currentlyProcessingLocation = false;
        }

        //siklulasiSession = new SiklulasiSession(this);

        defaultUploadWebsite = Const.WELCOME_URL + "sendgps";

        /*if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        } else {
            currentlyProcessingLocation = false;
        }*/

        Log.e(TAG, "hasil berada di Oncreate currentlyProcessingLocation "+currentlyProcessingLocation);

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
        Log.d(TAG, "hasil berada startTracking");

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
            Log.e(TAG, "hasil unable to connect to google play services currentlyProcessingLocation "+currentlyProcessingLocation);
        }
        Log.e(TAG, "hasil berada di startTracking currentlyProcessingLocation "+currentlyProcessingLocation);
    }

    protected void sendLocationDataToWebsite(Location location) {
        // formatted for mysql datetime format
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getDefault());
        Date date = new Date(location.getTime());

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

        editor.putFloat("previousLatitude", (float)location.getLatitude());
        editor.putFloat("previousLongitude", (float)location.getLongitude());
        editor.apply();

        Log.e(TAG, "hasil berada di sendLocationDataToWebsite currentlyProcessingLocation " + currentlyProcessingLocation);

        String url = Const.WELCOME_URL + "sendgps";
        Double speedInMilesPerHour = location.getSpeed()* 2.2369;
        RequestBody formBody = new FormEncodingBuilder()
                .add(Const.id, sessionManager.getIdUser())
                .add(Const.LATITUDE, Double.toString(location.getLatitude()))
                .add(Const.LONGITUDE, Double.toString(location.getLongitude()))
                .add(Const.SPEED, Integer.toString(speedInMilesPerHour.intValue()))
                .build();

        String Alamat = getCompleteAddressString(location.getLatitude(),location.getLongitude());

        Toast.makeText(this, "sessionManager " + sessionManager.getUsername() + " Update "+Alamat+" Lat " + Double.toString(location.getLatitude()) + " Long " + Double.toString(location.getLongitude()), Toast.LENGTH_LONG).show();
        System.out.println("hasilnya adalah " + url + "?" + sessionManager.getIdUser());

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(com.squareup.okhttp.Request request, IOException e) {
                Log.e("ERORR", "Failed to execute " + request, e);
            }

            @Override
            public void onResponse(com.squareup.okhttp.Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String respon = response.body().string();
                    Log.v("okHttp", respon);
                    //parse(respon);
                    currentlyProcessingLocation = false;
                    System.out.println(currentlyProcessingLocation +" hasilnya adalah " + respon);
                    stopSelf();
                } catch (IOException e) {
                    currentlyProcessingLocation = false;
                    e.printStackTrace();
                    System.out.println("hasil gagalna nyaeta " + e);
                    stopSelf();
                }


            }
        });

        Log.e(TAG, "hasil berada di akhir sendLocationDataToWebsite currentlyProcessingLocation " + currentlyProcessingLocation);

    }

    public String getCompleteAddressString(double LATITUDE,double LONGITUDE) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w(TAG, "hasil " + strReturnedAddress.toString());
            } else {
                Log.w(TAG, "hasil No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w(TAG, "hasil Canont get Address!");
        }
        return strAdd;
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
            Log.e(TAG, "hasil position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates

            if (sessionManager.isUserLoggedIn()) {
                //if (location.getAccuracy() < 500.0f) {
                currentlyProcessingLocation = true;
                    stopLocationUpdates();
                    sendLocationDataToWebsite(location);
                    Toast.makeText(this,"sessionManager "+sessionManager.getUsername()+" Update location ",Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000); // milliseconds
        locationRequest.setFastestInterval(1000); // the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);

        Log.e(TAG, "hasil berada di onConnected currentlyProcessingLocation " + currentlyProcessingLocation);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "hasil onConnectionFailed");

        Log.e(TAG, "hasil berada di onConnectionFailed currentlyProcessingLocation " + currentlyProcessingLocation);

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "hasil GoogleApiClient connection has been suspend currentlyProcessingLocation "+currentlyProcessingLocation);
    }
}
