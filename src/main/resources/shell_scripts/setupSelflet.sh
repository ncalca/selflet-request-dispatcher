#!/bin/sh

BUILD_ALL=${1:-"notdefined"}

source kill_selflet.sh

PROJECTS[1]="REDS"
PROJECTS[2]="SelfLetCommon"
PROJECTS[3]="SelfLetPrototype"
PROJECTS[4]="RedsMiddleware"
PROJECTS[5]="SelfletRequestDispatcher"


for index in 1 2 3 4 5
do
        echo 'Checking out repository for '${PROJECTS[index]}
    	mkdir ${PROJECTS[index]} &> /dev/null
        svn co https://selflet.svn.sourceforge.net/svnroot/selflet/trunk/${PROJECTS[index]} --non-interactive --trust-server-cert > ${PROJECTS[index]}/svn_delta
done

DIRECTORIES="REDS SelfLetCommon SelfLetPrototype RedsMiddleware SelfletRequestDispatcher"

for DIRNAME in $DIRECTORIES
do
        echo "--> Building ${DIRNAME}"
        cp mavenLifeCyle.sh ${DIRNAME}
        cd $DIRNAME
        CHANGED=`cat svn_delta | wc -l`
        if [ $CHANGED -gt 1 -o $BUILD_ALL == "-b" ]
                then
                source mavenLifeCyle.sh
        else
                echo 'Project '${DIRNAME}' already up to date'
        fi
        cd ..
done