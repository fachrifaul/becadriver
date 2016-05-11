package id.web.go_cak.drivergocak.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.utils.Const;

public class ConfirmationActivity extends Activity {

    private TextView userNameTxt, address, Name, koordinat, ongkosTXT;
    private WebView webView;
    private Button prosesButton, prosesCall, cancelButton;
    private int confirmasi = 0;
    private Context context;
    private Bundle b;
    private Runnable sToastMessager;
    private String id, confString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        b = getIntent().getExtras();

        if (b.getString("userName").isEmpty()) {
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

        id = b.getString("id");
        final String userName = b.getString("userName");
        final String nama = b.getString("nama");
        final String LatTujuan = b.getString("LatTujuan");
        final String LongTujuan = b.getString("LongTujuan");
        final String LatJemput = b.getString("LatJemput");
        final String LongJemput = b.getString("LongJemput");
        final String distance = b.getString("distance");
        final String AlamatLengkap = b.getString("AlamatLengkap");
        final String ongkos = b.getString("ongkos");
        String driverkonfirmasi = b.getString("driverkonfirmasi");

        if ((!userName.isEmpty()) && (!nama.isEmpty()) || (!LatTujuan.isEmpty())) {
            //Toast.makeText(this,userName+"="+nama+""+LatTujuan,Toast.LENGTH_LONG).show();
            Log.i("Confirmation ", "Dari GCM : " + userName + "Nama: " + nama + "=" + LatJemput);
        }

        Name = (TextView) findViewById(R.id.Name);
        userNameTxt = (TextView) findViewById(R.id.userName);
        address = (TextView) findViewById(R.id.address);
        ongkosTXT = (TextView) findViewById(R.id.ongkosTXT);
        prosesButton = (Button) findViewById(R.id.prosesButton);
        prosesCall = (Button) findViewById(R.id.prosesCall);

        cancelButton = (Button) findViewById(R.id.cancelButton);
        koordinat = (TextView) findViewById(R.id.koordinat);
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

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent sendIntent = new Intent(ConfirmationActivity.this, TransaksiActivity.class);
        startActivity(sendIntent);
        finish();
    }

}
