package com.SocialMediaAPI.exception;

public class IncompatibleTypeError extends RuntimeException{
    public IncompatibleTypeError(String message){
        super(message);
    }
}
