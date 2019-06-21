#!/bin/sh
cd /Users/vitalijbukatkin/Desktop/MediaServer
export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk-11.0.2.jdk/Contents/Home"
export CONFIG_SERVICE_URI=http://localhost:8888
export CONFIG_SERVICE_PASSWORD=rock64
export POSTGRES_PASSWORD=rock64
export POSTGRES_URI=localhost:5432
gradle system:microservices:auth-service:bootRun