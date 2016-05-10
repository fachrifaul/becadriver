package id.web.go_cak.drivergocak.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.picasso.Picasso;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.io.IOException;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.fragmen.DashboardFragment;
//import id.web.go_cak.drivergocak.service.LoopjHttpClient;
import id.web.go_cak.drivergocak.session.UserSessionManager;
import id.web.go_cak.drivergocak.session.SiklulasiSession;
import id.web.go_cak.drivergocak.session.RegisterGCM;
import id.web.go_cak.drivergocak.tracker.GpsTrackerAlarmReceiver;
import id.web.go_cak.drivergocak.utils.Const;

public class MainActivity extends AppCompatActivity {

    private Fragment fragment;
    // Session Manager Class
    private UserSessionManager sessionManager;
    private SiklulasiSession siklulasiPosition;
    private RegisterGCM registerGCM;

    private Intent gpsTrackerIntent;
    private PendingIntent pendingIntent;
    private boolean currentlyTracking;
    private int intervalInMinutes = 1;
    private AlarmManager alarmManager;

    //GCM
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String LOG_TAG = "KamusActivity";
    private ProgressDialog loading;
    // please enter your sender id
    String SENDER_ID = "670832882424";

    public FloatingActionButton fab;

    static final String TAG = "GCMDemo";
    GoogleCloudMessaging gcm;

    //TextView mDisplay;
    Context context;
    String regid;
    //

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //mDisplay = (TextView) findViewById(R.id.display);
        ImageView myphoto = (ImageView) findViewById(R.id.myphoto);
        TextView userName = (TextView) findViewById(R.id.userName_t);
        TextView telp = (TextView) findViewById(R.id.telp_t);

        // Session class instance
        sessionManager = new UserSessionManager(this);
        siklulasiPosition = new SiklulasiSession(this);

        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
        } else {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                Toast.makeText(this, "GPS anda telah aktif", Toast.LENGTH_SHORT).show();
            }else{
                showGPSDisabledAlertToUser();
            }

            userName.setText(sessionManager.getUsername());
            telp.setText(sessionManager.getTelp());
            //cek GCM
            registerGCM = new RegisterGCM(this);
            if (registerGCM.checkRegsitered()) {
                //Toast.makeText(this, "true " + registerGCM.getRegID(), Toast.LENGTH_LONG).show();
                //mDisplay.setText("true");
            } else {
                //Toast.makeText(this, "Proses Pendaftaran RegID pusat", Toast.LENGTH_LONG).show();
                //mDisplay.setText("false");
                //GCM
                context = getApplicationContext();
                if (checkPlayServices()) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    if (registerGCM.checkRegsitered()) {
                        regid = getRegistrationId(context);
                        if (regid.isEmpty()) {
                            new RegisterBackground().execute();
                        } else {
                            //mDisplay.setText(regid);
                            //mDisplay.setText(registerGCM.getRegID());
                            //Toast.makeText(this, registerGCM.getRegID(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        new RegisterBackground().execute();
                    }
                }
                //
            }


            Picasso.with(this)
                    .load(Const.WEBSITE_URL+"assets/foto/"+sessionManager.getFoto())
                    .error(R.drawable.my_avatar)      // optional
                    .resize(250, 200)                        // optional
                    .placeholder( R.drawable.progress_animation )
                    .into(myphoto);

            fragment = DashboardFragment.newInstance();
           // fragment = ConfirmationFragment.newInstance();
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
            }

            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    fab.setVisibility(View.INVISIBLE);

                    loading = ProgressDialog.show(MainActivity.this, "Tunggu Beberapa saat", "Pengecekan database", true, true);

                    String msg;
                    try {
                        gcm.unregister();
                        msg  = "Dvice unregistered, success unregistration";
                        fab.setVisibility(View.INVISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        msg  = "Dvice unregistered, Failed unregistration";
                        fab.setVisibility(View.VISIBLE);
                    }

                    Log.d("111", msg);

                    String url = Const.WELCOME_URL + "logoutdriver";
                    //RequestBody formBody = RequestBody.create(JSON, "{"+Const.VARREGID+":"+regid+","+Const.id+":"+sessionManager.getIdUser()+"}");
                    RequestBody formBody = new FormEncodingBuilder()
                            .add(Const.id, sessionManager.getIdUser())
                            .build();

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
                                System.out.println("hasilnya adalah " + respon);
                                fab.setVisibility(View.VISIBLE);
                                sessionManager.userLogoutUser();
                                loading.dismiss();
                                cancelAlarmManager();

                                Intent sendIntent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(sendIntent);
                                finish();

                            } catch (IOException e) {
                                e.printStackTrace();
                                System.out.println("gagalna nyaeta " + e);
                                fab.setVisibility(View.VISIBLE);
                            }


                        }
                    });

                    /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();*/
                }
            });

            startAlarmManager();

            if(siklulasiPosition.isCurrentlyProc()==true) {

                Toast.makeText(this,"GPS LAT "+siklulasiPosition.getlats()+" LONG "+siklulasiPosition.getlongs(),Toast.LENGTH_LONG).show();

            }

        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS Anda belum aktif, silakan aktifkan GPS!")
                .setCancelable(false)
                .setPositiveButton("Masuklah ke pengaturan atau klik tulidsn ini!",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        checkPlayServices();
    }

    class RegisterBackground extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... arg0) {
            // TODO Auto-generated method stub
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regid = gcm.register(SENDER_ID);
                msg = "Dvice registered, registration ID=" + regid;
                Log.d("111", msg);
                sendRegistrationIdToBackend();

                // Persist the regID - no need to register again.
                storeRegistrationId(context, regid);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String msg) {
            //Toast.makeText(MainActivity.this,msg,Toast.LENGTH_LONG).show();
        }

        private void sendRegistrationIdToBackend() {
            // Your implementation here.

            String url = Const.WELCOME_URL + "insertRegsiterID";
            //RequestBody formBody = RequestBody.create(JSON, "{"+Const.VARREGID+":"+regid+","+Const.id+":"+sessionManager.getIdUser()+"}");
            RequestBody formBody = new FormEncodingBuilder()
                    .add(Const.VARREGID, regid)
                    .add(Const.id, sessionManager.getIdUser())
                    .build();

            System.out.println("hasilnya adalah " + url+ "?"+Const.VARREGID+"="+regid+"&"+Const.id+"="+ sessionManager.getIdUser());

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
                        System.out.println("hasilnya adalah " + respon);

                        JSONObject data = null;

                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("gagalna nyaeta " + e);
                    }


                }
            });

        }

        private void storeRegistrationId(Context context, String regId) {
            final SharedPreferences prefs = getGCMPreferences(context);
            int appVersion = getAppVersion(context);
            Log.i(TAG, "Saving regId on app version " + appVersion);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PROPERTY_REG_ID, regId);
            editor.putInt(PROPERTY_APP_VERSION, appVersion);
            editor.commit();
        }
    }

    public void sendRegID() {
        
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private SharedPreferences getGCMPreferences(Context context) {

        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }*/

    private Boolean exit = false;
    @Override
    public void onBackPressed() {

        Toast.makeText(getApplicationContext(),"Tekan tombol home untuk keluar", Toast.LENGTH_LONG).show();

        /*
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();

            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
          */
    }

    public void startAlarmManager() {
        Log.d(TAG, "hasil startAlarmManager");
        currentlyTracking = true;

        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);

       // SharedPreferences sharedPreferences = this.getSharedPreferences("com.websmithing.gpstracker.prefs", Context.MODE_PRIVATE);
       // intervalInMinutes = sharedPreferences.getInt("intervalInMinutes", 1);
       intervalInMinutes = 3;

       /* alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                intervalInMinutes * 60000, // 60000 = 1 minute
                pendingIntent);*/

        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(),
                30000, // 60000 = 1 minute, 5000 = 5 seconds
                pendingIntent);


        //Toast.makeText(this,"Update GPS location",Toast.LENGTH_LONG).show();
        Log.d("hasil", "Update GPS location");

    }

    public void cancelAlarmManager() {
        Log.d(TAG, "cancelAlarmManager");

        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, GpsTrackerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

}
