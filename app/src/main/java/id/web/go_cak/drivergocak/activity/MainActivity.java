package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.adapter.MainAdapter;
import id.web.go_cak.drivergocak.model.Dashboard;
import id.web.go_cak.drivergocak.service.GPSTracker;
import id.web.go_cak.drivergocak.service.GpsTrackerBootReceiver;
import id.web.go_cak.drivergocak.service.ServiceLogout;
import id.web.go_cak.drivergocak.service.ServiceRegisterGCM;
import id.web.go_cak.drivergocak.session.RegisterGcmSession;
import id.web.go_cak.drivergocak.session.RegisterIdSession;
import id.web.go_cak.drivergocak.session.SiklulasiSession;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import id.web.go_cak.drivergocak.utils.DividerItemDecoration;
import id.web.go_cak.drivergocak.utils.Utils;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.username_textview) TextView usernameTextview;
    @Bind(R.id.phone_textview) TextView phoneTextview;
    @Bind(R.id.avatar_imageview) CircleImageView avatarImageview;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    private UserSession userSession;
    private RegisterIdSession registerIdSession;
    private ProgressDialog loading;
    private GoogleCloudMessaging gcm;

    private Context context;
    private String regIdUser;
    private static final String TAG = "GCMDemo";
    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    public MainActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSession = new UserSession(this);
        if (userSession.checkLogin()) {
            redirectLogin();
        } else {
            setContentView(R.layout.activity_main);
            ButterKnife.bind(this);
            setSupportActionBar(toolbar);

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_ACCESS_COARSE_LOCATION);
            }

            Picasso.with(this).load(ApiConstant.IMAGE_URL + userSession.getFoto()).error(R.drawable.ic_avatar).into(avatarImageview);
            usernameTextview.setText(userSession.getUsername());
            phoneTextview.setText(userSession.getTelp());

            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            MainAdapter adapter = new MainAdapter(this);
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Dashboard item, int position) {
                    if (position == 0) {
                        Intent sendIntent = new Intent(MainActivity.this, TransaksiActivity.class);
                        startActivity(sendIntent);
                        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                    } else if (position == 1) {
                        Intent sendIntent = new Intent(MainActivity.this, ReportActivity.class);
                        startActivity(sendIntent);
                        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
                    } else {
                        showSnackBar("Jalur Pusat masih tertutup");
                    }
                }

            });


            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                Log.e("GPS-ENABLED", gps.getLatitude() + " " + gps.getLongitude());
            } else {
                gps.showSettingsAlert();
            }

            Intent intent = new Intent(this, GpsTrackerBootReceiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
            AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), sender);
            } else {
                am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000, sender);
            }


            //cek GCM
            registerIdSession = new RegisterIdSession(this);

            RegisterGcmSession registerGcmSession = new RegisterGcmSession(this);
            if (!registerGcmSession.checkRegsitered()) {
                context = getApplicationContext();
                if (Utils.checkPlayServices(this)) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    if (registerGcmSession.checkRegsitered()) {
                        regIdUser = registerIdSession.getRegistrationId();
                        if (regIdUser.isEmpty()) {
                            new RegisterGCMBackground().execute();
                        }
                    } else {
                        new RegisterGCMBackground().execute();
                    }
                }
            }

            SiklulasiSession sirkulasiSession = new SiklulasiSession(this);
            if (sirkulasiSession.isCurrentlyProc()) {
                Log.wtf(TAG, "onCreate: " + "GPS LAT " + sirkulasiSession.getlats() + " LONG " + sirkulasiSession.getlongs());
            }

        }
    }

    @OnClick(R.id.fab)
    public void onClickLogout() {
        loading = ProgressDialog.show(MainActivity.this, "Keluar", "Loading...", true, false);

        new ServiceLogout(this).fetchService(userSession.getIdUser(), new ServiceLogout.CallBack() {
            @Override
            public void onSuccess(String message) {
                Log.v("ServiceLogout", message);
                userSession.userLogoutUser();
                loading.dismiss();

                Intent sendIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(sendIntent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Log.e("ServiceLogout", message);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.checkPlayServices(this);
    }

    public class RegisterGCMBackground extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... arg0) {
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }
                regIdUser = gcm.register("670832882424");
                Log.wtf(TAG, "Device registered, registration ID=" + regIdUser);
                new ServiceRegisterGCM(MainActivity.this).fetchService(regIdUser, userSession.getIdUser(),
                        new ServiceRegisterGCM.RegisterGcmCallBack() {
                            @Override
                            public void onSuccess(String message) {
                                Log.wtf(TAG, "onSuccess: " + message);
                            }

                            @Override
                            public void onFailure(String message) {
                                Log.wtf(TAG, "onFailure: " + message);
                            }
                        });

                // Persist the regID - no need to register again.
                registerIdSession.storeRegistrationId(regIdUser);
            } catch (IOException ex) {
                Log.wtf(TAG, ex.getMessage());
            }
            return null;
        }

    }


    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), "Tekan tombol home untuk keluar", Toast.LENGTH_LONG).show();
    }


    private void redirectLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showSnackBar(String message) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                } else {
                    Toast.makeText(this, "Need your location!", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
