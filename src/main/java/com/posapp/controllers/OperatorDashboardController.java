package com.posapp.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OperatorDashboardController {








    protected void load_dashboard(){
       try{
           FXMLLoader loaderop = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
           Scene dashscene = new Scene(loaderop.load());
           Stage dashstage = new Stage();
           dashstage.setScene(dashscene);
           dashstage.show();
       } catch (Exception e) {

           e.printStackTrace();
       }
    }
}