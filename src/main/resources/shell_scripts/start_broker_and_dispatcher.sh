#!/bin/sh

PORT=42000

# ------ download dispatcher from git and compile ----------
# downloads source code for all repositories and compiles them
BUILD_ALL=${1:-"notdefined"}

source kill_selflet.sh

GIT_REPO_BASE="git://github.com/nicola-calcavecchia/"

# download only the source code for the selflet to improve performances
GIT_REPO[1]="reds"
GIT_REPO[2]="reds-broker"
GIT_REPO[3]="selflet-request-dispatcher"

for REPO in "${GIT_REPO[@]}"
do
	rm -rf $REPO
	git clone --depth=1 $GIT_REPO_BASE${REPO}".git"
done


for DIRNAME in "${GIT_REPO[@]}"
do
	echo "--> Building ${DIRNAME}"
    cp mavenLifeCyle.sh ${DIRNAME}
    cd $DIRNAME
    source mavenLifeCyle.sh
    cd ..
done

#--------------------------------------------------------

cd reds-broker
screen -d -m mvn exec:java -Dexec.args=$PORT
cd ../selflet-request-dispatcher 

while ! nc -vz localhost $PORT; do sleep 3; done
screen -d -m mvn jetty:run
cd ..

WEB_SERVER_PORT=8080
while ! nc -vz localhost $WEB_SERVER_PORT; do sleep 3; done

MY_IP=`curl http://169.254.169.254/latest/meta-data/public-ipv4`
wget -qO- localhost:8080/associateIPs?dispatcher=$MY_IP &> /dev/null

echo "Broker started at "$MY_IP