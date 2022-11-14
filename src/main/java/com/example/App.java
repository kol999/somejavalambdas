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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

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
        StringBuilder stringBuilder = transformFile(file); 
        uploadFile(stringBuilder, srcBucket, srcKey);

        String response = "200 OK";
        return response;
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

    private StringBuilder transformFile(ResponseInputStream<GetObjectResponse> file) {
        BufferedReader bufferReader = new BufferedReader(
                new InputStreamReader(file, StandardCharsets.UTF_8));

        StringBuilder stringBuilder = new StringBuilder();

        bufferReader.lines()
                .collect(Collectors.toList())
                .stream()
                .forEach(line -> stringBuilder.append(line.toUpperCase() + "\n"));

        return stringBuilder;
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
}