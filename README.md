# somejavalambdas

To deploy the lambda functions run:

Create a lambda function with the handler: com.example.App::handleRequest

mvn clean package

aws lambda update-function-code --function-name HelloJava --zip-file fileb://target/demo-1.0-SNAPSHOT.jar 

Some various lambda functions for Java. By branch:

main branch: 
	The main is a simple java handler that will return a pojo.   
git switch branch1 
	Processes an S3 event
git switch branch2 






