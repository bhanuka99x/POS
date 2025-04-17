package com.posapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.StageStyle;

public class AppLauncher extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception  {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/loading_screen.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}