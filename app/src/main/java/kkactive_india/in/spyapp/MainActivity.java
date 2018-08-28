package kkactive_india.in.spyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        Log.d("IMEI1",imeiNumber1);
        Log.d("IMEI2",imeiNumber2);

        @SuppressLint("HardwareIds") String number = tm.getLine1Number();
        Log.d("numner", number);

        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        Log.d("Test", "Current list = " + subsInfoList);

        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

            String num = subscriptionInfo.getNumber();

            Log.d("Test", " Number is  " + num);
        }

    }
}
