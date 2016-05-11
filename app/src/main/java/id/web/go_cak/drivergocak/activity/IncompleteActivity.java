//package id.web.go_cak.drivergocak.activity;
//
//import android.app.ListActivity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.ListAdapter;
//import android.widget.ListView;
//import android.widget.SimpleAdapter;
//
//import org.apache.http.NameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import id.web.go_cak.drivergocak.R;
//import id.web.go_cak.drivergocak.session.UserSession;
//import id.web.go_cak.drivergocak.utils.Const;
//import id.web.go_cak.drivergocak.utils.JSONParser;
//
//public class IncompleteActivity extends ListActivity {
//
//    private UserSession sessionManager;
//    private ProgressDialog pDialog;
//    private JSONParser jsonParser = new JSONParser();
//    private ArrayList<HashMap<String, String>> incompleteList;
//    private JSONArray inbox = null;
//    private static final String INBOX_URL = Const.WELCOME_URL;
//
//    private static final String TAG_MESSAGES = "transaksi";
//    private static final String TAG_ID = "ID";
//    private static final String TAG_USERNAME = "userName";
//    private static final String TAG_DISTANCE = "distance";
//    private static final String TAG_DRIVERID = "driver";
//    private static final String TAG_DRIVERNAME = "drivername";
//    private static final String TAG_DRIVERCONFIRMATION = "driverkonfirmasi";
//    private static final String TAG_DRIVERDONE = "driverdone";
//    private static final String TAG_JARAK = "jarak";
//    private static final String TAG_ONGKOS = "ongkos";
//    private static final String TAG_LATTUJUAN = "LatTujuan";
//    private static final String TAG_LONGTUJUAN = "LongTujuan";
//    private static final String TAG_LATJEMPUT = "LatJemput";
//    private static final String TAG_LONGJEMPUT = "LongJemput";
//    private static final String TAG_ALAMAT = "AlamatLengkap";
//    private static final String TAG_PENYEWAID = "PenyewaID";
//    private static final String TAG_PENYEWANAMA = "nama";
//    private static final String TAG_PENYEWATELP = "telp";
//    private static final String TAG_PENYEWAEMAIL = "email";
//    private static final String TAG_CODE = "code";
//    private static final String TAG_RESULT = "result";
//
//    private ListView lv;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_incomplete);
//
//        lv = getListView();
//
//        sessionManager = new UserSession(this);
//        if (!sessionManager.isUserLoggedIn()) {
//            sessionManager.checkLogin();
//        }
//
//        incompleteList = new ArrayList<>();
//
//        new LoadIncomplete().execute();
//
//    }
//
//    @Override
//    protected void onListItemClick(ListView l, View v, int position, long id) {
//        super.onListItemClick(l, v, position, id);
//
//        HashMap<String, String> itemValue = incompleteList.get(position);
//
//        Intent intent = new Intent(this, ConfirmationActivity.class);
//
//        Bundle args = new Bundle();
//        args.putString("id", itemValue.get(TAG_ID));
//        args.putString("userName", itemValue.get(TAG_USERNAME));
//        args.putString("LatTujuan", itemValue.get(TAG_LATTUJUAN));
//        args.putString("LongTujuan", itemValue.get(TAG_LONGTUJUAN));
//        args.putString("LatJemput", itemValue.get(TAG_LATJEMPUT));
//        args.putString("LongJemput", itemValue.get(TAG_LONGJEMPUT));
//        args.putString("driverkonfirmasi", itemValue.get(TAG_DRIVERCONFIRMATION));
//        args.putString("AlamatLengkap", itemValue.get(TAG_ALAMAT));
//        args.putString("distance", itemValue.get(TAG_DISTANCE));
//        args.putString("nama", itemValue.get(TAG_PENYEWANAMA));
//        args.putString("ongkos", itemValue.get(TAG_ONGKOS));
//        args.putString("tipe", "Antarjemput");
//
//        intent.putExtras(args);
//
//        startActivity(intent);
//
//    }
//
//    /**
//     * Background Async Task to Load all INBOX messages by making HTTP Request
//     */
//    class LoadIncomplete extends AsyncTask<String, String, String> {
//
//        /**
//         * Before starting background thread Show Progress Dialog
//         */
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(IncompleteActivity.this);
//            pDialog.setMessage("Loading ...");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(false);
//            pDialog.show();
//        }
//
//        /**
//         * getting Inbox JSON
//         */
//        protected String doInBackground(String... args) {
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//
//            String urlx = INBOX_URL + "transaksiList/0?driverID=" + sessionManager.getIdUser();
//            Log.d("IncompleteActivity", urlx);
//            JSONObject json = jsonParser.makeHttpRequest(urlx, "GET",
//                    params);
//
//            Log.d("Inbox JSON: ", json.toString());
//
//            try {
//                inbox = json.getJSONArray(TAG_MESSAGES);
//                for (int i = 0; i < inbox.length(); i++) {
//                    JSONObject c = inbox.getJSONObject(i);
//
//                    String id = c.getString(TAG_ID);
//                    String username = c.getString(TAG_USERNAME);
//                    String distance = c.getString(TAG_DISTANCE);
//                    String driverID = c.getString(TAG_DRIVERID);
//                    String drivername = c.getString(TAG_DRIVERNAME);
//                    String driverkonfirmasi = c.getString(TAG_DRIVERCONFIRMATION);
//                    String driverdone = c.getString(TAG_DRIVERDONE);
//                    String jarak = c.getString(TAG_JARAK);
//                    String ongkos = c.getString(TAG_ONGKOS);
//                    String LatTujuan = c.getString(TAG_LATTUJUAN);
//                    String LongTujuan = c.getString(TAG_LONGTUJUAN);
//                    String LatJemput = c.getString(TAG_LATJEMPUT);
//                    String LongJemput = c.getString(TAG_LONGJEMPUT);
//                    String AlamatLengkap = c.getString(TAG_ALAMAT);
//                    String PenyewaID = c.getString(TAG_PENYEWAID);
//                    String nama = c.getString(TAG_PENYEWANAMA);
//                    String telp = c.getString(TAG_PENYEWATELP);
//                    String email = c.getString(TAG_PENYEWAEMAIL);
//                    String code = c.getString(TAG_CODE);
//                    String xresult = c.getString(TAG_RESULT);
//
//                    HashMap<String, String> map = new HashMap<String, String>();
//
//                    map.put(TAG_ID, id);
//                    map.put(TAG_USERNAME, username);
//                    map.put(TAG_DISTANCE, distance);
//                    map.put(TAG_DRIVERID, driverID);
//                    map.put(TAG_DRIVERNAME, drivername);
//                    map.put(TAG_DRIVERCONFIRMATION, driverkonfirmasi);
//                    map.put(TAG_DRIVERDONE, driverdone);
//                    map.put(TAG_JARAK, jarak);
//                    map.put(TAG_ONGKOS, ongkos);
//                    map.put(TAG_LATTUJUAN, LatTujuan);
//                    map.put(TAG_LONGTUJUAN, LongTujuan);
//                    map.put(TAG_LATJEMPUT, LatJemput);
//                    map.put(TAG_LONGJEMPUT, LongJemput);
//                    map.put(TAG_ALAMAT, AlamatLengkap);
//                    map.put(TAG_PENYEWAID, PenyewaID);
//                    map.put(TAG_PENYEWANAMA, nama);
//                    map.put(TAG_PENYEWATELP, telp);
//                    map.put(TAG_PENYEWAEMAIL, email);
//                    map.put(TAG_CODE, code);
//                    map.put(TAG_RESULT, xresult);
//
//                    incompleteList.add(map);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            return null;
//        }
//
//        protected void onPostExecute(String file_url) {
//            pDialog.dismiss();
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    ListAdapter adapter = new SimpleAdapter(
//                            IncompleteActivity.this, incompleteList,
//                            R.layout.incomplete_list_item, new String[]{TAG_PENYEWANAMA, TAG_PENYEWATELP, TAG_ALAMAT},
//                            new int[]{R.id.nama_pelanggan, R.id.call, R.id.alamat});
//                    // updating listview
//                    setListAdapter(adapter);
//                }
//            });
//
//        }
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent sendIntent = new Intent(IncompleteActivity.this, MainActivity.class);
//        startActivity(sendIntent);
//        finish();
//    }
//
//}
