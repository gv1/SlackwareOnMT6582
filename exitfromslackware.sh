#/system/bin/sh
export PATH=/data/local/tmp/bin:$PATH

killall -9 fluxbox
killall -9 Xorg
killall -9 wbar

cd /data/local/tmp/slackware

for d in sys proc dev system usr bin sbin etc \
	tmp boot home root init lib media mnt opt \
	run sbin srv var android.tmp;
do
	echo "umounting $d"
	mountpoint $d && umount $d
done
mountpoint /data/local/tmp/sdcard && umount /data/local/tmp/sdcard
# setprop ctl.start is not working.
# LD_LIBRARY_PATH= /system/bin/setprop ctl.start surfaceflinger
LD_LIBRARY_PATH= /system/bin/stop
LD_LIBRARY_PATH= /system/bin/start
