#!/bin/sh
cd /Users/vitalijbukatkin/Desktop/MediaServer
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home"
export CONFIG_SERVICE_PASSWORD=rock64
export POSTGRES_PASSWORD=rock64
export POSTGRES_URI=localhost
gradle system:microservices:auth-service:bootRun