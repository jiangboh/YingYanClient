package com.jiangboh.bti.yingyanclient.BaiduTrare;

import android.app.Activity;
import android.content.Context;

import com.jiangboh.bti.yingyanclient.MainActivity;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 2018-6-4.
 */

public class ScreenManager {
    private Context mContext;

    private WeakReference<Activity> mActivityWref;

    public static ScreenManager gDefualt;

    public static ScreenManager getInstance(Context pContext) {
        if (gDefualt == null) {
            gDefualt = new ScreenManager(pContext.getApplicationContext());
        }
        return gDefualt;
    }
    private ScreenManager(Context pContext) {
        this.mContext = pContext;
    }

    public void setActivity(Activity pActivity) {
        mActivityWref = new WeakReference<Activity>(pActivity);
    }

    /*public void startLiveActivity() {
        LiveActivity.actionToLiveActivity(mContext);
    }*/

    public void startMainActivity(boolean isClose) {
        MainActivity.actionToMainActivity(mContext,isClose);
    }

    public void finishActivity() {
        //结束掉LiveActivity
        if (mActivityWref != null) {
            Activity activity = mActivityWref.get();
            if (activity != null) {
                activity.finish();
            }
        }
    }

}
