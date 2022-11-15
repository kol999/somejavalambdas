package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map; 
import java.io.IOException;
/**
 * Handler 
 */
public class App implements RequestHandler<Map<String,String>,String>{
    public String handleRequest(Map<String,String> event, Context context) {
        try{ 
            System.out.println("Hello from the Lamba function");
            System.out.println(event);
            String response = "200 OK";
            return response;
        }catch(Exception e){
            return e.getMessage(); 
        }
    }
}