package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Transaksi;
import id.web.go_cak.drivergocak.utils.Utils;

public class DetailMapsActivity extends AppCompatActivity  implements RoutingListener,OnMapReadyCallback {
    @Bind(R.id.toolbar) Toolbar toolbar;

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_PHONE_CALL = 100;
    private boolean mPermissionDenied = false;

    protected GoogleMap map;
    private LatLng starD = new LatLng(-6.910569499999999, 107.6497351);
    private LatLng endD = new LatLng(-6.928624799999999, 107.73401319999999);
    private static final String LOG_TAG = "DetailMapsActivity";
    private List<Polyline> polylines = new ArrayList<>();
    private Bundle bundle;
    private Transaksi transaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_maps);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bundle = getIntent().getExtras();
        transaksi = (Transaksi) bundle.getSerializable("TRANSAKSI");

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (Utils.Operations.isOnline(this)) {
            route();
        } else {
            Toast.makeText(this, "No internet connectivity", Toast.LENGTH_SHORT).show();
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

//        String totalDistance = "(" + new DecimalFormat("##.0").format((double) distance / 1000) + " KM)";
//        jarakTextView.setText(totalDistance);

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
        Log.i(LOG_TAG, "Routing was cancelled.");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
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
                } else {
                    mPermissionDenied = true;
                }
                break;
        }
    }

    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_ACCESS_COARSE_LOCATION);
        } else if (map != null) {
            // Access to the location has been granted to the app.
            map.setMyLocationEnabled(true);
        }
    }
}
