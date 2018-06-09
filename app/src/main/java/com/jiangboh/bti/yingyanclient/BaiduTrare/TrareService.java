package com.jiangboh.bti.yingyanclient.BaiduTrare;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.jiangboh.bti.yingyanclient.PublicUnit.MusicPlayer;
import com.jiangboh.bti.yingyanclient.PublicUnit.StaticParam;

import static android.content.ContentValues.TAG;

/**
 * Created by admin on 2018-6-2.
 */
public class TrareService extends Service {

    private static final int FORESERVICE_PID = android.os.Process.myPid();
    //private AssistServiceConnection mConnection;

    @Override
    public IBinder onBind(Intent intent) {
        Log.w(TAG, "in onBind");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w(TAG, "in onCreate");

        // 初始化轨迹服务客户端
        new MyBaiduTrare(getApplicationContext()).run();

        //接收系统推送消息
        PushManager.startWork(getApplicationContext(), PushConstants.LOGIN_TYPE_API_KEY, StaticParam.MY_KEY);

        //屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity
        final ScreenManager screenManager = ScreenManager.getInstance(TrareService.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.w(TAG, "in onScreenOn");
                screenManager.finishActivity();
                screenManager.startMainActivity(true);
            }
            @Override
            public void onScreenOff() {
                Log.w(TAG, "in onScreenOff");
                //screenManager.startActivity();
                screenManager.startMainActivity(false);
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w(TAG, "in onStartCommand");

        //播放音乐
        MusicPlayer.play(this);

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
        Log.w(TAG, "in onDestroy");

        // 如果Service被杀死，干掉通知
        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
            NotificationManager mManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            mManager.cancel(FORESERVICE_PID);
        }*/

        MusicPlayer.recycle();

        Log.d(TAG,"DaemonService---->onDestroy，前台service被杀死");
        // 重启自己
        Intent intent = new Intent(getApplicationContext(),TrareService.class);
        startService(intent);
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
