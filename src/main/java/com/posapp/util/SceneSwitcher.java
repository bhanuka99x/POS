package com.posapp.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneSwitcher {
    public static void switchScene(Stage stage, String fxmlPath) throws IOException {
        Parent root = FXMLLoader.load(SceneSwitcher.class.getResource(fxmlPath));
        stage.setScene(new Scene(root));
        stage.show();
    }
}