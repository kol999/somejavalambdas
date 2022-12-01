package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.example.pojo.Input; 
import com.example.pojo.Output; 

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class App implements RequestHandler<Input,Output>{

  Gson gson = new GsonBuilder().setPrettyPrinting().create();

  public App() {
    System.out.println("App constructor");
  }


  @Override
  public Output handleRequest(Input input, Context context) {

    LambdaLogger logger = context.getLogger();
    String  env_var = System.getenv("ENV_VAR");
    logger.log("ENV_VAR: " + env_var); 

    logger.log("EVENT: " + gson.toJson(input));

    String message = String.format("Hello %s%s.", input.getName() , " " + "from " + context.getFunctionName());
    return new Output(message);
  }
  
}
