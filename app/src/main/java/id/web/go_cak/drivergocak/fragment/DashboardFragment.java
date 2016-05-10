package id.web.go_cak.drivergocak.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.activity.Report;
import id.web.go_cak.drivergocak.activity.TransaksiActivity;
import id.web.go_cak.drivergocak.adapter.DashboardAdapter;
import id.web.go_cak.drivergocak.model.Dashboard;
import id.web.go_cak.drivergocak.session.RegisterGCM;
import id.web.go_cak.drivergocak.session.UserSessionManager;

public class DashboardFragment extends Fragment {

    /*@Bind(R.id.gridMenu)
    GridView gridMenu;*/

    private DashboardAdapter dashboardAdapter;
    private ArrayList<Dashboard> dashboards;
    private Dashboard dashboard;
    private TextView titleToolbar,mDisplay;
    private String mTitle;
    private NavigationView navigationView;
    private String LOG_TAG = "DashboardFragment";

    public View view;
    private GridView gridMenu;

    // Session Manager Class
    private UserSessionManager sessionManager;
    private RegisterGCM resgisterGCM;

    public DashboardFragment() {
    }

    public static Fragment newInstance() {
        return new DashboardFragment();
    }

    /*public DashboardFragment(TextView titleToolbar, NavigationView navigationView) {
        this.titleToolbar = titleToolbar;
        this.navigationView = navigationView;
    }

    public static Fragment newInstance(TextView titleToolbar, NavigationView navigationView) {
        return new DashboardFragment(titleToolbar, navigationView);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        gridMenu = (GridView) view.findViewById(R.id.gridMenu);
        //mDisplay = (TextView) view.findViewById(R.id.display);
        ButterKnife.bind(this, view);

        // Session class instance
        sessionManager = new UserSessionManager(getActivity());
        resgisterGCM   = new RegisterGCM(getActivity());

        /*ImageView myphoto = (ImageView) view.findViewById(R.id.myphoto);
        TextView Nama = (TextView) view.findViewById(R.id.Nama);
        TextView NoHp = (TextView) view.findViewById(R.id.NoHp);*/

        //Toast.makeText(getActivity(),"Nama "+sessionManager.getUsername(),Toast.LENGTH_LONG).show();

        /*Nama.setText(sessionManager.getUsername());
        NoHp.setText(sessionManager.getTelp());
        mDisplay.setText(resgisterGCM.getRegID());

        Picasso.with(getActivity())
                .load(Const.WEBSITE_URL+"assets/foto/"+sessionManager.getFoto())
                .error(R.drawable.my_avatar)      // optional
                .resize(250, 200)                        // optional
                .placeholder( R.drawable.progress_animation )
                .into(myphoto);
*/
        //titleToolbar.setText(getString(R.string.app_name));

        int[] title = {R.string.transport, R.string.history,R.string.help};
        int[] subtitle = {R.string.subtransport, R.string.subhistory,R.string.subhelp};
        int[] thumbnail = {
                R.drawable.bentor,
                R.drawable.ic_dictionary,
                R.drawable.ic_info
        };

        dashboards = new ArrayList(title.length);
        for (int i = 0; i < title.length; i++) {
            dashboard = new Dashboard(
                    i + "",
                    getString(title[i]),
                    String.valueOf(thumbnail[i])
            );
            dashboards.add(dashboard);
        }

        dashboardAdapter = new DashboardAdapter(getActivity(), dashboards);
        gridMenu.setAdapter(dashboardAdapter);

        gridMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    Intent sendIntent = new Intent(getActivity(), TransaksiActivity.class);

                    /*Bundle args = new Bundle();
                    args.putString("userName", "081392380349");
                    args.putString("LatTujuan", "-6.9370992");
                    args.putString("LongTujuan", "107.78138719999993");
                    args.putString("LatJemput", "-6.908696199999999");
                    args.putString("LongJemput", "107.69527330000005");
                    args.putString("driverkonfirmasi", "0");
                    args.putString("AlamatLengkap","Kompleks Ujung Berung Indah, Cigending, Jawa Barat, Indonesia");
                    args.putString("tipe", "Antarjemput");

                    sendIntent.putExtras(args); //Put your id to your next Intent*/
                    startActivity(sendIntent);
                    getActivity().finish();
                } else if (position == 1) {

                    Intent sendIntent = new Intent(getActivity(), Report.class);
                    startActivity(sendIntent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(),"Jalur Pusat masih tertutup",Toast.LENGTH_LONG).show();
                }
            }

        });

        return view;
    }

}
