#!/bin/sh

PORT=42000

source kill_selflet.sh

cd RedsMiddleware
screen -d -m mvn exec:java -Dexec.args=$PORT
cd ../SelfletRequestDispatcher 

while ! nc -vz localhost $PORT; do sleep 3; done
screen -d -m mvn jetty:run
cd ..

WEB_SERVER_PORT=8080
while ! nc -vz localhost $WEB_SERVER_PORT; do sleep 3; done

MY_IP=`curl http://169.254.169.254/latest/meta-data/public-ipv4`
wget -qO- localhost:8080/associateIPs?dispatcher=$MY_IP &> /dev/null

echo "Broker started at "$MY_IP