package com.posapp.controllers;

import com.posapp.controllers.LoginScreenController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.AccessibleRole;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

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
    private Button btnope;

    @FXML
    private Label txtlbl;


    @FXML
    private Button btnlogout;

    @FXML
    void clickcustomers(ActionEvent event) {

    }

    @FXML
    void clickinventory(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_inventory.fxml"));
        Scene invtscene = new Scene(loader.load());
        Stage invtstage = new Stage();
        invtstage.setScene(invtscene);
        invtstage.setMaximized(true);
        invtstage.show();
        Stage currentstage = (Stage) txtlbl.getScene().getWindow();
        currentstage.close();

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

    @FXML
    void clickope(ActionEvent event) throws IOException {
          FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_operator.fxml"));
          Scene scene = new Scene(loader.load());
          Stage stage = new Stage();
          stage.setScene(scene);
          stage.setMaximized(true);
          stage.show();
          Stage curruntstage = (Stage)txtlbl.getScene().getWindow();
          curruntstage.close();






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
            btnmenu.setVisible(true);
            btncustomers.setVisible(true);
            btninventory.setVisible(true);
            btnorders.setVisible(true);
            btnreports.setVisible(true);
            btnlogout.setVisible(true);
            txtlbl.setText(userRole);

        }else if (userRole.equals("operator")){
            System.out.println("Operator user detected");
            btnmenu.setVisible(true);
            btninventory.setVisible(true);
            btncustomers.setVisible(true);
            btnorders.setVisible(false);
            btnreports.setVisible(false);
            btnlogout.setVisible(true);
            txtlbl.setText(userRole);
            System.out.println("Invalid role detected: " + userRole);
        }
    }



}
