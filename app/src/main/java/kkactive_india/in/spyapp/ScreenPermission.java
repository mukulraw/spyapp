package kkactive_india.in.spyapp;

import android.app.Activity;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;

import static kkactive_india.in.spyapp.ScreenShot.mProjectionManager;
import static kkactive_india.in.spyapp.ScreenShot.sMediaProjection;
import static kkactive_india.in.spyapp.ScreenShot.setScreenshotPermission;

public class ScreenPermission extends Activity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_permission);

        Log.e("Media","Activity");

        if (mProjectionManager != null)
        {
            Log.e("Media",mProjectionManager.toString());
        }

        startActivityForResult(mProjectionManager.createScreenCaptureIntent(), 1);

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {

            Log.e("Media","permission");

            if (resultCode == Activity.RESULT_OK) {

                Log.e("Media","Allow");

                sMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
                setScreenshotPermission((Intent) data.clone());
                // this.finish();
            } else {
                setScreenshotPermission(null);
                Log.e("Access", "No Access");

            }
            finish();
        }

    }
}
