package kkactive_india.in.spyapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import kkactive_india.in.spyapp.DeviceTokenPOJO.deviceBean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class FirebaseInstanceIdService extends Service {

    SharedPreferences pref;
    SharedPreferences.Editor edit;
    ConnectionDetector cd;
    String token;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        cd = new ConnectionDetector(getApplication());

        pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        edit = pref.edit();

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                token = task.getResult().getToken();

                // Log.e("Token", token);
                // Log.e("ID",pref.getString("id",null));


                if (cd.isConnectingToInternet()) {
                    Bean b = (Bean) getApplicationContext();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseURL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Allapi cr = retrofit.create(Allapi.class);
                    String id = pref.getString("id", "");
                    // Log.e("IdHaiTokenKeLiye",id);
                    // Log.e("DTOKEN",token);
                    Call<deviceBean> call = cr.dToken(id, token);
                    call.enqueue(new Callback<deviceBean>() {
                        @Override
                        public void onResponse(Call<deviceBean> call, Response<deviceBean> response) {
                            Log.e("DeviceToken", response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<deviceBean> call, Throwable t) {

                            Log.e("DeviceToken", t.toString());

                        }
                    });
                }


            }
        });


        return START_STICKY;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }
}
