TZ_adjust=2.0;
d=$(date +%s);
t=$(echo "60*60*$TZ_adjust/1" | bc);
echo T$(echo $d+$t | bc ) > /dev/tty.usbmodem621
