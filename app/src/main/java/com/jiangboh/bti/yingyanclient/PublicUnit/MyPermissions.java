package com.jiangboh.bti.yingyanclient.PublicUnit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2018-6-1.
 */
public class MyPermissions {

    private Activity activity;

    public MyPermissions(Activity activity)
    {
        this.activity = activity;
    }

    private boolean isNeedRequestPermissions(List<String> permissions) {
        // 定位精确位置
        addPermission(permissions, Manifest.permission.ACCESS_FINE_LOCATION);
        // 存储权限
        addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 读取手机状态
        addPermission(permissions, Manifest.permission.READ_PHONE_STATE);
        // 修改WIFI
        //addPermission(permissions, Manifest.permission.CHANGE_WIFI_STATE);

        return permissions.size() > 0;
    }

    private void addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    /**
    * 弹出权限确认对话框
    */
    public boolean show(boolean isCloseActivity)
    {
        boolean isNeedRPermissions = false;
        // 适配android M，检查权限
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNeedRequestPermissions(permissions)) {
            isNeedRPermissions = true;
            activity.requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
        }

        /*if (isCloseActivity && isNeedRPermissions) {
            activity.finish();
        }*/

        return isNeedRPermissions;
    }
}
