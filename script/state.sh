#!/bin/bash

. appenv

pid=`ps -wwef|grep "Dflag=${SERVICE_ID}"|grep -v grep`
if [ -n "${pid}" ]
then
    echo "${SERVICE_NAME} Start Successed!."
else
    echo "${SERVICE_NAME} Is Down!."
fi
