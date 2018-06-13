package com.jiangboh.bti.yingyanclient.BaiduTrare;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.PowerManager;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jiangboh.bti.yingyanclient.PublicUnit.MusicPlayer;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;
import com.jiangboh.bti.yingyanclient.PublicUnit.StaticParam;

/**
 * Created by admin on 2018-6-2.
 */
public class TrareService extends Service {

    private static Integer requestTag = MyBaiduTrare.ADD_POINTS;

    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock mWifiLock;

    private static final int FORESERVICE_PID = android.os.Process.myPid();
    //private AssistServiceConnection mConnection;
    private MyBaiduTrare myBaiduTrare;

    @Override
    public IBinder onBind(Intent intent) {
        MyFunction.MyPrint( "in onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyFunction.MyPrint( "in onCreate");

        //注删广播息息
        RegisterBroadcast.register(this);

        // 初始化轨迹服务客户端
        myBaiduTrare = new MyBaiduTrare(getApplicationContext());
        myBaiduTrare.run();

        //接收系统推送消息
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, StaticParam.MY_KEY);

        //播放音乐
        MusicPlayer.play(this);

        //屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity
        final ScreenManager screenManager = ScreenManager.getInstance(TrareService.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                MyFunction.MyPrint( "in onScreenOn");
                releaseWakeLock();
                screenManager.finishActivity();
                screenManager.startMainActivity(true);
            }
            @Override
            public void onScreenOff() {
                MyFunction.MyPrint( "in onScreenOff");
                acquireWakeLock();
                //screenManager.startActivity();
                screenManager.startMainActivity(false);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MyFunction.MyPrint( "in onStartCommand");

        String action = intent.getStringExtra("action");
        String desc = intent.getStringExtra("desc");
        if (action != null && desc != null) {
            MyFunction.MyPrint("收到广播: 动作：" + action + ";描述：" + desc);
            if (!action.isEmpty() && !desc.isEmpty()) {
                StaticParam.updataMessage.add(requestTag, action, desc);
                myBaiduTrare.SendAddPoins(requestTag);
                requestTag++;
            }
        }

        /*
        START_STICKY
        如果系统在onStartCommand返回后被销毁，系统将会重新创建服务并依次调用onCreate和onStartCommand（注意：根据测试Android2.3.3以下版本只会调用onCreate根本不会调用onStartCommand，Android4.0可以办到），这种相当于服务又重新启动恢复到之前的状态了）。
        START_NOT_STICKY
        如果系统在onStartCommand返回后被销毁，如果返回该值，则在执行完onStartCommand方法后如果Service被杀掉系统将不会重启该服务。
        START_REDELIVER_INTENT
        START_STICKY的兼容版本，不同的是其不保证服务被杀后一定能重启。
        */
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyFunction.MyPrint( "in onDestroy");

        // 如果Service被杀死，干掉通知
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(FORESERVICE_PID);
        }*/

        MusicPlayer.recycle();

        MyFunction.MyPrint("DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(),TrareService.class);
        startService(intent);
    }

    private void acquireWakeLock() {
        if (wakeLock == null) {
            // MyFunction.MyPrint(d("Acquiring wake lock");
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, this.getClass().getCanonicalName());
            wakeLock.acquire();
        }
        // Create a wifi lock
        if (mWifiLock == null) {
            @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
            mWifiLock = wifiManager.createWifiLock("CBSRadioPlayer");
            mWifiLock.setReferenceCounted(true);
            // Acquire wifi lock
            mWifiLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
        if (mWifiLock != null && mWifiLock.isHeld()) {
            mWifiLock.release();
            mWifiLock = null;
        }

    }

    //设置通知栏通知
    //API 18以下，直接发送Notification并将其置为前台
        /*if (Build.VERSION.SDK_INT <Build.VERSION_CODES.JELLY_BEAN_MR2)
            startForeground(FORESERVICE_PID, new Notification());
        else {
            //API 18以上，发送Notification并将其置为前台后，启动InnerService
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(FORESERVICE_PID, builder.build());
            startService(new Intent(this, InnerService.class));
        }*/


    /*public  static class  InnerService extends Service{
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
        @Override
        public void onCreate() {
            super.onCreate();
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(FORESERVICE_PID, builder.build());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(FORESERVICE_PID);
                    stopSelf();
                }
            },100);

        }
    }*/



}
