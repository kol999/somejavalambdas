package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.util.Map; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler
 * A very straightforward handler that accepts a Map and returns a String.
 * 
 */
public class App implements RequestHandler<Map<String,String>,String>{

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public String handleRequest(Map<String,String> event, Context context) {
        logger.info("EVENT:" + gson.toJson(event));
        try{ 
            System.out.println("Hello from the Lamba function");
            String response = "200 OK";
            return response;
        }catch(Exception e){
            return e.getMessage(); 
        }
    }
}