package com.example.socksmyapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_GATEWAY)
public class SocksNotFoundException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Таких носков на складе нет!";
    }
}
