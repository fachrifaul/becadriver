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
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by fachrifebrian on 4/11/16.
 */
public class ServiceRegisterGCM {

    public interface RegisterGcmUrl {
        @Headers({
                "Accept: application/json",
                "Content-Type: application/json"
        })
        @FormUrlEncoded
        @POST("insertRegsiterID")
        Call<String> registerGcm(@Field("regId") String regId, @Field("id") String id);
    }

    private Context context;
    public static final String TAG = "ServiceRegisterGCM";

    public ServiceRegisterGCM(Context context) {
        this.context = context;
    }

    public void fetchService(String regId, String id, final RegisterGcmCallBack callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiConstant.API_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        RegisterGcmUrl service = retrofit.create(RegisterGcmUrl.class);
        Call<String> listCall = service.registerGcm(regId, id);
        listCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "onResponse: " + response.message());
                Log.d(TAG, "onResponse: " + response.body());
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
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

    public interface RegisterGcmCallBack {
        void onSuccess(String message);

        void onFailure(String message);
    }


}
