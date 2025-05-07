package com.posapp.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OperatorDashboardController {

    @FXML
    private TableColumn<?, ?> action;

    @FXML
    private TableColumn<?, ?> add_quantity;

    @FXML
    private Button btncustomers;

    @FXML
    private Button btngenate_receipt;

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
    private Button btnpay;

    @FXML
    private Button btnremove;

    @FXML
    private Button btnreports;

    @FXML
    private ComboBox<?> cmb_payment_method;

    @FXML
    private Label lbltax;

    @FXML
    private Label lbltotal;

    @FXML
    private TableColumn<?, ?> price;

    @FXML
    private TableColumn<?, ?> product_image;

    @FXML
    private TableColumn<?, ?> product_name;

    @FXML
    private TableColumn<?, ?> receipt_price;

    @FXML
    private TableColumn<?, ?> receipt_product_name;

    @FXML
    private TableColumn<?, ?> receipt_quantity;

    @FXML
    private TableView<?> tbl_product_display;

    @FXML
    private TableView<?> tbl_receipt;

    @FXML
    private TextField txtamount;

    @FXML
    private Label txtlbl;

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
    void clickope(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_operator.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage curruntstage = (Stage) txtlbl.getScene().getWindow();
        curruntstage.close();
    }


        @FXML
    void clickorders(ActionEvent event) {

    }

    @FXML
    void clickpay(ActionEvent event) {

    }

    @FXML
    void clickreceipt(ActionEvent event) {

    }

    @FXML
    void clickremove(ActionEvent event) {

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
