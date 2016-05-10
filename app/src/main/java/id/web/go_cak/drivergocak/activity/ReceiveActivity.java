package id.web.go_cak.drivergocak.activity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReceiveActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getExtras().getString("message");
        System.out.println("tipe msg messages "+message.toString());
        try {

            JSONArray jsonArray = new JSONArray(message);
            System.out.println("tipe msg sebelum for");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);
                System.out.println("tipe msg " + c.getString("tipe"));

                Intent sendIntent = new Intent(this, ConfirmationActivity.class);
                System.out.println("tipe msg sebelum new Budle ");

                Bundle args = new Bundle();
                args.putString("id", c.getString("id"));
                args.putString("userName", c.getString("userName"));
                args.putString("LatTujuan", c.getString("LatTujuan"));
                args.putString("LongTujuan", c.getString("LongTujuan"));
                args.putString("LatJemput",  c.getString("LatJemput"));
                args.putString("LongJemput",  c.getString("LongJemput"));
                args.putString("driverkonfirmasi", "0");
                args.putString("AlamatLengkap", c.getString("AlamatLengkap"));
                args.putString("distance", c.getString("jarak"));
                args.putString("nama", c.getString("nama"));
                args.putString("tipe", c.getString("tipe"));
                args.putString("ongkos", c.getString("ongkos"));
                /*
                args.putString("driver", c.getString("driver"));
                args.putString("drivername", c.getString("drivername"));
                args.putString("distance", c.getString("jarak"));
                args.putString("jarak", c.getString("jarak"));
                args.putString("ongkos", c.getString("ongkos"));
                args.putString("PenyewaID", c.getString("PenyewaID"));
                args.putString("userName", c.getString("userName"));
                args.putString("lastUpdate", c.getString("lastUpdate"));
                args.putString("LatTujuan", c.getString("LatTujuan"));
                args.putString("LongTujuan", c.getString("LongTujuan"));
                args.putString("LatJemput", c.getString("LatJemput"));
                args.putString("LongJemput", c.getString("LongJemput"));
                args.putString("AlamatLengkap", c.getString("AlamatLengkap"));
                args.putString("nama", c.getString("nama"));
                args.putString("tipe", c.getString("tipe"));/*/

                System.out.println("tipe msg sebelum Put Extra ");

                sendIntent.putExtras(args);

                System.out.println("tipe msg sebelum Start Activity ");

                startActivity(sendIntent);

                /*sendIntent.putExtras(args); //Put your id to your next Intent
                startActivity(sendIntent);
                finish();*/

                System.out.println("tipe msg test akhir " +  c.getString("nama"));

                /*
                how to call
                Bundle b = getIntent().getExtras();
                int value = b.getInt("key");
                 */



            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
