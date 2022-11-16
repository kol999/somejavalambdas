#!/bin/bash

aws lambda update-function-code --function-name HelloJava --zip-file fileb://./target/demo-1.0-SNAPSHOT.jar
