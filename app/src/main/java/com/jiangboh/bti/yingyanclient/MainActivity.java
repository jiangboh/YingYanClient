package com.jiangboh.bti.yingyanclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.jiangboh.bti.yingyanclient.BaiduTrare.ScreenManager;
import com.jiangboh.bti.yingyanclient.BaiduTrare.TrareService;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyPermissions;
import com.jiangboh.bti.yingyanclient.PublicUnit.SystemUtil;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private String TAG = "MainActivity";
    private void showSystemParameter() {
        String TAG = "系统参数：";
        Log.e(TAG, "手机厂商：" + SystemUtil.getDeviceBrand());
        Log.e(TAG, "手机型号：" + SystemUtil.getSystemModel());
        Log.e(TAG, "手机当前系统语言：" + SystemUtil.getSystemLanguage());
        Log.e(TAG, "Android系统版本号：" + SystemUtil.getSystemVersion());
        Log.e(TAG, "手机IMEI：" + SystemUtil.getIMEI(getApplicationContext()));
    }

    public static void actionToMainActivity(Context pContext) {
        actionToMainActivity(pContext,true);
    }
    public static void actionToMainActivity(Context pContext,boolean isClose) {
        Intent intent = new Intent(pContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isClose", isClose);
        pContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        //放在左上角
        window.setGravity(Gravity.START | Gravity.TOP);
        WindowManager.LayoutParams attributes = window.getAttributes();
        //宽高设计为1个像素
        attributes.width = 1;
        attributes.height = 1;
        //起始坐标
        attributes.x = 0;
        attributes.y = 0;
        window.setAttributes(attributes);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.w(TAG, "in onRequestPermissionsResult");
        this.finish();

        //showSystemParameter();
        SystemUtil.jumpStartInterface(getApplicationContext());

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), TrareService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startService(intent);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //权限确认
        boolean isClose = getIntent().getBooleanExtra("isClose",true);
        if (false == new MyPermissions(this).show(isClose)) {  //如果返回true表示有权限确认，那么权限确认完自动弹出界面
            //showSystemParameter();
            Log.w(TAG, "in oonStart");
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), TrareService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplicationContext().startService(intent);

            if (isClose)
                this.finish();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume is invoke!!!");

        ScreenManager.getInstance(this).setActivity(this);
    }


}