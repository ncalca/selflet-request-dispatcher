#!/bin/sh

FILE_PATH=selflet-request-dispatcher/src/main/resources/AwsCredentials.properties

echo "accessKey="${1} > $FILE_PATH
echo "secretKey="${2} >> $FILE_PATH
