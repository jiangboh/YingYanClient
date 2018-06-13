package com.jiangboh.bti.yingyanclient.BaiduTrare;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.jiangboh.bti.yingyanclient.MyBroadcastReceiver;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;

/**
 * Created by admin on 2018-6-11.
 */

public class RegisterBroadcast {
    private static boolean isRegisterReceiver = false;

    public static void register(TrareService service) {
        if (!isRegisterReceiver) {
            isRegisterReceiver = true;

            MyBroadcastReceiver sr = new MyBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            //filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);

            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);

            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

            filter.setPriority(1000);
            MyFunction.MyPrint("注册屏幕解锁、加锁广播接收者...");
            service.registerReceiver(sr, filter);
        }
    }
}