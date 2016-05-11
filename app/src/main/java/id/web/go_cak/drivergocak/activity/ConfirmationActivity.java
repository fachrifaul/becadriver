package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Transaksi;
import id.web.go_cak.drivergocak.utils.Const;

public class ConfirmationActivity extends AppCompatActivity implements RoutingListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    @Bind(R.id.toolbar) Toolbar toolbar;

    private TextView userNameTxt, address, Name, koordinat, ongkosTXT;
    private WebView webView;
    private Button prosesButton, prosesCall, cancelButton;
    private int confirmasi = 0;
    private Bundle bundle;
    private Runnable sToastMessager;
    private String id, confString;
    private Transaksi transaksi;

    protected GoogleMap map;
    LatLng starD;
    LatLng endD;
    private static final String LOG_TAG = "MyActivity";
    protected GoogleApiClient mGoogleApiClient;
//    private ProgressDialog progressDialog;
    private List<Polyline> polylines;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        transaksi = (Transaksi) bundle.getSerializable("TRANSAKSI");

        if (transaksi.getUserName().isEmpty()) {
            sToastMessager = new Runnable() {
                @Override
                public void run() {
                    initRun();
                }
            };
            this.runOnUiThread(sToastMessager);
        } else {
            initRun();
        }



    }

    private void initRun() {
        id = transaksi.getID();
        final String userName = transaksi.getUserName();
        final String nama = transaksi.getNama();
        final String LatTujuan = transaksi.getLatTujuan();
        final String LongTujuan = transaksi.getLongTujuan();
        final String LatJemput = transaksi.getLatJemput();
        final String LongJemput = transaksi.getLongJemput();
        final String distance = transaksi.getDistance();
        final String AlamatLengkap = transaksi.getAlamatLengkap();
        final String ongkos = transaksi.getOngkos();
        String driverkonfirmasi = transaksi.getDriverkonfirmasi();


        route();

        if ((!userName.isEmpty()) && (!nama.isEmpty()) || (!LatTujuan.isEmpty())) {
            Log.i("Confirmation ", "Dari GCM : " + userName + "Nama: " + nama + "=" + LatJemput);
        }

        Name = (TextView) findViewById(R.id.nama_pelanggan_text_view);
        userNameTxt = (TextView) findViewById(R.id.no_telp_text_view);
        address = (TextView) findViewById(R.id.alamat_lengkap_text_view);
        ongkosTXT = (TextView) findViewById(R.id.ongkos_text_view);
        prosesButton = (Button) findViewById(R.id.prosesButton);
        prosesCall = (Button) findViewById(R.id.prosesCall);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        koordinat = (TextView) findViewById(R.id.lokasi_text_view);
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        String getSend = "?LatTujuan=" + LatTujuan + "&LongTujuan=" + LongTujuan + "&LatJemput=" + LatJemput + "&LongJemput=" + LongJemput;
        webView.loadUrl(Const.WELCOME_URL + "showjalur" + getSend);

        final Geocoder geoCoder = new Geocoder(this);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String detailFrom = getCompleteAddressString(Double.parseDouble(LatJemput), Double.parseDouble(LongJemput));//+" - "+countryFrom;

                String detailTo = getCompleteAddressString(Double.parseDouble(LatTujuan), Double.parseDouble(LongTujuan)); //+" - "+countryTo;

                Double Ddistance = (Double.parseDouble(distance) / 1000);
                DecimalFormat f = new DecimalFormat("##.0");
                koordinat.setText(detailFrom + " menuju " + detailTo + " (" + f.format(Ddistance) + " KM) ");

                userNameTxt.setText(userName);
                address.setText(AlamatLengkap);
                Name.setText(nama);
                ongkosTXT.setText(ongkos);

                //koordinat.setText(f.format(Ddistance)+" KM) ");

            }
        });


        //koordinat.setText(detailFrom+" menuju "+detailTo+" ("+f.format(Ddistance)+" KM) ");


        if (driverkonfirmasi.equals("0")) {
            prosesButton.setText("Konfirmasi Penjemputan");
            confirmasi = 0;
        } else if (driverkonfirmasi.equals("1")) {
            prosesButton.setText("Proses Antar");
            prosesCall.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            confirmasi = 1;
        } else if (driverkonfirmasi.equals("2")) {
            prosesButton.setText("Telah Sampai Tujuan");
            cancelButton.setVisibility(View.INVISIBLE);
            confirmasi = 2;
        } else {
            prosesButton.setText("Konfirmasi Penjemputan");
            confirmasi = 0;
        }

        prosesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Process();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmasi = 4;
                Process();
            }
        });

        prosesCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

    }

    private void call() {

        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + userNameTxt.getText().toString()));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(callIntent);
        } catch (ActivityNotFoundException activityException) {
            Log.e("helloandroid dialing ", "Call failed");
            Toast.makeText(getApplicationContext(), "Panggilan Tidak Bisa dilakukan", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < (returnedAddress.getMaxAddressLineIndex() - 1); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(" ");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction ", "" + strReturnedAddress.toString());
            } else {
                Log.w("My Current loction ", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction ", "Canont get Address!");
        }
        return strAdd;
    }

    private void Process() {
        String urlSuffix = Const.WELCOME_URL + "ProcessTransaksi/" + confirmasi;
        RequestBody formBody = new FormEncodingBuilder()
                .add("idTransaksi", id)
                .build();

        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(urlSuffix)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .post(formBody)
                .build();

        Call call = new OkHttpClient().newCall(request);
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("error call", "Failed to execute " + request, e);
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
                    JSONObject data = null;
                    try {
                        data = new JSONObject(respon);
                        confString = data.getString("confirmasi");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (confString.equals("0")) {
                                prosesButton.setText("Konfirmasi Penjemputan");
                                confirmasi = 0;
                            } else if (confString.equals("1")) {
                                prosesButton.setText("Proses Antar");
                                prosesCall.setVisibility(View.VISIBLE);
                                cancelButton.setVisibility(View.VISIBLE);
                                confirmasi = 1;
                            } else if (confString.equals("2")) {
                                prosesButton.setText("Telah Sampai Tujuan");
                                cancelButton.setVisibility(View.INVISIBLE);
                                confirmasi = 2;
                            } else if (confString.equals("5") || confString.equals("3")) {

                                Intent sendIntent = new Intent(ConfirmationActivity.this, TransaksiActivity.class);
                                startActivity(sendIntent);
                                finish();

                            }
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("gagalna nyaeta " + e);
                }
            }
        });
    }


    public void route() {

        starD = new LatLng(Double.parseDouble(transaksi.getLatJemput()), Double.parseDouble(transaksi.getLongJemput()));
        starD = new LatLng(Double.parseDouble(transaksi.getLatTujuan()), Double.parseDouble(transaksi.getLatTujuan()));

        polylines = new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        MapsInitializer.initialize(this);
        mGoogleApiClient.connect();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        }
        map = mapFragment.getMap();

//        progressDialog = ProgressDialog.show(this, "Please wait.",
//                "Fetching route information.", true);
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(starD, endD)
                .build();
        routing.execute();
    }


    @Override
    public void onRoutingFailure(RouteException e) {
        // The Routing request failed
//        progressDialog.dismiss();
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
//        progressDialog.dismiss();
        CameraUpdate center = CameraUpdateFactory.newLatLng(starD);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

        map.moveCamera(center);
        map.animateCamera(zoom);

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();


        int max = route.get(0).getDistanceValue();
        int position = 0;
        for (int i = 1; i < route.size(); i++) {
            Log.wtf("MIN", "onRoutingSuccess: " + route.get(i).getDistanceValue());
            if (route.get(i).getDistanceValue() < max) {
                max = route.get(i).getDistanceValue();
                position = i;
            }
        }
        Log.wtf("MIN2", position +" onRoutingSuccess: " + max);

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.accent));
        polyOptions.width(10 + position * 3);
        polyOptions.addAll(route.get(position).getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(starD);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
        map.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(endD);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));
        map.addMarker(options);

    }

    @Override
    public void onRoutingCancelled() {
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

}
