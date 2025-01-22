package com.security.spring_security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserAlreadyExistWithUsernameException extends RuntimeException{
    public UserAlreadyExistWithUsernameException(String message){
        super(message);
    }
}
