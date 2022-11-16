package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.concurrent.atomic.AtomicInteger;

import java.net.URI; 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import java.util.Map; 
import java.util.HashMap;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter; 


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler 
 */

public class App implements RequestHandler<S3Event,String>{
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public String handleRequest(S3Event event, Context context) {
        logger.info("EVENT:" + gson.toJson(event));

        S3EventNotificationRecord record = event.getRecords().get(0);
        String srcBucket = record.getS3().getBucket().getName();
        String srcKey = record.getS3().getObject().getUrlDecodedKey();
        ResponseInputStream<GetObjectResponse> file = getFileFromS3(srcBucket, srcKey); 

        final Map<StringBuilder, Integer> map = transformFile(file); 

        Map.Entry<StringBuilder, Integer> entry = map.entrySet().iterator().next();
        StringBuilder sb = entry.getKey();
        int lineCount = entry.getValue();

        logger.info("lineCount: " + Integer.toString(lineCount));
        uploadFile(sb, srcBucket, srcKey);
        updateTable(srcKey, srcBucket, lineCount);
        return(new String("200 OK" )); 
    }

    private ResponseInputStream<GetObjectResponse> getFileFromS3(String bucketName, String fileName) {
        Region region = Region.EU_WEST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        ResponseInputStream<GetObjectResponse> file = s3.getObject(getObjectRequest);
        return file;
    }

    private Map<StringBuilder, Integer> transformFile(ResponseInputStream<GetObjectResponse> file) {
        BufferedReader bufferReader = new BufferedReader(

                new InputStreamReader(file, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                AtomicInteger counter = new AtomicInteger();
                bufferReader.lines()
                        .collect(Collectors.toList())
                        .stream()
                        .forEach(line -> {
                            stringBuilder.append(line.toUpperCase() + "\n"); 
                            counter.getAndIncrement(); 
                        });
        
                Map<StringBuilder, Integer> map = new HashMap<StringBuilder, Integer>();
                map.put(stringBuilder, counter.get());

                return map; 
        
    }

    private void uploadFile(StringBuilder stringBuilder, String bucketName, String fileName ) {
        Region region = Region.EU_WEST_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .build();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("/transformed/" + fileName)
                .build();

        RequestBody requestBody = RequestBody.fromString(stringBuilder.toString());
        s3.putObject(objectRequest, requestBody);
    }

    private void updateTable(String bucketName, String key, int lineCount) {
        try {
            // DynamoDbClient ddb = DynamoDbClient.builder().region(Region.EU_WEST_1).build();
            DynamoDbClient ddb = DynamoDbClient.builder()
//                                        .endpointOverride(URI.create("http://localhost:8000"))
                                        .region(Region.EU_WEST_1).build();

            DynamoDbEnhancedClient client = DynamoDbEnhancedClient.builder().dynamoDbClient(ddb).build();

            DynamoDbTable<MetaFileIndex> table = client.table("MetaFileIndex", TableSchema.fromBean(MetaFileIndex.class));
       
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
            LocalDateTime now = LocalDateTime.now();              

            // LocalDate localDate = LocalDate.parse("2020-04-07");
            //LocalDateTime localDateTime = localDate.atStartOfDay();
            // Instant instant = localDateTime.toInstant(ZoneOffset.UTC);

            // Populate the Table.
            MetaFileIndex metaInfo = new MetaFileIndex();
            metaInfo.setFileName(key);
            metaInfo.setLineCount(lineCount);
            metaInfo.setBucketName(bucketName);
            metaInfo.setRegistrationDate(now) ;

            // Put the file data into an Amazon DynamoDB table.
            table.putItem(metaInfo);

        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        System.out.println("MetaInfo updated");

    }

}