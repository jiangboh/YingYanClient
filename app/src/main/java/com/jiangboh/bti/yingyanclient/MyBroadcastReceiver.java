package com.jiangboh.bti.yingyanclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jiangboh.bti.yingyanclient.BaiduTrare.TrareService;

/**
 * Created by admin on 2018-6-5.
 */

public class MyBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive (Context context, Intent intent) {
        Log.d(TAG,"BroadcastReceiver:" + intent.getAction());

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent intent1 = new Intent();
            intent1.setClass(context.getApplicationContext(), TrareService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startService(intent);
            MainActivity.actionToMainActivity(context);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.w(TAG, "in onScreenOn");
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.w(TAG, "in onScreenOff");
        }
    }

}
