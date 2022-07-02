#!/usr/bin/env bash

function find_idle_profile()
{
    CURRENT_PORT=`cat /etc/nginx/conf.d/service-url.inc | cut -c 37-40`

    if [ ${CURRENT_PORT} == 8081 ]
    then
      IDLE_PROFILE=server2
    else
      IDLE_PROFILE=server1
    fi

    echo "${IDLE_PROFILE}"
}
# 쉬고 있는 profile의 port 찾기a
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == server1 ]
    then
      echo "8081"
    else
      echo "8082"
    fi
}