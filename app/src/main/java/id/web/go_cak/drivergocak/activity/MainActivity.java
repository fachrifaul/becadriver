package id.web.go_cak.drivergocak.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
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
import id.web.go_cak.drivergocak.service.ServiceRegisterGCM;
import id.web.go_cak.drivergocak.session.RegisterGcmSession;
import id.web.go_cak.drivergocak.session.RegisterIdSession;
import id.web.go_cak.drivergocak.session.SiklulasiSession;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import id.web.go_cak.drivergocak.utils.Const;
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
    private static final String TAG = "GCMDemo";
    private GoogleCloudMessaging gcm;

    private Context context;
    private String regIdUser;

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
            Utils.startAlarmManager(MainActivity.this);


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
        loading = ProgressDialog.show(MainActivity.this, "Tunggu Beberapa saat", "Pengecekan database", true, true);

        String url = Const.WELCOME_URL + "logoutdriver";
        RequestBody formBody = new FormEncodingBuilder()
                .add(Const.id, userSession.getIdUser())
                .build();

        System.out.println("hasilnya adalah " + url + "?" + userSession.getIdUser());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("ERORR", "Failed to execute " + request, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String respon = response.body().string();
                    Log.v("okHttp", respon);
                    //parse(respon);
                    System.out.println("hasilnya adalah " + respon);
                    userSession.userLogoutUser();
                    loading.dismiss();
                    Utils.cancelAlarmManager(MainActivity.this);

                    Intent sendIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(sendIntent);
                    finish();

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("gagalna nyaeta " + e);
                }


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
                new ServiceRegisterGCM(MainActivity.this).fetchRegister(regIdUser, userSession.getIdUser(),
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

    @OnClick(R.id.logo_image_view)
    public void onClicks() {
        Intent intent = new Intent(this, id.web.go_cak.drivergocak.samplelocation.MainActivity.class);
        startActivity(intent);
    }

}
