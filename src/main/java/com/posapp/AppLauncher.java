package com.posapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/loading_screen.fxml"));
        primaryStage.setScene(new Scene(loader.load()));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("pos");
        primaryStage.show();

    }
}