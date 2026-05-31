package com.datafoot.exception.entitexception;

public class PlayerNotFoundException extends RuntimeException{

    public PlayerNotFoundException(String message){

        super(message);
    }
}
