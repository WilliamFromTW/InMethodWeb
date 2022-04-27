#!/bin/sh
rm -rf /root/InMethodWeb
git lfs clone --depth=1 https://gitblit.hlmt.com.tw/r/it/web/InMethodWeb.git
cd /root/InMethodWeb
gradle war
if [ -f "/root/InMethodWeb/build/libs/InMethodWeb.war" ]
then
  /usr/local/apache-tomcat/bin/shutdown.sh
  mkdir /usr/local/apache-tomcat/webapps/InMethodWeb
  cd /usr/local/apache-tomcat/webapps/InMethodWeb
  rm -rf /usr/local/apache-tomcat/webapps/InMethodWeb/*
  jar vxf /root/InMethodWeb/build/libs/InMethodWeb.war
  sleep 5s
  /usr/local/apache-tomcat/bin/startup.sh
  sleep 20s
  /usr/local/apache-tomcat/bin/shutdown.sh
  sleep 5s
  /usr/local/apache-tomcat/bin/startup.sh  
else
  echo "fail!"
fi
chmod +x /usr/local/apache-tomcat/webapps/InMethodWeb/scripts/*
crontab /usr/local/apache-tomcat/webapps/InMethodWeb/scripts/crontab
mkdir -p /opt
/usr/bin/cp -rf /root/InMethodWeb/opt/* /opt


