#!/bin/bash

#/--------------------------LAMPP
#/ try to run entire lampp server - apache php + mysql server
sudo /opt/lampp/lampp start;
echo "lampp started";

#/--------------------------TOMCAT

#/ try to start apache tomcat
cd /opt/apache-tomcat-7.0.56/bin/

sudo ./startup.sh;

echo "tomcat started";
 
#/ comment
#/ to stop tomcat in the bin directory - sudo ./shutdown.sh

#/-------------------------- NO-IP
#/ sudo noip2 -C
#/ then it shows me if it's already in use 
#/ if yes then it shows me id of process 
#/ then - sudo kill id of process

 cd /opt/noip-2.1.9-1/ 
 sudo noip2
 
 echo "no-ip ddns started";























