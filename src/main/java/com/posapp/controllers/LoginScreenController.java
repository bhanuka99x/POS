package com.posapp.controllers;
import com.posapp.dbconnection.dbconn;
import com.posapp.util.Alerts;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginScreenController  {

    @FXML
    private Button btnlexit;

    @FXML
    private Button btnexit;

    @FXML
    private PasswordField txtpassword;

    @FXML
    private TextField txtuser;

    @FXML
    private Label lblversion;

    @FXML
    private ImageView logoimage;

    @FXML
    private ProgressIndicator progressindicator;

    @FXML
    private void handleKeyevent(KeyEvent event){
        if(event.getCode() == KeyCode.ENTER){
            loginsystem();
        }
        if(event.getCode() ==KeyCode.ESCAPE){
            closelogin();
        }
    }

    @FXML
    private void closelogin(){

        if(Alerts.showconfirmation("Exit","Are you sure you want to exit?")){
            Platform.exit();
        }
    }

    public void loginsystem(){

        String username = txtuser.getText();
        String password = txtpassword.getText();

        if(username.isEmpty() || password.isEmpty()){
            Alerts.showError("Error","Please enter username and password");
            return;
        }
        try(Connection conn = dbconn.connect() ){
            String sql = "SELECT * FROM users WHERE user_name= ? AND password_hash = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,username);
            stmt.setString(2,password);
            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                String role = rs.getString("role");
                System.out.println("user role" + role );
                Stage curruntstage = (Stage) txtuser.getScene().getWindow();
                curruntstage.close();
                load_dashboard(role);

            }else{
                Alerts.showError("Error", "Invalid username or password.");
            }

        }catch (Exception e){
            Alerts.showError("Error", "Invalid username or password.");
        }
    }

    protected void show_login(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/login_screen.fxml"));
            Scene loginscene = new Scene(loader.load());
            Stage loginstage = new Stage();
            loginstage.setScene(loginscene);
            loginstage.initStyle(StageStyle.UNDECORATED);
            loginscene.getStylesheets().add(getClass().getResource("/com/posapp/css/Application.css").toExternalForm());
            loginstage.show();
            Stage loadingstage = (Stage) progressindicator.getScene().getWindow();
            loadingstage.close();
        }catch (Exception e ){
            e.printStackTrace();

        }

    }
    public void load_dashboard(String role) {
        try {
            FXMLLoader loaderop = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
            Scene dashscene = new Scene(loaderop.load());
            Stage dashstage = new Stage();
            dashstage.setScene(dashscene);
            dashstage.setMaximized(true);

            OperatorDashboardController operatorDashboardController = loaderop.getController();
            operatorDashboardController.setUserRole(role);
            dashstage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
