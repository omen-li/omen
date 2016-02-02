#! /bin/bash
. appenv
sleep 1

pid=`ps -wwef|grep "Dflag=${SERVICE_ID}"|grep -v grep`
if [ -n "${pid}" ]
then
    ps -wwef|grep Dflag=${SERVICE_ID}|grep -v grep|grep -v tail|awk 'BEGIN{printf "kill "}{printf "%s ", $2}'|bash
	echo "${SERVICE_NAME} has been stoped!"
else
    echo " ${SERVICE_NAME} is not running."
fi
sleep 1
. run.sh
