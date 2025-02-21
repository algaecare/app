package com.algaecare.controller;

import com.algaecare.model.Model;

public class MainController {
    private Model model;

    public MainController() {
        this.model = new Model();
    }

    public String getMessage() {
        return model.getMessage();
    }

    public void updateMessage(String newMessage) {
        model.setMessage(newMessage);
    }
}