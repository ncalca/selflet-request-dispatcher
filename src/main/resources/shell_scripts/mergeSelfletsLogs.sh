#!/bin/sh

cd ../../webapp/logs/
cat actions*.log > all_actions.csv
rm actions*.log
sed -i '1,1s/^/timestamp,action \n/g' all_actions.csv
mv all_actions.csv ../results/

cat results*.log > all_results.csv
rm results*.log
sed -i '1,1s/^/timestamp,reqType,service,rt,selflet,numberOfReqs \n/g' all_results.csv
mv all_results.csv ../results/

cat selflet*.log > all_logs.log
rm selflet*.log
mv all_logs.log ../results/

cd ../../../../logs
sed -i '1,1s/^/timestamp,selflets \n/g' activeSelflets.log
mv activeSelflets.log ../src/main/webapp/results/activeSelflets.csv

sed -i '1,1s/^/timestamp,service,rt,selfletID,numberOfReqs \n/g' requests.log
mv requests.log ../src/main/webapp/results/requests.csv