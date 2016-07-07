#!/usr/bin/env bash
echo "Starting $0"

DIRNAME=`dirname $0`
PROCESS_HOME=`cd $DIRNAME/.;pwd;`
export PROCESS_HOME;

mvn package -DskipTests; java -jar target/tls-controller-1.0-SNAPSHOT-fat.jar -conf $PROCESS_HOME/conf/conf.json
