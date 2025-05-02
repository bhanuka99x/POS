package com.posapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Button btncustomers;

    @FXML
    private Button btninventory;

    @FXML
    private Button btnlogout;

    @FXML
    private Button btnmenu;

    @FXML
    private Button btnope;

    @FXML
    private Button btnorders;

    @FXML
    private Button btnreports;

    @FXML
    private Label txtlbl;

    @FXML
    void clickcustomers(ActionEvent event) {

    }

    @FXML
    void clickinventory(ActionEvent event) {

    }

    @FXML
    void clicklogout(ActionEvent event) {

    }

    @FXML
    void clickmenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage curruntstage = (Stage)txtlbl.getScene().getWindow();
        curruntstage.close();
    }

    @FXML
    void clickope(ActionEvent event) {

    }

    @FXML
    void clickorders(ActionEvent event) {

    }

    @FXML
    void clickreports(ActionEvent event) {

    }

}
