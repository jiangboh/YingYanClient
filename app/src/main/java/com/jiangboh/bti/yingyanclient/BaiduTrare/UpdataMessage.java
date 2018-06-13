package com.jiangboh.bti.yingyanclient.BaiduTrare;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by admin on 2018-6-11.
 */



public class UpdataMessage {
    private static Lock lock = new ReentrantLock();

    //上传消息结构体
    public class MsgText {
        public String action;
        public String desc;

        public MsgText(String action,String desc)
        {
            this.action = action;
            this.desc = desc;
        }
    }

    private Map<Integer, MsgText> Msg ;

    public UpdataMessage()
    {
        MsgText text = new MsgText("","");
        Msg = new HashMap<Integer, MsgText>();
    }

    public void add(Integer tag, String action, String desc) {
        MsgText text = new MsgText(action,desc);
        this.add(tag,text);
    }
    public void add(Integer tag,MsgText text)
    {
        lock.lock();
        try {
            Msg.put(tag,text);
        }finally {
        }
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

    public void del(Integer tag)
    {
        lock.lock();
        try {
            Msg.remove(tag);
        }finally {

        }
        lock.unlock();
    }
}
