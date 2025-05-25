package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;

public class LoadingScreenController extends LoginScreenController {

    @FXML private Label lblversion;
    @FXML private Label lblStatus;
    @FXML private ImageView logoimage;
    @FXML private ProgressIndicator progressindicator;

    @FXML
    public void initialize() {
        new Thread(() -> {
            try {
                updateStatus("Checking internet connection...");
                Thread.sleep(2000);
                if (!checkInternetConnection()) {
                    updateStatus("No internet connection.");
                    return;
                }
                updateStatus("Connecting to Schema...");
                Thread.sleep(1000);
                if (!checkDatabaseConnection()) {
                    updateStatus("Database connection failed.");
                    return;
                }
                updateStatus("Load succesfull....");
                Thread.sleep(1000);
                Platform.runLater(this::show_login);

            } catch (InterruptedException e) {
                updateStatus("Unexpected error occurred.");
                e.printStackTrace();
            }
        }).start();
    }

    private boolean checkInternetConnection() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 1500); 
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    private boolean checkDatabaseConnection() {
        try (Connection conn = dbconn.connect()) {
            return conn != null;
        } catch (Exception e) {
            return false;
        }
    }
    private void updateStatus(String message) {
        Platform.runLater(() -> lblStatus.setText(message));
    }
}
