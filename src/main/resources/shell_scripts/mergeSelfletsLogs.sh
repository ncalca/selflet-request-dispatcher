#!/bin/sh

cd ../../webapp/logs/
cat actions*.log > all_actions.log
rm actions*.log
#sed -i '1,1s/^/timestamp,action \n/g' all_actions.log
mv test_actions.log ../results/

cat results*.log > all_results.log
rm results*.log
#sed -i '1,1s/^/timestamp,reqType,service,rt,numberOfReqs \n/g' all_results.log
mv test_results.log ../results/

cat selflet*.log > all_logs.log
rm selflet*.log
mv test_logs.log ../results/

cd ../../../../logs
#sed -i '1,1s/^/timestamp,selflets \n/g' activeSelflets.log
mv activeSelflets.log ../src/main/webapp/results/