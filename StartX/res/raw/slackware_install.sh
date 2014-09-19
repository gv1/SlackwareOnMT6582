#############################################################################
# Script for installing Slackware packages in the distribution
# Portions of this script are taken and modifed from /usr/lib/setup directory 
# in slackware installer
# check /usr/lib/setup/setup, slackinstall
#
#
#
#
# Sep 2014
#
#
#############################################################################

export PATH=/sbin:/bin:/usr/lib/setup 
#/xbin

# (cd bin ; ./busybox --install .)
# exit 1

# exit on error
set -e  

# qemu-arm ./bin/busybox sh initrd-kirkwood/usr/lib/setup/installpkg --root test/ /arch.new/slackware.arm/slackware/a/aaa_base-14.1-arm-1.tgz
# INSTALLPKG='qemu-arm ./bin/busybox sh initrd-kirkwood/usr/lib/setup/installpkg' 
# defaults
SLVERSION=14.1
SRCDIR=slackware-$SLVERSION/slackware
SERIES="a l ap n d e x xap xfce" 
TGT_PARTITION=
ROOTDIR=/mnt
FORMAT=

##############
TAGFILE=
# TAGFILE="-tagfile $HOME/slackware-$SLVERSION/tagfiles/$series/tagfile"
#tagfile sample entries from slackware/a/tagfile
# vboot-utils: ADD
# which:REC
# xfsprogs:OPT

# INFOBOX=-infobox
print_usage() {
    # usage
    echo "$0 "
    echo "-s --src"
    echo "-t --target"
    echo "-d --device"
    echo "-i --series"
    echo "-f --format"
    echo "-v --version"
}

print_setup() {
    echo "Installer source :  $SRCDIR"
    echo "Slackware version : SLVERSION=14.1"
    echo "Packeges directory : $SRCDIR"
    echo "Series : $SERIES"
    echo "Target Partition : $TGT_PARTITION"
    echo "Installion root : $ROOTDIR"
}
process_error() {
    if [ $1 == 99 ]; then
	echo "User abort installation, exiting"
	exit 1
    else
	echo "installpkg error #$1" --msgbox \
	    "There was a fatal error attempting to install $2.  The package may \
be corrupt, the installation media may be bad, one of the target \
drives may be full, or something else \
has caused the package to be unable to be read without error.  You \
may hit enter to continue if you wish, but if this is an important \
required package then your installation may not work as-is."
    fi
}

format_target() {
   if [ x$FORMAT = x"" ]; then
       echo "No format specified for target partition, not formatting"
       return
   else 
       echo "Formatting $TGT_PARTITION to $FORMAT"
       test -x `which mkfs.$FORMAT` || \
	   ( echo "mkfs.$FORMAT execution error" ; exit 1)
       mkfs.$FORMAT $TGT_PARTITION
       if [ $? = 1 ]; then
	   echo "Fail to format $TGT_PARTITION"
	   exit 1
       fi       
   fi   
}

preinstall_setup() {
    if [ ! x$TGT_PARTITION = x"" ]; then
       format_target    
       echo "Mounting $TGT_PARTITION at $ROOTDIR"
       mountpoint $ROOTDIR && umount $ROOTDIR
       if [ x$FORMAT = x"" ]; then
   	  mount -t $FORMAT $TGT_PARTITION $ROOTDIR
       else
   	  mount $TGT_PARTITION $ROOTDIR
       fi
       if [ $? = 1 ]; then
    	   echo "Unable to mount $TGT_PARTITION at $ROOTDIR"
	   exit 1
       fi
       echo "Installing Slackware-$SLVERSION from $SRCDIR to $TGT_PARTITION mounted on $ROOTDIR"
    else
       echo "Installing Slackware-$SLVERSION from $SRCDIR to $ROOTDIR"  	
    fi
    echo "All packages in $SERIES will be installed"
    echo "Check $ROOTDIR/tmp/installpkg-report.log for instllation log"
    echo "Please remember to run setup for post installation configuration"
    test -d $ROOTDIR/tmp/ || mkdir $ROOTDIR/tmp
    if [ $? = 1 ]; then
	echo "[ERROR] Unable to create tmp directory in $ROOTDIR"
	exit 1
    fi
}

install_all() {
    for series in $SERIES; do
	for package in $SRCDIR/$series/*.t?z ; do
	    echo "installpkg -root $ROOTDIR $TAGFILE $INFOBOX -priority ADD $package"
	    installpkg -root $ROOTDIR $TAGFILE $INFOBOX -priority ADD $package  2>> $ROOTDIR/tmp/installpkg-report.log
	    ERROR=$?
	    if [ ! $ERROR = 0 ]; then
		process_error $ERROR $package
	    fi
	done
    done
}

# Process command line:
  while [ $# -gt 0 ]; do
   case "$1" in
       "--src")
	   SRCDIR=`echo $2`
	   shift 2 
	   ;;
       "--target")
	   ROOTDIR=`echo $2`
	   shift 2 
	   ;;
       "--device")
	   TGT_PARTITION=`echo $2`
	   shift 2 
	   ;;
       "--series")
	   SERIES=`echo $2`
	   shift 2 
	   ;;
       "--config")
	   CONFIG=`echo $2`
	   shift 2 
	   ;;
       "--format")
	   FORMAT=`echo $2`
	   shift 2
	   ;;
       "--help")
	   print_usage 
	   exit 1
	   ;;
   *)
     echo "Unrecognized option $1" ; shift 1
     print_usage
     exit 1
     ;;
   esac
  done

#

#main
print_setup
preinstall_setup
install_all
