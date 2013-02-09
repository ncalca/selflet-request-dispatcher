#!/bin/sh

source kill_selflet.sh

cd selflet
screen -d -m mvn exec:java -Dexec.args="src/main/resources/selflets/${3} -i ${1} -b ${2}"
