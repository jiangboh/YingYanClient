package com.jiangboh.bti.yingyanclient.PublicUnit;

import com.jiangboh.bti.yingyanclient.BaiduTrare.UpdataMessage;

/**
 * Created by admin on 2018-6-5.
 */

public class StaticParam {
    //是否为调式模式
    public static final boolean isDebugMode = true;

    public static final String TAG = "TrareService";
    public static final String MY_KEY = "PL2bxEtzMN81cEXx0zbEHceZ2boDuC3g";

    public static String mPhone = "13760209505";

    public static String NotificationText = "暂无天气信息！";
    //public static MyBaiduTrare myBaiduTrare;

    // 定位周期(单位:秒)
    public static int gatherInterval = 15;
    // 打包回传周期(单位:秒)
    public static int packInterval = 60;

    //天气更新间隔（单位：秒）
    public static final int WeatherChangeInterval = 3600;

    //上传消息定义
    public static UpdataMessage updataMessage = new UpdataMessage();
}
