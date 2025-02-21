package com.algaecare.app;

import com.algaecare.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView);
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        // primaryStage.setFullScreen(true);
        primaryStage.setTitle("Algae Care");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}