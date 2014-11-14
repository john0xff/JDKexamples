#!/bin/bash

#/--------------------------
#/ try to stop entire lampp server - apache php + mysql server
sudo /opt/lampp/lampp stop;
echo "lampp stopped";

#/--------------------------

#/ try to stop apache tomcat
cd /opt/apache-tomcat-7.0.56/bin/

sudo ./shutdown.sh;
echo "tomcat stopped";

#/ to stop tomcat in the bin directory - sudo ./shutdown.sh

#/--------------------------
#/-------------------------- NO-IP
#/ sudo noip2 -C
#/ then it shows me if it's already in use 
#/ if yes then it shows me id of process 
#/ then - sudo kill id of process


#/ echo "no-ip ddns stopped";
