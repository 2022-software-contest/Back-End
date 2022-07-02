#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PORT=$(find_idle_port)
REPOSITORY=/home/ec2-user

echo "> Build 파일 복사"
echo "> cp $ABSDIR/../build/libs/sw-contest-0.0.1-SNAPSHOT.jar $REPOSITORY"

cp $ABSDIR/../build/libs/sw-contest-0.0.1-SNAPSHOT.jar $REPOSITORY      # 새로운 jar file 계속 덮어쓰기

echo "> 새 어플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)

echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"

chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"

IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME 를 profile=$IDLE_PROFILE 로 실행합니다."

cd $REPOSITORY

sudo docker build --build-arg PORT=$IDLE_PORT -t $IDLE_PROFILE ./
sudo docker run -it --name "$IDLE_PROFILE" -d -p $IDLE_PORT:$IDLE_PORT $IDLE_PROFILE