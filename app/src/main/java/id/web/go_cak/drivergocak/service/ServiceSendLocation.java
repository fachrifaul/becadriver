package id.web.go_cak.drivergocak.service;

import android.content.Context;
import android.util.Log;

import id.web.go_cak.drivergocak.R;
import id.web.go_cak.drivergocak.utils.ApiConstant;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by fachrifebrian on 4/11/16.
 */
public class ServiceSendLocation {

    public interface SendLocationUrl {
        @FormUrlEncoded
        @POST("sendgps")
        Call<String> proccesOrder(@Field("id") String id, @Field("latitude") String latitude,
                                  @Field("longitude") String longitude, @Field("speed") String speed);
    }

    private Context context;

    public ServiceSendLocation(Context context) {
        this.context = context;
    }

    public void fetchService(String id, String latitude,
                             String longitude,
                             final CallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        SendLocationUrl service = retrofit.create(SendLocationUrl.class);
        Call<String> listCall = service.proccesOrder(id, latitude, longitude, "1");
        listCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Log.wtf("ServiceProcess", response.body() + "");
                    callback.onSuccess(String.valueOf(response.body()));
                } else {
                    callback.onFailure(context.getString(R.string.koneksi_bermasalah));
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });

    }

    public interface CallBack {
        void onSuccess(String message);

        void onFailure(String message);
    }


}
