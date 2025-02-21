package com.algaecare.model;

public class Model {
    private String message;

    public Model() {
        this.message = "Hello from Model!";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}