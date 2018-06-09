package com.jiangboh.bti.yingyanclient.PublicUnit;

import android.content.Context;
import android.os.Environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
}
