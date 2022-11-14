package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map; 
/**
 * Handler 
 */
public class App implements RequestHandler<Map<String,String>,String>{
    public String handleRequest(Map<String,String> event, Context context) {
        String response = "200 OK";
        return response;
    }
}
