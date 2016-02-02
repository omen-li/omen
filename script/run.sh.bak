#!/bin/bash
export LANG=zh_CN.UTF-8
. appenv
. classpath

pid=`ps -wwef|grep "Dflag=${SERVICE_ID}"|grep -v grep`
if [ -n "${pid}" ]
then
    echo "${SERVICE_NAME} is already running. Do not start server twice!"
else
    nohup java -Xms128m -Xmx256m  -Dflag=${SERVICE_ID} -Djava.ext.dirs=../lib -cp ${APPCLASSPATH}  ${MainClass}  &
    sleep 1
	. state.sh
fi
	. runv.sh
