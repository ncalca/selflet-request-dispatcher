#!/bin/sh

FILE_PATH=SelfletRequestDispatcher/src/main/resources/AwsCredentials.properties

echo "accessKey="${1} > $FILE_PATH
echo "secretKey="${2} >> $FILE_PATH
