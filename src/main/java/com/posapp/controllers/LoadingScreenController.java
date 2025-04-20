package com.posapp.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.paint.Paint;
import javafx.stage.StageStyle;

public class LoadingScreenController extends LoginScreenController{

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
                Thread.sleep(1000);
                Platform.runLater(this::show_login );
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }).start();
    }
}
