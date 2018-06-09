package com.jiangboh.bti.yingyanclient.PublicUnit;

/**
 * Created by admin on 2018-6-5.
 */

public class StaticParam {
    public static final String TAG = "TrareService";
    public static final String MY_KEY = "PL2bxEtzMN81cEXx0zbEHceZ2boDuC3g";

    public static String mPhone = "13760209505";

    public static String NotificationText = "暂无天气信息！";
    //public static MyBaiduTrare myBaiduTrare;

    // 定位周期(单位:秒)
    public static int gatherInterval = 120;
    // 打包回传周期(单位:秒)
    public static int packInterval = 120;

    //天气更新间隔（单位：秒）
    public static final int WeatherChangeInterval = 3600;
}
