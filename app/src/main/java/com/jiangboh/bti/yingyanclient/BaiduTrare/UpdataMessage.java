package com.jiangboh.bti.yingyanclient.BaiduTrare;

import com.jiangboh.bti.yingyanclient.PublicUnit.MyFunction;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-6-11.
 */



public class UpdataMessage {
    private static Lock lock = new ReentrantLock();
    public static final int MaxOffset = 30;
    public static final String Action = "action";
    public static final String Desc = "desc";
    public static final String BefreTime = "befreTime";

    //上传消息结构体
    public class MsgText {
        //事件产生时间
        public long timer;
        //向前取坐标
        public boolean befreTime;
        //取坐标的时间范围。单位分钟
        public int offset;

        public String action;
        public String desc;

        public MsgText(String action,String desc,boolean befreTime)
        {
            this.timer = System.currentTimeMillis()/1000;
            this.offset = 1;
            this.action = action;
            this.desc = desc;
            this.befreTime = befreTime;
        }
    }

    private Map<Integer, MsgText> Msg ;

    public UpdataMessage()
    {
        MsgText text = new MsgText("","",false);
        Msg = new HashMap<Integer, MsgText>();
    }

    public void add(Integer tag, String action, String desc,boolean befreTime) {
        MsgText text = new MsgText(action,desc,befreTime);
        this.add(tag,text);
    }
    public void add(Integer tag,MsgText text)
    {
        lock.lock();
        try {
            Msg.put(tag,text);
        }finally {
        }
        MyFunction.MyPrint("添加后记录条数:" + Msg.size());
        lock.unlock();
    }

    public String getAction(Integer tag)
    {
        String action = "";
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                action = m.action;
            }
        }finally {

        }
        lock.unlock();
        return action;
    }

    public String getDesc(Integer tag)
    {
        String desc = "";
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                desc = m.desc;
            }
        }finally {

        }
        lock.unlock();
        return desc;
    }

    public boolean getBefreTime(Integer tag)
    {
        Boolean befreTime = false;
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                befreTime = m.befreTime;
            }
        }finally {

        }
        lock.unlock();
        return befreTime;
    }

    public long getTime(Integer tag)
    {
        long timer = 0;
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                timer = m.timer;
            }
        }finally {

        }
        lock.unlock();
        return timer;
    }

    public int getOffset(Integer tag)
    {
        int offset = 0;
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                offset = m.offset;
            }
        }finally {

        }
        lock.unlock();
        return offset;
    }

    public MsgText getMsgText(Integer tag)
    {
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
        }finally {

        }
        lock.unlock();
        return m;
    }

    public void del(Integer tag)
    {
        lock.lock();
        try {
            Msg.remove(tag);
        }finally {

        }
        MyFunction.MyPrint("剩余记录条数:" + Msg.size());
        lock.unlock();
    }

    public boolean addOffset(Integer tag)
    {
        boolean re = false;
        int offset = 1;
        MsgText m = null;
        lock.lock();
        try {
            m =  Msg.get(tag);
            if (m!=null)
            {
                offset = m.offset;
                if (offset <= MaxOffset) {
                    m.offset = offset + 1;
                    re = true;
                }
            }
        }finally {

        }
        lock.unlock();
        return re;
    }
}
