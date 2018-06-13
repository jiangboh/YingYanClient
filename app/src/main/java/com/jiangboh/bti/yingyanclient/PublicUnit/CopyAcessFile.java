package com.jiangboh.bti.yingyanclient.PublicUnit;

import android.content.Context;

import java.io.IOException;

import static com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction.getInnerSDCardPath;

/**
 * Created by admin on 2018-6-4.
 */

public class CopyAcessFile {
    private Context mContext;

    public CopyAcessFile(Context pContext)
    {
        this.mContext = pContext;
    }

    private void RunCommand()    {
        RunExeCommand command = new RunExeCommand(false) ;
        StringBuilder log = new StringBuilder();
        String inPath = getInnerSDCardPath();
        inPath = "/data/data/com.jiangboh.bti.yingyanclient";
        MyFunction.MyPrint("内置SD卡路径：" + inPath + "\r\n");

        //command.run( "mkdir " + inPath + "/bb",3000);

        //command.run( "mount  -t tmpfs -o size=5m  tmpfs " + inPath + "/bb",3000);

        String exe_path = inPath + "/RunAndroid.sh";


        try {
            MyFunction.copyBigDataToSD(mContext,"RunAndroid.sh",inPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            MyFunction.copyBigDataToSD(mContext,"CheckProcess.sh",inPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //File exe_file = new File(exe_path);
        //exe_file.setExecutable(true, true);

        command.run( "chmod 777 " + exe_path ,3000);
        command.run( "chmod 777 " + inPath + "/CheckProcess.sh" ,3000);

        command.run( "nohup sh " + exe_path + " > /dev/null 2>&1 &",3000);

    }
}
