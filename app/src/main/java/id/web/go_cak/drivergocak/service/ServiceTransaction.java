package id.web.go_cak.drivergocak.service;

import android.content.Context;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.DaftarTransaksi;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by fachrifebrian on 4/11/16.
 */
public class ServiceTransaction {

    public interface TransactionIncompleteUrl {
        @GET("transaksiList/0")
        Call<DaftarTransaksi> getTransactionIncomplete(
                @Query("driverID") String driverID);

        @GET("transaksiList/2")
        Call<DaftarTransaksi> getTransactionComplete(
                @Query("driverID") String driverID);
    }

    public static int TYPE_INCOMPLETE = 0;
    public static int TYPE_COMPLETE = 2;

    private Context context;

    public ServiceTransaction(Context context) {
        this.context = context;
    }

    public void fetchTransaction(int type, String driverID, final TransactionCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TransactionIncompleteUrl service = retrofit.create(TransactionIncompleteUrl.class);

        Call<DaftarTransaksi> listCall = service.getTransactionIncomplete(driverID);
        if (type == TYPE_INCOMPLETE) {
            listCall = service.getTransactionIncomplete(driverID);
        } else if (type == TYPE_COMPLETE) {
            listCall = service.getTransactionComplete(driverID);
        }

        listCall.enqueue(new Callback<DaftarTransaksi>() {
            @Override
            public void onResponse(Call<DaftarTransaksi> call,
                                   Response<DaftarTransaksi> response) {

                if (response.isSuccessful()) {
                    DaftarTransaksi daftarTransaksi = response.body();

                    if (daftarTransaksi.success == 1) {
                        callback.onSuccess(daftarTransaksi);
                    } else {
                        callback.onFailure(context.getString(R.string.tidak_ada_transaksi));
                    }
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<DaftarTransaksi> call,
                                  Throwable t) {
                callback.onFailure(context.getString(R.string.koneksi_bermasalah));
            }
        });
    }

    public interface TransactionCallBack {
        void onSuccess(DaftarTransaksi daftarTransaksi);

        void onFailure(String message);
    }

}
