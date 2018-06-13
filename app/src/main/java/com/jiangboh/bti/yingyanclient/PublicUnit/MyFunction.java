package com.jiangboh.bti.yingyanclient.PublicUnit;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Date;


/**
 * Created by admin on 2018-6-1.
 */

public class MyFunction {

    /**
     * 获取内置SD卡路径
     * @return
     */
    static public String getInnerSDCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 拷贝资源文件夹下的文件到指定路径
     * AssetsFileName 资源文件名称
     * strOutFilePath 拷贝目的路径
     * @return
     */
    static public void copyBigDataToSD(Context context,String AssetsFileName, String strOutFilePath) throws IOException
    {
        InputStream myInput;
        OutputStream myOutput = new FileOutputStream(strOutFilePath + "/" + AssetsFileName);
        myInput = context.getAssets().open(AssetsFileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while(length > 0)
        {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }

        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    /**
     *
     * @param timer 延时时间，单位ms
     */
    static public void MySleep(int timer)
    {
        try {
            Thread.sleep(timer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void MyPrint(String str){
        if (StaticParam.isDebugMode) {
            AsyncLogger.Logging("YingYanLog",str);
            //System.out.println(str);
        }
    }

    public static final String LongToDateString(Long lTime) {
        Date date = new Date(lTime);
        String sendTime;
        sendTime = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String sendTime = format.format(date); // 得到发送时间
        return sendTime;
    }

    public static final String LongToDateString_2(Long lTime) {
        Date date = new Date(lTime);
        String sendTime =DateUtil.format(date, "yyyyMMdd-HHmmss");
        //SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        //String sendTime = format.format(date); // 得到发送时间
        return sendTime;
    }

    public static final String LongToStimeString(Long lTime) {
        Date date = new Date(lTime);
        String sendTime =DateUtil.format(date, "yyyyMMddHHmmssSSS");
        //SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        //String sendTime = format.format(date); // 得到发送时间
        return sendTime;
    }

    public static final String TimeFormToDay(Long lTime) {
        Date date = new Date(lTime);
        String sendTime =DateUtil.format(date, "yyyy-MM-dd");
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        //String sendTime = format.format(date); // 得到发送时间
        return sendTime;
    }

    public static final long DateStringToLong(String sTime) {
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            //date = formatter.parse(sTime);
            date=DateUtil.parse(sTime, "yyyy-MM-dd HH:mm:ss");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (date == null) {
            return 0;
        } else {
            long currentTime = date.getTime(); // date类型转成long类型
            return currentTime;
        }
    }

    public static final String BooleanToString(boolean bool) {
        if (bool) {
            return "1";
        } else {
            return "0";
        }
    }

    public static final boolean StringToBoolean(String str) {
        return str.compareTo("1") == 0;
    }

    // 检测SD卡是否存在，存在返回TRUE,否则返回FALSE
    public static boolean checkSdcard() {
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 5、创建目录
     */
    public static void createPath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // 删除文件
    public static void deleteFile(String path) {
        File file = new File(path);
        deleteFile(file);
    }

    public static void deleteFile(File file) {
        if (file.exists()) { // 判断文件是否存在
            if (file.isFile()) { // 判断是否是文件
                file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory()) { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                    MyFunction.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
            }
            file.delete();
        } else {
            MyFunction.MyPrint("文件不存在！");
        }

    }

    public static boolean fileIsExists(String fileName) {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }
}
