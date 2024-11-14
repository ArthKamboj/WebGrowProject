package com.example.webgrow.payload.dto;


import lombok.Data;

@Data
public class DTOClass {
    private String message;
    private String status;
    private Object data;

    public DTOClass(String message) {
        this.message = message;
        this.status = "SUCCESS";
    }

    public DTOClass(String message, String status, Object data) {
        this.message = message;
        this.status = status;
        this.data = data;
    }

}
