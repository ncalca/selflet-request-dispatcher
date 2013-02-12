#!/bin/sh

# downloads source code for all repositories and compiles them
BUILD_ALL=${1:-"notdefined"}

source kill_selflet.sh

GIT_REPO_BASE="git://github.com/nicola-calcavecchia/"

GIT_REPO[1]="reds"
GIT_REPO[2]="selflet-common"
GIT_REPO[3]="selflet"
GIT_REPO[4]="reds-broker"
GIT_REPO[5]="selflet-request-dispatcher"

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