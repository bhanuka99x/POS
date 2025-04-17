package com.posapp.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class LoadingScreenController {

    @FXML
    private Label lblversion;

    @FXML
    private ImageView logoimage;

    @FXML
    private ProgressIndicator progressindicator;


    @FXML
    public void initialize(){

        new Thread(() ->{
            try{
                Thread.sleep(3000);
                Platform.runLater(this::showlogin );
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }).start();
    }

    private void showlogin(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/login_screen.fxml"));
            Scene loginscene = new Scene(loader.load());
            Stage loginstage = new Stage();
            loginstage.setScene(loginscene);
            loginstage.show();

            Stage loadingstage = (Stage) progressindicator.getScene().getWindow();
            loadingstage.close();

        }catch (Exception e ){
            e.printStackTrace();

        }

    }

}
