package com.example.adhish.quiphservice;

import android.accessibilityservice.AccessibilityService;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import static android.content.ContentValues.TAG;

/**
 * Created by Adhish on 26/12/17.
 */

public class MyQuiphService extends AccessibilityService {


    public void onAccessibilityEvent(AccessibilityEvent event) {
        getChromeUrl(getRootInActiveWindow());
    }

    public void getChromeUrl(AccessibilityNodeInfo nodeInfo) {
        //Use the node info tree to identify the proper content.
        //For now we'll just log it to logcat.
        Log.d(TAG, toStringHierarchy(nodeInfo, 0));
    }
    private String toStringHierarchy(AccessibilityNodeInfo info, int depth) {
        if (info == null) return "";

        String result = "|";
        for (int i = 0; i < depth; i++) {
            if (result.contains("www.quiph.com")) {

                if(appInstalledOrNot("https://play.google.com/store/apps/details?id=com.application.zomato&hl=en"))
                {
                    showAlerttoDownloadOrOpen(true);
                }
                else
                {
                    showAlerttoDownloadOrOpen(false);
                }
            }
            result += "  ";
        }

        result += info.toString();

        for (int i = 0; i < info.getChildCount(); i++) {
            result += "\n" + toStringHierarchy(info.getChild(i), depth + 1);
        }

        return result;
    }
    private static void debug(Object object) {
        Log.d(TAG, object.toString());
    }


    @Override
    public void onInterrupt() {

    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private void showAlerttoDownloadOrOpen(final Boolean openApp)
    {

        String message;
        if(openApp)
        {
            message = "This website has an app which is already installed in your device. Open app?";
        }
        else
        {
            message = "This website has an app. Download app?";
        }
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getApplicationContext());
        builder1.setMessage(message);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                        if(openApp) {
                            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.application.zomato");
                            if (launchIntent != null) {
                                startActivity(launchIntent);//null pointer check in case package name was not found
                            }
                        }
                        else
                        {
                            final String appPackageName = "com.application.zomato"; // package name of the app
                            try {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                            }
                        }
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
