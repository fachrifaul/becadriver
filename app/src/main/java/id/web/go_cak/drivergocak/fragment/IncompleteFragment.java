package id.web.go_cak.drivergocak.fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.activity.ConfirmationActivity;
import id.web.go_cak.drivergocak.adapter.TransactionAdapter;
import id.web.go_cak.drivergocak.model.DaftarTransaksi;
import id.web.go_cak.drivergocak.model.Transaksi;
import id.web.go_cak.drivergocak.service.ServiceTransaction;
import id.web.go_cak.drivergocak.session.UserSession;
import id.web.go_cak.drivergocak.utils.DividerItemDecoration;

public class IncompleteFragment extends Fragment {
    @Bind(R.id.recycler_view) RecyclerView mRecyclerView;

    private List<Transaksi> transaksiList = new ArrayList<>();
    private TransactionAdapter adapter;

    public IncompleteFragment() {
    }

    public static Fragment newInstance() {
        return new IncompleteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_incomplete, container, false);
        ButterKnife.bind(this, view);

        UserSession sessionManager = new UserSession(getActivity());
        if (!sessionManager.isUserLoggedIn()) {
            sessionManager.checkLogin();
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new TransactionAdapter();
        mRecyclerView.setAdapter(adapter);

        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading ...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        ServiceTransaction serviceTransaction = new ServiceTransaction(getActivity());
        serviceTransaction.fetchService(ServiceTransaction.TYPE_INCOMPLETE, sessionManager.getIdUser(),
                new ServiceTransaction.TransactionCallBack() {
                    @Override
                    public void onSuccess(DaftarTransaksi daftarTransaksi) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        transaksiList = daftarTransaksi.transaksi;
                        adapter.add(transaksiList);
                    }

                    @Override
                    public void onFailure(String message) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Log.wtf("onFailure", "onFailure: " + message);
                    }
                });

        adapter.setOnItemClickListener(new TransactionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaksi transaksi, int position) {
                Intent intent = new Intent(getActivity(), ConfirmationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("TRANSAKSI", transaksi);
                intent.putExtras(bundle);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.do_nothing, R.anim.do_nothing);

            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
