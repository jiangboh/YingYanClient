package com.jiangboh.bti.yingyanclient.PublicUnit;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AsyncLogger {
    private static volatile AsyncLogger instance = null;
    private SimpleDateFormat mFormat = null;
    private WriteThread mThread = null;

    private AsyncLogger() {
        mThread = new WriteThread();
        mFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss:SS");
        mThread.start();
    }

    //使用安全的单例模式
    public static AsyncLogger getInstance() {
        if (instance == null) {
            synchronized (AsyncLogger.class) {
                if (instance == null) {
                    instance = new AsyncLogger();
                }
            }
        }
        return instance;
    }

    //外部直接调用该方法打印Exception堆栈
    public static void LoggingE(String tag, String str, Exception e) {
        Log.e(tag, str,e);
        //将Exception的错误信息转换成String
        String log = "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log = str + "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            log = str + " fail to print Exception";
        }
        AsyncLogger.getInstance().Log(tag, str);
    }
    //外部直接调用该方法打印Log
    public static void Logging(String tag, String str) {
        Log.d(tag, str);
        AsyncLogger.getInstance().Log(tag, str);
    }

    public synchronized void Log(String tag, String str) {
        String time = mFormat.format(new Date());
        mThread.enqueue("["+ time + "]" + str);
    }

    //线程保持常在,不工作时休眠,需要工作时再唤醒就可.
    public class WriteThread extends Thread {
        private boolean isRunning = false;
        private String filePath = null;
        private Object lock = new Object();
        private ConcurrentLinkedQueue<String> mQueue = new ConcurrentLinkedQueue<String>();

        public WriteThread() {
            String sdcard = getPath();
            if (sdcard != null)
                filePath = sdcard + "/YingYanLog.txt";
            isRunning = true;
        }

        public String getPath() {
            return exist() ? Environment.getExternalStorageDirectory().toString() : null;
        }

        public boolean exist() {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }

        //将需要写入文本的字符串添加到队列中.线程休眠时,再唤醒线程写入文件
        public void enqueue(String str) {
            mQueue.add(str);
            if (isRunning() == false) {
                awake();
            }
        }

        public boolean isRunning() {
            return isRunning;
        }

        public void awake() {
            synchronized (lock) {
                lock.notify();
            }
        }
        @Override
        public void run() {
            while (true) {
                synchronized (lock) {
                    isRunning = true;
                    while (!mQueue.isEmpty()) {
                        try {
                            //pop出队列的头字符串写入到文件中
                            recordStringLog(mQueue.poll());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    isRunning = false;
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

        public void recordStringLog(String text) {
            File file = new File(filePath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                //append data to file
                FileWriter filerWriter = new FileWriter(file, true);
                BufferedWriter bufWriter = new BufferedWriter(filerWriter);
                bufWriter.write(text);
                bufWriter.newLine();
                bufWriter.close();
                filerWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断日志文件是否存在
         *
         * @return
         */
        public boolean isExitLogFile() {
            File file = new File(filePath);
            if (file.exists() && file.length() > 3) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * 删除日志文件
         */
        public void deleteLogFile() {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
