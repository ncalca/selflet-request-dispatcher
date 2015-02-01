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

cat start*.log > all_start.csv
rm start*.log
sed -i '1,1s/^/timestamp,selflet \n/g' all_start.csv
mv all_start.csv ../results/

cat end*.log > all_end.csv
rm end*.log
sed -i '1,1s/^/timestamp,selflet \n/g' all_end.csv
mv all_end.csv ../results/

cat life*.log > all_life.csv
rm life*.log
sed -i '1,1s/^/timestamp,selflet,start,end,lifetime \n/g' all_life.csv
mv all_life.csv ../results/

cat selflet*.log > all_logs.log
rm selflet*.log
mv all_logs.log ../results/

cd ../../../../logs
sed -i '1,1s/^/timestamp,selflets \n/g' activeSelflets.log
mv activeSelflets.log ../src/main/webapp/results/activeSelflets.csv

sed -i '1,1s/^/timestamp,service,rt,selfletID,numberOfReqs \n/g' requests.log
mv requests.log ../src/main/webapp/results/requests.csv