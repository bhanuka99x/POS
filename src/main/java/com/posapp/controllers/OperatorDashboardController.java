package com.posapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class OperatorDashboardController {

    @FXML
    private Button btncustomers;

    @FXML
    private Button btninventory;

    @FXML
    private Button btnmenu;

    @FXML
    private Button btnorders;

    @FXML
    private Button btnreports;

    @FXML
    private Label txtlbl;

    @FXML
    private Button btnlogout;

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
    void clickmenu(ActionEvent event) {

    }

    @FXML
    void clickorders(ActionEvent event) {

    }

    @FXML
    void clickreports(ActionEvent event) {

    }
    private String userRole;

    public void setUserRole(String role) {
        this.userRole = role;
        System.out.println("User role in dashboard" + userRole);
        configureDashboard();
    }
    private void configureDashboard(){
        if(userRole.equals("admin")){
            System.out.println("Admin user detected");
            btnmenu.setVisible(false);
            btncustomers.setVisible(true);
            btninventory.setVisible(true);
            btnorders.setVisible(true);
            btnreports.setVisible(true);
            btnlogout.setVisible(true);
        }else if (userRole.equals("operator")){
            System.out.println("Operator user detected");
            btnmenu.setVisible(true);
            btninventory.setVisible(true);
            btncustomers.setVisible(true);
            btnorders.setVisible(false);
            btnreports.setVisible(false);
            btnlogout.setVisible(true);
        }else {
            System.out.println("Invalid role detected: " + userRole);
        }
    }



}
