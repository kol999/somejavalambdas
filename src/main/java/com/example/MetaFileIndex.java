package com.example;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
// import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute; 

import java.time.LocalDateTime;

/**
 * This class is used by the Enhanced Client examples.
 */

 @DynamoDbBean
public class MetaFileIndex{

        private String fileName;
        private String bucketName;
        private int lineCount;
        private LocalDateTime registrationDate;

        @DynamoDbPartitionKey
	@DynamoDbAttribute("FileName") 	
        public String getFileName() {
            return this.fileName;
        }

	@DynamoDbAttribute("FileName") 	
        public void setFileName(String name) {
            this.fileName = name;
        }

	@DynamoDbAttribute("BucketName") 	
        public String getBucketName() {
            return this.bucketName;
        }

	@DynamoDbAttribute("BucketName") 	
        public void setBucketName(String name) {
            this.bucketName= name;
        }

	@DynamoDbAttribute("LineCount") 	
	public void setLineCount(int lineCount) {
            this.lineCount = lineCount;
    	}	

	@DynamoDbAttribute("LineCount") 	
	public int getLineCount() {
	    return this.lineCount;
    	}

	@DynamoDbAttribute("RegistrationDate") 	
	public void setRegistrationDate(LocalDateTime now) {
		this.registrationDate = now;  
	}

	@DynamoDbAttribute("RegistrationDate") 	
	public LocalDateTime getRegistrationDate() {
		return registrationDate; 
	}

        @Override
        public String toString() {
            return "File [name=" + fileName + "]"; 
        }
}