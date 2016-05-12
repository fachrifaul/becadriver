package id.web.go_cak.drivergocak.service;

import android.content.Context;
import android.util.Log;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.model.Confirmation;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by fachrifebrian on 4/11/16.
 */
public class ServiceProcess {

    public interface ProcessUrl {
        @FormUrlEncoded
        @POST("ProcessTransaksi/{confirmation}")
        Call<Confirmation> proccesOrder(@Path("confirmation") String confirmation,
                                        @Field("idTransaksi") String idTransaksi);
    }

    private Context context;

    public ServiceProcess(Context context) {
        this.context = context;
    }

    public void fetchService(String confirmation, String idTransaksi, final ProcessCallBack callback) {
        Log.wtf("ServiceProcess", "fetchService: " + confirmation + " " + idTransaksi);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ProcessUrl service = retrofit.create(ProcessUrl.class);
        Call<Confirmation> listCall = service.proccesOrder(confirmation, idTransaksi);
        listCall.enqueue(new Callback<Confirmation>() {
            @Override
            public void onResponse(Call<Confirmation> call, Response<Confirmation> response) {
                if (response.isSuccessful()) {
                    Log.wtf("ServiceProcess", response.body().getConfirmasi() + "");
                    callback.onSuccess(String.valueOf(response.body().getConfirmasi()));
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<Confirmation> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });

    }

    public interface ProcessCallBack {
        void onSuccess(String message);

        void onFailure(String message);
    }


}
