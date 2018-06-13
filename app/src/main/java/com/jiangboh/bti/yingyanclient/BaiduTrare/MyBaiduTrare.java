package com.jiangboh.bti.yingyanclient.BaiduTrare;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.AddEntityResponse;
import com.baidu.trace.api.entity.AroundSearchResponse;
import com.baidu.trace.api.entity.BoundSearchResponse;
import com.baidu.trace.api.entity.DeleteEntityResponse;
import com.baidu.trace.api.entity.DistrictSearchResponse;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.entity.PolygonSearchResponse;
import com.baidu.trace.api.entity.SearchResponse;
import com.baidu.trace.api.entity.UpdateEntityRequest;
import com.baidu.trace.api.entity.UpdateEntityResponse;
import com.baidu.trace.api.track.AddPointRequest;
import com.baidu.trace.api.track.AddPointResponse;
import com.baidu.trace.api.track.AddPointsResponse;
import com.baidu.trace.api.track.ClearCacheTrackResponse;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.QueryCacheTrackResponse;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.LatLng;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.Point;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;
import com.jiangboh.bti.yingyanclient.PublicUnit.StaticParam;
import com.jiangboh.bti.yingyanclient.PublicUnit.SystemUtil;
import com.jiangboh.bti.yingyanclient.R;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by admin on 2018-6-1.
 */

public class MyBaiduTrare {
        public static final int STOP_TRACE_SERVER = 1;
        public static int ADD_POINTS = 2000;

        private Context context;

        //是否已获取到天气信息
        public static boolean isGetWeather = false;

        private boolean isTraceStarted = false;
        private boolean isGatherStarted = false;
        private boolean isRegisterReceiver = false;
        private boolean isStartStatus = true;

        private PowerManager powerManager = null;

        private PowerManager.WakeLock wakeLock = null;

        private TrackReceiver trackReceiver = null;

        // 轨迹服务ID
        private long serviceId = 200567;
        // 设备标识
        private String entityName = StaticParam.mPhone;
        // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
        private boolean isNeedObjectStorage = false;

        private Trace mTrace;
        private LBSTraceClient mTraceClient;

        private OnTraceListener mTraceListener;
        private OnTrackListener mTrackListener;
        private OnEntityListener mEntityListener;

        public MyBaiduTrare(Context context)
        {
            this.context = context;
            //this.mTraceClient = new LBSTraceClient(context);
        }

        public LBSTraceClient get_mTraceClient()
        {
            return this.mTraceClient;
        }

        private void InitTraceListene()
        {
            mEntityListener = new OnEntityListener() {

                //添加Entity回调接口
                @Override
                public void onAddEntityCallback(AddEntityResponse response){
                    MyFunction.MyPrint(String.format("onAddEntityCallback, errorNo:%d, message:%s ", response.status,response.message));
                }
                //更新Entity回调接口
                @Override
                public void onUpdateEntityCallback(UpdateEntityResponse response) {
                    MyFunction.MyPrint(String.format("onUpdateEntityCallback, errorNo:%d, message:%s ", response.status,response.message));
                }
               /* @Override
                public void onReceiveLocation(TraceLocation location){
                    if (location.status ==0 ) {
                        MyFunction.MyPrint( "onLatestPointCallback 经度：" + location.getLongitude() + "；维度：" + location.getLatitude());
                        new GetWeather(context,mTrace).getWeatherInfo(location.getLongitude(),location.getLatitude());

                        isStartStatus = false;
                        mTraceClient.stopGather(mTraceListener);
                        mTraceClient.stopTrace(mTrace, mTraceListener);
                    }
                    else
                    {
                        MyFunction.MyPrint( "未获取到位置坐标。。。");
                    }
                }*/

                //删除Entity回调接口
                @Override
                public void onDeleteEntityCallback(DeleteEntityResponse response){}

                //查询Entity列表回调接口
                @Override
                public void onEntityListCallback(EntityListResponse response){}

                //搜索Entity回调接口
                @Override
                public void onSearchEntityCallback(SearchResponse response){}

                //矩形搜索回调接口
                @Override
                public void onBoundSearchCallback(BoundSearchResponse response){}

                //周边搜索回调接口
                @Override
                public void onAroundSearchCallback(AroundSearchResponse response){}

                //多边形搜索回调接口
                @Override
                public void onPolygonSearchCallback(PolygonSearchResponse response){}

                //行政区搜索回调接口
                @Override
                public void onDistrictSearchCallback(DistrictSearchResponse response){}

                //实时定位回调接口
                @Override
                public void onReceiveLocation(TraceLocation location){
                    MyFunction.MyPrint( "onLatestPointCallback 经度：" + location.getLongitude() + "；维度：" + location.getLatitude());
                }

            };

            mTrackListener = new OnTrackListener() {
                @Override
                public void onLatestPointCallback(LatestPointResponse response) {
                    if (response.getStatus() ==0 ) {
                        LatLng latLng = response.getLatestPoint().getLocation();
                        if (response.getTag() == STOP_TRACE_SERVER)
                        {
                            //isGetWeather = true;
                            MyFunction.MyPrint( "onLatestPointCallback 经度：" + latLng.getLatitude() + "；维度：" + latLng.getLongitude());
                            new GetWeather(context, mTrace).getWeatherInfo(latLng.getLongitude(), latLng.getLatitude());
                            isStartStatus = false;
                            mTraceClient.stopGather(mTraceListener);
                            mTraceClient.stopTrace(mTrace, mTraceListener);
                        } else if (response.getTag() >= ADD_POINTS){
                            String action = StaticParam.updataMessage.getAction(response.getTag());
                            String desc = StaticParam.updataMessage.getAction(response.getTag());
                            if (!action.isEmpty() && !desc.isEmpty()) {
                                Map<String, String> columns = new HashMap<String, String>();

                                columns.put("action", action);
                                columns.put("desc", desc);

                                AddPointRequest requst = new AddPointRequest();
                                requst.setServiceId(serviceId);
                                requst.setEntityName(entityName);
                                Point point = new Point(latLng, CoordType.bd09ll);
                                point.setLocTime(System.currentTimeMillis() / 1000);
                                requst.setPoint(point);
                                requst.setColumns(columns);
                                MyFunction.MyPrint("上传动作：" + action + ";动作描述：" + desc);
                                mTraceClient.addPoint(requst, mTrackListener);
                            }
                            StaticParam.updataMessage.del(response.getTag());

                        }
                    }
                    else
                    {
                        MyFunction.MyPrint( "未获取到位置坐标。。。");
                    }
                }

                @Override
                public void onAddPointCallback(AddPointResponse response) {
                    MyFunction.MyPrint(String.format("onAddPointCallback, errorNo:%d, message:%s " + System.currentTimeMillis()/1000, response.status, response.message));
                }
                public void onAddPointsCallback(AddPointsResponse response){}
                public void onHistoryTrackCallback(HistoryTrackResponse response){}
                public void onDistanceCallback(DistanceResponse response){}
                public void onQueryCacheTrackCallback(QueryCacheTrackResponse response){}
                public void onClearCacheTrackCallback(ClearCacheTrackResponse response){}
            };

            // 初始化轨迹服务监听器
            mTraceListener = new OnTraceListener() {
                /*
                 * 绑定服务回调接口
                 * @param errorNo  状态码
                 * @param message 消息
                 *                <p>
                 *                <pre>0：成功 </pre>
                 *                <pre>1：失败</pre>
                 */
                @Override
                public void onBindServiceCallback(int errorNo, String message) {
                    MyFunction.MyPrint(String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
                }


                /*
                 * 开启服务回调接口
                 * @param errorNo 状态码
                 * @param message 消息
                 *                <p>
                 *                <pre>0：成功 </pre>
                 *                <pre>10000：请求发送失败</pre>
                 *                <pre>10001：服务开启失败</pre>
                 *                <pre>10002：参数错误</pre>
                 *                <pre>10003：网络连接失败</pre>
                 *                <pre>10004：网络未开启</pre>
                 *                <pre>10005：服务正在开启</pre>
                 *                <pre>10006：服务已开启</pre>
                 */
                @Override
                public void onStartTraceCallback(int errorNo, String message) {
                    if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                        isTraceStarted = true;
                    }

                    MyFunction.MyPrint(String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
                }


                /*
                * 停止服务回调接口
                * @param errorNo 状态码
                * @param message 消息
                *                <p>
                *                <pre>0：成功</pre>
                *                <pre>11000：请求发送失败</pre>
                *                <pre>11001：服务停止失败</pre>
                *                <pre>11002：服务未开启</pre>
                *                <pre>11003：服务正在停止</pre>
                */
                @Override
                public void onStopTraceCallback(int errorNo, String message) {
                    if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                        isTraceStarted = false;
                        isGatherStarted = false;
                        //unregisterPowerReceiver();
                    }
                    MyFunction.MyPrint(String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message));
                }


                /*
                * 开启采集回调接口
                * @param errorNo 状态码
                * @param message 消息
                *                <p>
                *                <pre>0：成功</pre>
                *                <pre>12000：请求发送失败</pre>
                *                <pre>12001：采集开启失败</pre>
                *                <pre>12002：服务未开启</pre>
                */
                @Override
                public void onStartGatherCallback(int errorNo, String message) {
                    if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                        isGatherStarted = true;

                        UpdateEntityRequest entityRequest = new UpdateEntityRequest(0x11,serviceId, entityName);
                        entityRequest.setEntityDesc(StaticParam.mPhone);
                        mTraceClient.updateEntity(entityRequest,mEntityListener);

                    }
                    MyFunction.MyPrint(String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message));
                }


                /*
                * 停止采集回调接口
                * @param errorNo 状态码
                * @param message 消息
                *                <p>
                *                <pre>0：成功</pre>
                *                <pre>13000：请求发送失败</pre>
                *                <pre>13001：采集停止失败</pre>
                *                <pre>13002：服务未开启</pre>
                */
                @Override
                public void onStopGatherCallback(int errorNo, String message) {
                    if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                        isGatherStarted = false;
                    }
                    MyFunction.MyPrint(String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message));
                }

                /*
                * 推送消息回调接口
                *
                * @param messageType 状态码
                * @param pushMessage 消息
                *                  <p>
                *                  <pre>0x01：配置下发</pre>
                *                  <pre>0x02：语音消息</pre>
                *                  <pre>0x03：服务端围栏报警消息</pre>
                *                  <pre>0x04：本地围栏报警消息</pre>
                *                  <pre>0x05~0x40：系统预留</pre>
                *                  <pre>0x41~0xFF：开发者自定义</pre>
                */
                @Override
                public void onPushCallback(byte messageType, PushMessage pushMessage) {

                /*if (messageType < 0x03 || messageType > 0x04) {
                    viewUtil.showToast(TracingActivity.this, pushMessage.getMessage());
                    return;
                }
                FenceAlarmPushInfo alarmPushInfo = pushMessage.getFenceAlarmPushInfo();
                if (null == alarmPushInfo) {
                    viewUtil.showToast(TracingActivity.this,
                            String.format("onPushCallback, messageType:%d, messageContent:%s ", messageType,
                                    pushMessage));
                    return;
                }
                StringBuffer alarmInfo = new StringBuffer();
                alarmInfo.append("您于")
                        .append(CommonUtil.getHMS(alarmPushInfo.getCurrentPoint().getLocTime() * 1000))
                        .append(alarmPushInfo.getMonitoredAction() == MonitoredAction.enter ? "进入" : "离开")
                        .append(messageType == 0x03 ? "云端" : "本地")
                        .append("围栏：").append(alarmPushInfo.getFenceName());

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    Notification notification = new Notification.Builder(trackApp)
                            .setContentTitle(getResources().getString(R.string.alarm_push_title))
                            .setContentText(alarmInfo.toString())
                            .setSmallIcon(R.mipmap.icon_app)
                            .setWhen(System.currentTimeMillis()).build();
                    notificationManager.notify(notifyId++, notification);
                }*/
                }

                @Override
                public void onInitBOSCallback(int errorNo, String message) {
                    /*viewUtil.showToast(MainActivity.this,
                            String.format("onInitBOSCallback, errorNo:%d, message:%s ", errorNo, message));*/
                }


            };
        }

        public void run() {
            String eName = null;
            eName = SystemUtil.getIMEI(context.getApplicationContext());
            if (eName != null)
            {
                entityName = eName;
            }
            // 初始化轨迹服务
            mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);

            //设置通知栏消息
            setNotification(StaticParam.NotificationText);

            //设置回调方式
            InitTraceListene();

            // 初始化轨迹服务客户端
            mTraceClient = new LBSTraceClient(context);

            // 定位周期(单位:秒)
            int gatherInterval = StaticParam.gatherInterval;
            // 打包回传周期(单位:秒)
            int packInterval = StaticParam.packInterval;
            // 设置定位和打包周期
            mTraceClient.setInterval(gatherInterval, packInterval);

            // 开启服务
            mTraceClient.startTrace(mTrace, mTraceListener);

            // 开启采集
            //mTraceClient.startGather(mTraceListener);

            new MySetNotificationThread().start();
            new MyGetWeatherThread().start();

            //AddPointRequest r = new AddPointRequest();
        }

        public void setNotification(String text) {
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
            mBuilder.setContentTitle("微信实时天气");//设置通知栏标题
            mBuilder.setContentText(text);//设置通知栏显示内容
            //mBuilder.setContentIntent(context.getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) ;//设置通知栏点击意图
            //mBuilder.setNumber(number) //设置通知集合的数量
            //mBuilder.setTicker("测试通知来啦"); //通知首次出现在通知栏，带上升动画效果的
            mBuilder.setWhen(System.currentTimeMillis());//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);//设置该通知优先级
            //  mBuilder.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
            mBuilder.setOngoing(true);//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
            //mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
            //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

            Notification notification = mBuilder.build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            mTrace.setNotification(notification);
        }



    private class MyStartGather extends Thread {
        public void run() {
            while (true) {
                while (!isGetWeather) {
                    MyFunction.MyPrint( "获取位置信。。。");
                    LocRequest loc = new LocRequest(serviceId);
                    mTraceClient.queryRealTimeLoc(loc, mEntityListener);
                    MyFunction.MySleep(3000);
                    continue;
                }

                if (!isTraceStarted) {
                    MyFunction.MyPrint( "开启服务。。。" );
                    // 开启服务
                    mTraceClient.startTrace(mTrace, mTraceListener);
                    MyFunction.MySleep(3000);
                    continue;
                }
                if (!isGatherStarted) {
                    MyFunction.MyPrint( "开启采集。。。");
                    // 开启采集
                    mTraceClient.startGather(mTraceListener);
                    MyFunction.MySleep(3000);
                    continue;
                }

                MyFunction.MySleep(3000);
            }
        }
    }


    private class MySetNotificationThread extends Thread {
        public void run() {
            while (true) {
                if (!isTraceStarted && isGetWeather) {
                    MyFunction.MyPrint( "重新设置通知栏:" + StaticParam.NotificationText);
                    setNotification(StaticParam.NotificationText);
                    isStartStatus = true;
                }
                if (isStartStatus && !isTraceStarted)
                {
                    // 开启服务
                    mTraceClient.startTrace(mTrace, mTraceListener);
                }
                if (isStartStatus && isTraceStarted && !isGatherStarted)
                {
                    // 开启采集
                    mTraceClient.startGather(mTraceListener);
                }

                MyFunction.MySleep(3000);
            }
        }
    }

    private class MyGetWeatherThread extends Thread {
            private int timer = 10 * 1000;
             public void run(){
                while(true)
                {
                    MyFunction.MyPrint("延时时间:"+ timer);
                    MyFunction.MySleep(timer);

                    if (isTraceStarted && isGatherStarted) {
                        LatestPointRequest request = new LatestPointRequest(STOP_TRACE_SERVER,serviceId,entityName);
                        mTraceClient.queryLatestPoint(request,mTrackListener);
                        MyFunction.MySleep(60 * 1000);
                    }

                    if (isGetWeather) {
                        timer = StaticParam.WeatherChangeInterval * 1000;
                    }
                    else
                    {
                        timer = 60 * 1000;
                    }
                }
            }
        }

    public void SendAddPoins(int requestTag)
    {
        LatestPointRequest request = new LatestPointRequest(requestTag,serviceId,entityName);
        mTraceClient.queryLatestPoint(request,mTrackListener);
    }
    /**
     * 注册广播（电源锁、GPS状态）
     */
    /*private void registerReceiver() {
        if (isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        registerReceiver(trackReceiver, filter);
        isRegisterReceiver = true;

    }

    private void unregisterPowerReceiver() {
        if (!isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            unregisterReceiver(trackReceiver);
        }
        isRegisterReceiver = false;
    }*/
}
