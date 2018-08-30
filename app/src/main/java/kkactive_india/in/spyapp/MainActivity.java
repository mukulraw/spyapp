package kkactive_india.in.spyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textview);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        assert tm != null;
        @SuppressLint("HardwareIds") String imeiNumber1 = tm.getDeviceId(1); //(API level 23)
        @SuppressLint("HardwareIds") String imeiNumber2 = tm.getDeviceId(2);

        Log.d("IMEI1", imeiNumber1);
        Log.d("IMEI2", imeiNumber2);

        @SuppressLint("HardwareIds") String number = tm.getLine1Number();
        Log.d("numner", number);

        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        Log.d("Test", "Current list = " + subsInfoList);

        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

            String num = subscriptionInfo.getNumber();

            Log.d("Test", " Number is  " + num);
        }


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                1);
        // String strOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        StringBuffer sb = new StringBuffer();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        sb.append("Contact Details :");
        while (phones.moveToNext()) {
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();

            Log.d("names", name);
            Log.d("Phones", phoneNumber);
            //textView.setText("\n Name:" + name +"\nNumber:" + phoneNumber );

            sb.append(" \nName:--- " + name + "\nPhone Number:--- " + phoneNumber);
            sb.append("\n----------------------------------");


        }
        phones.close();
        textView.setText(sb);


        //callLogs();

        // getSMS();


    }

    public void callLogs() {


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG},
                2);
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            String pName = managedCursor.getString(name);
            String phNumber = managedCursor.getString(number); // mobile number
            String callType = managedCursor.getString(type); // call type
            String callDate = managedCursor.getString(date); // call date
            Date callDayTime = new Date(Long.valueOf(callDate));
            String callDuration = managedCursor.getString(duration);
            String dir = null;

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
            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
            sb.append("\n----------------------------------");
        }
        managedCursor.close();
        // textView.setText(sb);
        Log.e("Agil value --- ", sb.toString());


    }


    public List<String> getSMS() {
        List<String> sms = new ArrayList<String>();
        Uri uriSMSURI = Uri.parse("content://sms");
        StringBuffer sb = new StringBuffer();
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, strOrder);

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
                case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                    type = "outbox";
                    break;
                default:
                    break;
            }
            sms.add("\nNumber: " + address + "\n Message: " + body + "\n Date:" + dateFormat + "\n Type:" + type);

            sb.append("\nNumber: " + address + "\n Message: " + body + "\n Date:" + dateFormat + "\n Type:" + type);
            sb.append("\n-----------------");
        }
        Log.d("SMSS", sms.toString());
        textView.setText(sb);

        if (cur != null) {
            cur.close();
        }
        return sms;
    }

}
