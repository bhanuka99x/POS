package com.posapp.controllers;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class LoginScreenController {

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

    protected void showlogin(){
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

   public void closeapp(){
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Exit");
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to exit?");

        if(confirm.showAndWait().get() == ButtonType.OK){
            Platform.exit();
        }
   }
}
