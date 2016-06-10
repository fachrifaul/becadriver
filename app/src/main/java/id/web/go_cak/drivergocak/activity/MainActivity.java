package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.adapter.MainAdapter;
import id.web.go_cak.drivergocak.event.GpsEvent;
import id.web.go_cak.drivergocak.event.LogoutEvent;
import id.web.go_cak.drivergocak.model.Dashboard;
import id.web.go_cak.drivergocak.service.GPSTracker;
import id.web.go_cak.drivergocak.service.ServiceLogout;
import id.web.go_cak.drivergocak.service.ServiceRegisterGCM;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import id.web.go_cak.drivergocak.utils.DividerItemDecoration;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.username_textview) TextView usernameTextview;
    @Bind(R.id.phone_textview) TextView phoneTextview;
    @Bind(R.id.avatar_imageview) CircleImageView avatarImageview;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;
    @Bind(R.id.version_text_view) TextView versionTextView;

    private UserSession userSession;
    private ProgressDialog loading;

    private String myToken;
    private static final String TAG = "MainActivity";
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
            versionApp();

//            versionTextView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this, "Lokasi Akurat", Toast.LENGTH_SHORT).show();
//                }
//            });


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

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        } else {
            Log.d(TAG, "onStart: " + isLocationEnabled());
            if (!isLocationEnabled()) {
                showEnableLocationDialog();
            } else {
                registerGCM();
                startService(new Intent(this, GPSTracker.class));
            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onGpsEvent(GpsEvent gpsEvent) {
        showEnableLocationDialog();
    }

    protected boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 6000, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        Log.d(TAG, "location: " + location.getLatitude() + "," + location.getLongitude());
                    } else {
                        Log.d(TAG, "location: null");
                        showEnableLocationDialog();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void showEnableLocationDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("GPS harus diaktifkan. Silakan ke halaman pengaturan di perangkat untuk mengaktfikannya");
        alertDialog.setPositiveButton("Buka Pengaturan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Tutup aplikasi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

    @OnClick(R.id.fab)
    public void onClickLogout() {
        loading = ProgressDialog.show(MainActivity.this, "Keluar", "Loading...", true, false);

        new ServiceLogout(this).fetchService(userSession.getIdUser(), new ServiceLogout.CallBack() {
            @Override
            public void onSuccess(String message) {
                Log.v("ServiceLogout", message);
                stopService(new Intent(MainActivity.this, GPSTracker.class));
                EventBus.getDefault().post(new LogoutEvent());
                userSession.userLogoutUser();
                loading.dismiss();

                Intent sendIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(sendIntent);
                finish();
            }

            @Override
            public void onFailure(String message) {
                Log.e("ServiceLogout", message);
                Toast.makeText(getApplicationContext(), "Cek koneksi anda", Toast.LENGTH_LONG).show();
                loading.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void registerGCM() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                myToken = FirebaseInstanceId.getInstance().getToken();
                if (myToken != null) {
                    Log.d(TAG, "FCM ID : " + myToken);
                    new ServiceRegisterGCM(MainActivity.this).fetchService(myToken, userSession.getIdUser(),
                            new ServiceRegisterGCM.RegisterGcmCallBack() {
                                @Override
                                public void onSuccess(String message) {
                                    Log.d(TAG, "ServiceRegisterGCM onSuccess: " + message);
                                }

                                @Override
                                public void onFailure(String message) {
                                    Log.d(TAG, "ServiceRegisterGCM onFailure: " + message);
                                }
                            });
                } else {
                    registerGCM();
                }

            }
        }, 1000);
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

    private void versionApp() {
        StringBuilder appNameStringBuilder = new StringBuilder();
        appNameStringBuilder.append(getString(R.string.app_name));
        appNameStringBuilder.append(" ");
        appNameStringBuilder.append("v");
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appNameStringBuilder.append(packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionTextView.setText(appNameStringBuilder.toString());
    }
}
