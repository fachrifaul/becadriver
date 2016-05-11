package id.web.go_cak.drivergocak.fragment;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.Const;
import id.web.go_cak.drivergocak.utils.JSONParser;

public class IncompleteFragment extends Fragment {

    @Bind(android.R.id.list) ListView listView;

    private UserSession sessionManager;
    private ProgressDialog pDialog;
    private JSONParser jsonParser = new JSONParser();
    private ArrayList<HashMap<String, String>> incompleteList;
    private JSONArray inbox = null;
    private static final String INBOX_URL = Const.WELCOME_URL;

    private static final String TAG_MESSAGES = "transaksi";
    private static final String TAG_ID = "ID";
    private static final String TAG_USERNAME = "userName";
    private static final String TAG_DISTANCE = "distance";
    private static final String TAG_DRIVERID = "driver";
    private static final String TAG_DRIVERNAME = "drivername";
    private static final String TAG_DRIVERCONFIRMATION = "driverkonfirmasi";
    private static final String TAG_DRIVERDONE = "driverdone";
    private static final String TAG_JARAK = "jarak";
    private static final String TAG_ONGKOS = "ongkos";
    private static final String TAG_LATTUJUAN = "LatTujuan";
    private static final String TAG_LONGTUJUAN = "LongTujuan";
    private static final String TAG_LATJEMPUT = "LatJemput";
    private static final String TAG_LONGJEMPUT = "LongJemput";
    private static final String TAG_ALAMAT = "AlamatLengkap";
    private static final String TAG_PENYEWAID = "PenyewaID";
    private static final String TAG_PENYEWANAMA = "nama";
    private static final String TAG_PENYEWATELP = "telp";
    private static final String TAG_PENYEWAEMAIL = "email";
    private static final String TAG_CODE = "code";
    private static final String TAG_RESULT = "result";

    public IncompleteFragment() {
    }

    public static Fragment newInstance() {
        return new IncompleteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incomplete, container, false);
        ButterKnife.bind(this, view);

        sessionManager = new UserSession(getActivity());
        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
        }

        incompleteList = new ArrayList<>();

        new LoadIncomplete().execute();

        return view;
    }

    class LoadIncomplete extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Loading ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<>();

            String urlx = INBOX_URL + "transaksiList/2?driverID=" + sessionManager.getIdUser();
            Log.d("IncompleteActivity", urlx);
            JSONObject json = jsonParser.makeHttpRequest(urlx, "GET",
                    params);

            Log.d("Inbox JSON: ", json.toString());

            try {
                inbox = json.getJSONArray(TAG_MESSAGES);
                for (int i = 0; i < inbox.length(); i++) {
                    JSONObject c = inbox.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String username = c.getString(TAG_USERNAME);
                    String distance = c.getString(TAG_DISTANCE);
                    String driverID = c.getString(TAG_DRIVERID);
                    String drivername = c.getString(TAG_DRIVERNAME);
                    String driverkonfirmasi = c.getString(TAG_DRIVERCONFIRMATION);
                    String driverdone = c.getString(TAG_DRIVERDONE);
                    String jarak = c.getString(TAG_JARAK);
                    String ongkos = c.getString(TAG_ONGKOS);
                    String LatTujuan = c.getString(TAG_LATTUJUAN);
                    String LongTujuan = c.getString(TAG_LONGTUJUAN);
                    String LatJemput = c.getString(TAG_LATJEMPUT);
                    String LongJemput = c.getString(TAG_LONGJEMPUT);
                    String AlamatLengkap = c.getString(TAG_ALAMAT);
                    String PenyewaID = c.getString(TAG_PENYEWAID);
                    String nama = c.getString(TAG_PENYEWANAMA);
                    String telp = c.getString(TAG_PENYEWATELP);
                    String email = c.getString(TAG_PENYEWAEMAIL);
                    String code = c.getString(TAG_CODE);
                    String xresult = c.getString(TAG_RESULT);

                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put(TAG_ID, id);
                    map.put(TAG_USERNAME, username);
                    map.put(TAG_DISTANCE, distance);
                    map.put(TAG_DRIVERID, driverID);
                    map.put(TAG_DRIVERNAME, drivername);
                    map.put(TAG_DRIVERCONFIRMATION, driverkonfirmasi);
                    map.put(TAG_DRIVERDONE, driverdone);
                    map.put(TAG_JARAK, jarak);
                    map.put(TAG_ONGKOS, ongkos);
                    map.put(TAG_LATTUJUAN, LatTujuan);
                    map.put(TAG_LONGTUJUAN, LongTujuan);
                    map.put(TAG_LATJEMPUT, LatJemput);
                    map.put(TAG_LONGJEMPUT, LongJemput);
                    map.put(TAG_ALAMAT, AlamatLengkap);
                    map.put(TAG_PENYEWAID, PenyewaID);
                    map.put(TAG_PENYEWANAMA, nama);
                    map.put(TAG_PENYEWATELP, telp);
                    map.put(TAG_PENYEWAEMAIL, email);
                    map.put(TAG_CODE, code);
                    map.put(TAG_RESULT, xresult);

                    incompleteList.add(map);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(
                            getActivity(), incompleteList,
                            R.layout.incomplete_list_item, new String[]{TAG_PENYEWANAMA, TAG_PENYEWATELP, TAG_ALAMAT},
                            new int[]{R.id.nama_pelanggan, R.id.call, R.id.alamat});
                    listView.setAdapter(adapter);
                }
            });

        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
