package com.mal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("UI/main.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 1280, 850));
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(1000);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
