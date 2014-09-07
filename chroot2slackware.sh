#!/system/bin/sh
export PATH=/data/local/tmp/bin:$PATH
test -d /data/local/tmp/sdcard || mkdir /data/local/tmp/sdcard
mountpoint /data/local/tmp/sdcard || mount -t ext4 /dev/block/mmcblk1p1 /data/local/tmp/sdcard
test -d /data/local/tmp/slackware || mkdir /data/local/tmp/slackware
cd /data/local/tmp/slackware

for d in sys proc dev system usr bin sbin \
  etc  tmp boot home root lib \
  media mnt opt  run sbin srv var android.tmp ;
do
	test -d $d || mkdir $d
	case $d in
		proc)
			mountpoint proc || mount -t proc none proc/
		;;
		sys)
			mountpoint sys  || mount -t sysfs sys sys/
		;;
		dev)
			mountpoint dev  || mount -o bind /dev dev/
		;;
		system)
			mountpoint system || mount -o bind /system system
		;;
		android.tmp)	
			mountpoint android.tmp || mount -o bind /data/local/tmp android.tmp
		;;
		*)
			mountpoint $d || mount -o bind /data/local/tmp/sdcard/$d $d
		;;
	esac
done


export PATH=/usr/bin:/bin:/usr/X11R6/bin:$PATH
# chroot . /bin/su -
# if non interactive
# test -z $PS1 || test -x /data/local/tmp/sdcard/init && chroot . su - -c /init
# if interactive
# test -z $PS1 && chroot . su -
chroot . /bin/bash -x <<'EOF'
	su -
	LD_LIBRARY_PATH= /system/bin/setprop ctl.stop surfaceflinger
	export PATH=/opt/local/sw/bin:/usr/bin:/bin:$PATH
	export LD_LIBRARY_PATH=/opt/local/sw/lib:/usr/lib:$LD_LIBRARY_PATH
	killall -9 Xorg
	sleep 1
	# /usr/bin/startx -- /opt/local/sw/bin/Xorg :0 
	/usr/bin/xinit -- /opt/local/sw/bin/Xorg :0 
	wait
EOF
