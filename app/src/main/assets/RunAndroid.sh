#!/system/bin/sh

trap 'echo "exit111...." >> /data/data/com.jiangboh.bti.yingyanclient/sshlog' 0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 28 29 30 31

echo "begin...." >> /data/data/com.jiangboh.bti.yingyanclient/sshlog
nohup sh /data/data/com.jiangboh.bti.yingyanclient/CheckProcess.sh > /dev/null 2>&1 &
echo "end...." >> /data/data/com.jiangboh.bti.yingyanclient/sshlog