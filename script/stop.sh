#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)

source ${ABSDIR}/profile.sh

echo "> ABSPATH?? ${ABSPATH}"
echo "> ABSDIR?? ${ABSDIR}"
IDLE_PROFILE=$(find_idle_profile)

echo "> IDLE_PROFILE?? ${IDLE_PROFILE}"

CONTAINER_ID=$(sudo docker container ls -f "name=${IDLE_PROFILE}" -q)

echo "> 컨테이너 ID는 무엇?? ${CONTAINER_ID}"
echo "> 현재 프로필은 무엇?? ${IDLE_PROFILE}"

if [ -z ${CONTAINER_ID} ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
  echo "> docker container prune -f"
  sudo docker container prune -f
  echo "> docker image prune -f"
  sudo docker image prune -f
else
  echo "> docker stop ${IDLE_PROFILE}"
  sudo docker stop ${IDLE_PROFILE}
  echo "> docker rm ${IDLE_PROFILE}"
  sudo docker rm ${IDLE_PROFILE}    # 컨테이너 이름을 지정해서 사용하기 때문에.. 꼭 컨테이너 삭제도 같이 해주셔야 합니다. (나중에 다시 띄울거기 때문에..)
  echo "> docker rmi ${IDLE_PROFILE}"
  sudo docker rmi ${IDLE_PROFILE}
  sleep 5
fi