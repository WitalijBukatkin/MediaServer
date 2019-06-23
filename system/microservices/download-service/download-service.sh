#!/bin/sh
cd /Users/vitalijbukatkin/Desktop/MediaServer
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home"
export CONFIG_SERVICE_PASSWORD=rock64
export CONFIG_SERVICE_URI=http://localhost:8888
export AUTH_SERVICE_URI=http://localhost:5000
export ROOT_FILE_PATH=/Users/
gradle system:microservices:download-service:bootRun