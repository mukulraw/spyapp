package kkactive_india.in.spyapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.HashMap;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    // SessionManagement session;
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    String title, msg;
    private String MY_PREFS_NAME;
    int MODE_PRIVATE;
    ArrayList<String> name_list;

    HashMap<String, String> user;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("message", remoteMessage.getData().get("message"));

        /*if (remoteMessage.getData().get("message").equals("Notification Testing")) {
            Intent vib = new Intent(MyFirebaseMessagingService.this, VibrateService.class);
            startService(vib);

        }*/


        name_list = new ArrayList<>();
        // session = new SessionManagement(getApplicationContext());
        //  user = session.getUserDetails();


        //Log.d("loginnnnnnnnn",session.getUserDetails().get(SessionManagement.KEY_NAME));

        title = remoteMessage.getData().get("title");
        msg = remoteMessage.getData().get("message");
        name_list.add(title);


        // Log.d("notihddd",remoteMessage.getData().get("title"));


        //    int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("title_notification", title);
        editor.putString("message_notification", msg);
        editor.commit();


        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("video", "videoFragment");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1410,
                intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .addAction(R.drawable.common_google_signin_btn_icon_dark_focused, "See All", pendingIntent)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1410, notificationBuilder.build());


    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("newToken", s);


        storeRegIdInPref(s);


        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", s);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        // send_token(regId);

    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
