package com.posapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class OrderManagementController {

    @FXML
    private Button btncustomers,btninventory,btnlogout,btnmenu,btnope,btnorders,btnreports,btnsearch;

    @FXML private Label txtlbl;
    @FXML private TextField txtsearch;

    @FXML private TableColumn<?, ?> cell_discount;
    @FXML private TableColumn<?, ?> cell_payment_action;
    @FXML private TableColumn<?, ?> cell_payment_date;
    @FXML private TableColumn<?, ?> cell_payment_id;
    @FXML private TableColumn<?, ?> cell_payment_option;
    @FXML private TableColumn<?, ?> cell_payment_time;
    @FXML private TableColumn<?, ?> cell_payment_total;
    @FXML private TableColumn<?, ?> cell_tax;
    @FXML private TableColumn<?, ?> cell_total;
    @FXML private TableView<?> payment_record;






























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
    void clickope(ActionEvent event) {

    }

    @FXML
    void clickorders(ActionEvent event) {

    }

    @FXML
    void clickreports(ActionEvent event) {

    }

}
