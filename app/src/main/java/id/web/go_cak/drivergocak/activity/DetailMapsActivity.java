package id.web.go_cak.drivergocak.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Transaksi;
import id.web.go_cak.drivergocak.utils.Utils;

public class DetailMapsActivity extends AppCompatActivity  implements RoutingListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    @Bind(R.id.toolbar) Toolbar toolbar;

    protected GoogleMap map;
    private LatLng starD = new LatLng(-6.910569499999999, 107.6497351);
    private LatLng endD = new LatLng(-6.928624799999999, 107.73401319999999);
    private static final String LOG_TAG = "DetailMapsActivity";
    protected GoogleApiClient mGoogleApiClient;
    private List<Polyline> polylines;
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
        /*Log.wtf("starD", "route: " + transaksi.getLatJemput() + ", " + Double.parseDouble(transaksi.getLongJemput()));
        Log.wtf("endD", "route: " + transaksi.getLatTujuan() + ", " + Double.parseDouble(transaksi.getLongTujuan()));*/

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
        CameraUpdate center = CameraUpdateFactory.newLatLng(starD);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
        map.moveCamera(center);
        map.animateCamera(zoom);

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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(LOG_TAG, connectionResult.toString());
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
