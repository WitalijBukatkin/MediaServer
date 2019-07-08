#!/bin/sh
cd /Users/vitalijbukatkin/Desktop/MediaServer
export CONFIG_SERVICE_PASSWORD=rock64
gradle system:microservices:config-service:bootRun