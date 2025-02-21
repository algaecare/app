package com.algaecare.view;

import com.algaecare.controller.MainController;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainView extends VBox {
    private MainController controller;
    private Label messageLabel;
    private TextField messageInput;
    private Button updateButton;

    public MainView() {
        controller = new MainController();
        initializeUI();
    }

    private void initializeUI() {
        setPadding(new Insets(10));
        setSpacing(10);

        messageLabel = new Label(controller.getMessage());
        messageInput = new TextField();
        updateButton = new Button("Update Message");

        updateButton.setOnAction(e -> {
            String newMessage = messageInput.getText();
            controller.updateMessage(newMessage);
            messageLabel.setText(controller.getMessage());
        });

        getChildren().addAll(messageLabel, messageInput, updateButton);
    }
}