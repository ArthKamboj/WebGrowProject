package com.example.webgrow.user;

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

    // Getters and setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
