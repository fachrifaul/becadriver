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
import com.squareup.okhttp.MediaType;
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
import id.web.go_cak.drivergocak.service.ServiceRegisterGCM;
import id.web.go_cak.drivergocak.session.RegisterGCM;
import id.web.go_cak.drivergocak.session.RegisterIdSession;
import id.web.go_cak.drivergocak.session.SiklulasiSession;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.tracker.GPSTracker;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import id.web.go_cak.drivergocak.utils.Const;
import id.web.go_cak.drivergocak.utils.Utils;
import id.web.go_cak.drivergocak.views.DividerItemDecoration;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.username_textview) TextView usernameTextview;
    @Bind(R.id.phone_textview) TextView phoneTextview;
    @Bind(R.id.avatar_imageview) CircleImageView avatarImageview;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    private MainAdapter adapter;
    private UserSession userSession;
    private SiklulasiSession sirkulasiSession;
    private RegisterIdSession registerIdSession;

    private RegisterGCM registerGCM;
    private ProgressDialog loading;
    private String SENDER_ID = "670832882424";

    private static final String TAG = "GCMDemo";
    private GoogleCloudMessaging gcm;

    private Context context;
    private String regid;

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

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

            adapter = new MainAdapter(this);
            mRecyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Dashboard item, int position) {
                    if (position == 0) {
                        Intent sendIntent = new Intent(MainActivity.this, TransaksiActivity.class);
                        startActivity(sendIntent);
                        finish();
                    } else if (position == 1) {

                        Intent sendIntent = new Intent(MainActivity.this, Report.class);
                        startActivity(sendIntent);
                        finish();
                    } else {
                        showSnackBar("Jalur Pusat masih tertutup");
                    }
                }

            });

            sirkulasiSession = new SiklulasiSession(this);

            GPSTracker gps = new GPSTracker(this);
            if (gps.canGetLocation()) {
                Log.e("GPS-ENABLED", gps.getLatitude() + " " + gps.getLongitude());
            } else {
                gps.showSettingsAlert();
            }
            Utils.startAlarmManager(MainActivity.this);


            //cek GCM
            registerIdSession = new RegisterIdSession(this);

            registerGCM = new RegisterGCM(this);
            if (registerGCM.checkRegsitered()) {

            } else {
                context = getApplicationContext();
                if (Utils.checkPlayServices(this)) {
                    gcm = GoogleCloudMessaging.getInstance(this);
                    if (registerGCM.checkRegsitered()) {
                        regid = registerIdSession.getRegistrationId();
                        if (regid.isEmpty()) {
                            new RegisterGCMBackground().execute();
                        }
                    } else {
                        new RegisterGCMBackground().execute();
                    }
                }
            }

            if (sirkulasiSession.isCurrentlyProc()) {
                Toast.makeText(this, "GPS LAT " + sirkulasiSession.getlats() + " LONG " + sirkulasiSession.getlongs(), Toast.LENGTH_LONG).show();
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
                regid = gcm.register(SENDER_ID);
                Log.wtf(TAG, "Device registered, registration ID=" + regid);
                new ServiceRegisterGCM(MainActivity.this).fetchRegister(regid, userSession.getIdUser(),
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
                registerIdSession.storeRegistrationId(regid);
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

}
