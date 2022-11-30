# somejavalambdas

Various lambda functions .

To build the package run:

`mvn clean package`

To deploy the lambda function run:

`aws lambda update-function-code --function-name HelloJava --zip-file fileb://demo-1.0-SNAPSHOT.jar`

By branch:

- simple-pojo
    A lambda function that accepts a pojo and returns a pojo
    
- main branch:
    The main is a simple java handler that will accept a Map and return a string.