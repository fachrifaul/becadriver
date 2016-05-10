package id.web.go_cak.drivergocak.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.gdLibrary.GoogleDirection;
import id.web.go_cak.drivergocak.gdLibrary.GoogleDirection.OnDirectionResponseListener;

public class ConfirmationFragment extends Fragment {

    private  View view;

    GoogleMap mMap;
    SupportMapFragment Map;
    GoogleDirection gd;
    Document mDoc;
    ArrayList<LatLng> markerPoints;
    TextView tvDistanceDuration;

    LatLng start = new LatLng(13.744246499553903, 100.53428772836924);
    LatLng end = new LatLng(13.751279688694071, 100.54316081106663);

    public ConfirmationFragment() {
    }

    public static Fragment newInstance() {
        return new ConfirmationFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_confirmation, container, false);

        tvDistanceDuration = (TextView) view.findViewById(R.id.tv_distance_time);

        initmap();

        Bundle bundle = this.getArguments();
        String driver = bundle.getString("driver", "Loading");
        String drivername = bundle.getString("drivername","Loading");
        String jarak = bundle.getString("jarak","Loading");
        String ongkos = bundle.getString("ongkos","Loading");
        String PenyewaID = bundle.getString("PenyewaID","Loading");
        String userName = bundle.getString("userName","Loading");
        String lastUpdate = bundle.getString("lastUpdate","Loading");
        String LatTujuan = bundle.getString("LatTujuan","Loading");
        String LongTujuan = bundle.getString("LongTujuan","Loading");
        String LatJemput = bundle.getString("LatJemput","Loading");
        String LongJemput = bundle.getString("LongJemput","Loading");
        String AlamatLengkap = bundle.getString("AlamatLengkap","Loading");
        String tipe = bundle.getString("tipe","Loading");



        return view;
    }

    private void initmap() {

        mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 15));

        gd = new GoogleDirection(getActivity());
        gd.setOnDirectionResponseListener(new OnDirectionResponseListener() {
            public void onResponse(String status, Document doc, GoogleDirection gd) {
                mDoc = doc;
                mMap.addPolyline(gd.getPolyline(doc, 3, Color.RED));
                mMap.addMarker(new MarkerOptions().position(start)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

                mMap.addMarker(new MarkerOptions().position(end)
                        .icon(BitmapDescriptorFactory.defaultMarker(
                                BitmapDescriptorFactory.HUE_GREEN)));

            }
        });

        gd.setLogging(true);
        gd.request(start, end, GoogleDirection.MODE_DRIVING);
        gd.animateDirection(mMap, gd.getDirection(mDoc), GoogleDirection.SPEED_FAST
                , true, false, true, false, null, false, true, null);

    }


}
