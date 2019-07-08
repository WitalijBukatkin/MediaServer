#!/bin/sh
cd /Users/vitalijbukatkin/Desktop/MediaServer
export CONFIG_SERVICE_PASSWORD=rock64
export CONFIG_SERVICE_URI=http://localhost:8888
export AUTH_SERVICE_URI=http://localhost:5000
export ROOT_FILE_PATH=/Users/
export TEST_ROOT_FILE_PATH=/Users/vitalijbukatkin/Desktop/MediaServer/system/microservices/file-service/testdirectory/
gradle system:microservices:file-service:bootRun