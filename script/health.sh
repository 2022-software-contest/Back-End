#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/switch.sh

IDLE_PORT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PORT"
sleep 10

for RETRY_COUNT in {1..10}
do
  STATUS=$(curl -o /dev/null -w "%{http_code}" http://3.38.152.42:${IDLE_PORT})

  if [ ${STATUS} -ge 400 ]
  then # $up_count >= 1 ("real" 문자열이 있는지 검증)
      echo "> Health check의 응답을 알 수 없거나 혹은 실행 상태가 아닙니다."
      echo "> Health check: ${STATUS}"
  else
      echo "> Health check 성공"
      switch_proxy
      break
  fi

  if [ ${RETRY_COUNT} -eq 10 ]
  then
    echo "> Health check 실패. "
    echo "> 엔진엑스에 연결하지 않고 배포를 종료합니다."
    exit 1
  fi

  echo "> Health check 연결 실패. 재시도..."
  sleep 10
done