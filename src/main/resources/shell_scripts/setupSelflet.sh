#!/bin/sh

# downloads source code for all repositories and compiles it with maven
BUILD_ALL=${1:-"notdefined"}

source kill_selflet.sh

GIT_REPO_BASE="git://github.com/nicola-calcavecchia/"

GIT_REPO[1]="reds"
GIT_REPO[2]="selflet-common"
GIT_REPO[3]="selflet"
GIT_REPO[4]="reds-broker"
GIT_REPO[5]="selflet-request-dispatcher"

for index in `seq 1 5`
do
        echo 'Checking out repository for '${GIT_REPO[index]}
		mkdir ${GIT_REPO[index]} 2> /dev/null
        git clone --depth=1 $GIT_REPO_BASE${GIT_REPO[index]}".git"
done

DIRECTORIES="REDS SelfLetCommon SelfLetPrototype RedsMiddleware SelfletRequestDispatcher"

for DIRNAME in "${GIT_REPO[@]}"
do
        echo "--> Building ${DIRNAME}"
        cp mavenLifeCyle.sh ${DIRNAME}
        cd $DIRNAME
        # TODO: optimize for already up-to-date code (avoid recompiling)
        #  CHANGED=`cat svn_delta | wc -l`
        #if [ $CHANGED -gt 1 -o $BUILD_ALL == "-b" ]
                #        then
                source mavenLifeCyle.sh
        #else
                #         echo 'Project '${DIRNAME}' already up to date'
        #fi
        cd ..
done
