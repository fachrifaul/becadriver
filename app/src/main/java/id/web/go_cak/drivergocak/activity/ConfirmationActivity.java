package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Transaksi;
import id.web.go_cak.drivergocak.service.ServiceProcess;
import id.web.go_cak.drivergocak.utils.Utils;

public class ConfirmationActivity extends AppCompatActivity implements RoutingListener, OnMapReadyCallback {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.nama_pelanggan_text_view) TextView namaPelangganTextView;
    @Bind(R.id.no_telp_text_view) TextView noTelpTextView;
    @Bind(R.id.alamat_lengkap_text_view) TextView alamatLengkapTextView;
    @Bind(R.id.ongkos_text_view) TextView ongkosTextView;
    @Bind(R.id.asal_text_view) TextView lokasiAsalTextView;
    @Bind(R.id.tujuan_text_view) TextView lokasiTujuanTextView;
    @Bind(R.id.jarak_text_view) TextView jarakTextView;
    @Bind(R.id.prosesButton) Button prosesButton;
    @Bind(R.id.cancelButton) Button cancelButton;
    @Bind(R.id.callButton) Button callButton;
    @Bind(R.id.progressbar) ProgressBar progressbar;
    @Bind(R.id.button_layout) View buttonLayout;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    private boolean mPermissionDenied = false;
    private static final String TAG = "ConfirmationActivity";
    private int confirmation = 0;
    private Bundle bundle;
    private Transaksi transaksi;
    protected GoogleMap map;
    private LatLng starD = new LatLng(-6.910569499999999, 107.6497351);
    private LatLng endD = new LatLng(-6.928624799999999, 107.73401319999999);
    private List<Polyline> polylines = new ArrayList<>();
    private ProgressDialog progressDialog;

    private String idTransaksi;
    public String userName, nama, latTujuan, longTujuan, latJemput, longJemput, distance,
            alamatLengkap, ongkos, driverkonfirmasi, alamatJemput, alamatTujuan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        transaksi = (Transaksi) bundle.getSerializable("TRANSAKSI");

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading ....");
        progressDialog.show();

        initView();
    }

    private void initView() {
        idTransaksi = transaksi.getID();
        userName = transaksi.getUserName();
        nama = transaksi.getNama();
        alamatTujuan = transaksi.getAlamatto();
        latTujuan = transaksi.getLatTujuan();
        longTujuan = transaksi.getLongTujuan();
        alamatJemput = transaksi.getAlamatfrom();
        latJemput = transaksi.getLatJemput();
        longJemput = transaksi.getLongJemput();
        distance = transaksi.getDistance();
        alamatLengkap = transaksi.getAlamatLengkap();
        ongkos = transaksi.getOngkos();
        driverkonfirmasi = transaksi.getDriverkonfirmasi();

        noTelpTextView.setText(userName);
        alamatLengkapTextView.setText(alamatLengkap);
        namaPelangganTextView.setText(nama);
        ongkosTextView.setText(ongkos);

        if (alamatJemput != null && alamatTujuan != null) {
            progressDialog.dismiss();
            lokasiAsalTextView.setText(alamatJemput);
            lokasiTujuanTextView.setText(alamatTujuan);
        }
        else {
            new AddressBackground().execute();
        }


        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        showData();

        if (driverkonfirmasi.equals("0")) {
            prosesButton.setText("Konfirmasi Penjemputan");
            confirmation = 0;
        }
        else if (driverkonfirmasi.equals("1")) {
            prosesButton.setText("Proses Antar");
            callButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            confirmation = 1;
        }
        else if (driverkonfirmasi.equals("2")) {
            prosesButton.setText("Telah Sampai Tujuan");
            cancelButton.setVisibility(View.INVISIBLE);
            confirmation = 2;
        }
        else {
            prosesButton.setText("Konfirmasi Penjemputan");
            confirmation = 0;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map:
                //lat = y , Long = x
                //asal/current location

                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=" + latJemput + "," + longJemput +
                                "&daddr=" + latTujuan + "," + longTujuan));
                startActivity(intent);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTouch(R.id.maps_view)
    public boolean setOnclickMaps() {
        Intent intent = new Intent(this, DetailMapsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRANSAKSI", transaksi);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
        return false;
    }

    @OnClick(R.id.prosesButton)
    public void setOnclickProses() {
        Process();
    }

    @OnClick(R.id.cancelButton)
    public void setOnclickCancel() {
        confirmation = 4;
        Process();
    }

    @OnClick(R.id.callButton)
    public void setOnclickCallButton() {
        setOnclickCall();
    }

    private void setOnclickCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE_CALL);
        }
        else {
            //Open call function
            try {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + noTelpTextView.getText().toString()));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
            } catch (ActivityNotFoundException activityException) {
                Log.e(TAG, "Call failed");
                Toast.makeText(this, "Panggilan Tidak Bisa dilakukan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showData() {
        if (Utils.Operations.isOnline(this)) {
            route();
        }
        else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
        }
    }

    private void Process() {
        progressbar.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.GONE);
        new ServiceProcess(this).fetchService(String.valueOf(confirmation), idTransaksi, new ServiceProcess.ProcessCallBack() {
            @Override
            public void onSuccess(String messageConfirmation) {
                progressbar.setVisibility(View.GONE);
                buttonLayout.setVisibility(View.VISIBLE);
                if (messageConfirmation.equals("0")) {
                    prosesButton.setText("Konfirmasi Penjemputan");
                    confirmation = 0;
                }
                else if (messageConfirmation.equals("1")) {
                    prosesButton.setText("Proses Antar");
                    callButton.setVisibility(View.VISIBLE);
                    cancelButton.setVisibility(View.VISIBLE);
                    confirmation = 1;
                }
                else if (messageConfirmation.equals("2")) {
                    prosesButton.setText("Telah Sampai Tujuan");
                    cancelButton.setVisibility(View.INVISIBLE);
                    confirmation = 2;
                }
                else if (messageConfirmation.equals("5") || messageConfirmation.equals("3")) {
                    Intent sendIntent = new Intent(ConfirmationActivity.this, TransaksiActivity.class);
                    startActivity(sendIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(String message) {
                buttonLayout.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.GONE);
                Log.d("ServiceProcess", message);
            }
        });
    }


    public void route() {
        /*Log.d("starD", "route: " + transaksi.getLatJemput() + ", " + Double.parseDouble(transaksi.getLongJemput()));
        Log.d("endD", "route: " + transaksi.getLatTujuan() + ", " + Double.parseDouble(transaksi.getLongTujuan()));*/

        starD = new LatLng(Double.parseDouble(transaksi.getLatJemput()), Double.parseDouble(transaksi.getLongJemput()));
        endD = new LatLng(Double.parseDouble(transaksi.getLatTujuan()), Double.parseDouble(transaksi.getLongTujuan()));

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
        Toast.makeText(this, "Coba lagi", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(Utils.midPoint(starD.latitude, starD.longitude, endD.latitude, endD.longitude))
                .zoom(12)
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();

        int distance = route.get(0).getDistanceValue();
        int position = 0;
        for (int i = 1; i < route.size(); i++) {
            if (route.get(i).getDistanceValue() < distance) {
                distance = route.get(i).getDistanceValue();
                position = i;
            }
        }

        String totalDistance = "(" + new DecimalFormat("##.0").format((double) distance / 1000) + " KM)";
        jarakTextView.setText(totalDistance);

        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(getResources().getColor(R.color.colorPrimaryDark));
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
        Log.i(TAG, "Routing was cancelled.");
    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent sendIntent = new Intent(ConfirmationActivity.this, TransaksiActivity.class);
        startActivity(sendIntent);
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent sendIntent = new Intent(ConfirmationActivity.this, TransaksiActivity.class);
        startActivity(sendIntent);
        finish();
        overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });
        enableMyLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ACCESS_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // All good!
                    enableMyLocation();
                }
                else {
                    mPermissionDenied = true;
                }
                break;
            case PERMISSIONS_REQUEST_PHONE_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    setOnclickCall();
                }
                else {
                    Toast.makeText(this, "Sorry!!! Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        }
        else if (map != null) {
            // Access to the location has been granted to the app.
            map.setMyLocationEnabled(true);
        }
    }

    public class AddressBackground extends AsyncTask<String, String, String> {
        String detailFrom, detailTo;

        @Override
        protected String doInBackground(String... arg0) {
            detailFrom = Utils.getCompleteAddressString(ConfirmationActivity.this,
                    Double.parseDouble(latJemput), Double.parseDouble(longJemput));
            detailTo = Utils.getCompleteAddressString(ConfirmationActivity.this,
                    Double.parseDouble(latTujuan), Double.parseDouble(longTujuan));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("AddressBackground", "onPostExecute: " + detailFrom);

            if (detailFrom.equals("") || detailTo.equals("")) {
                new AddressBackground().execute();
            }
            else {
                lokasiAsalTextView.setText(detailFrom);
                lokasiTujuanTextView.setText(detailTo);
                progressDialog.dismiss();
            }
        }
    }
}
