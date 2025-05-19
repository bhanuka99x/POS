package com.posapp.controllers;
import com.posapp.dbconnection.dbconn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderManagementController {

    @FXML private Button btncustomers,btninventory,btnlogout,btnmenu,btnope,btnorders,btnreports,btnsearch;

    @FXML private Label txtlbl;
    @FXML private TextField txtsearch;

    @FXML private TableColumn<PaymentItem, Double> cell_discount;
    @FXML private TableColumn<PaymentItem, Double> cell_payment_action;
    @FXML private TableColumn<PaymentItem, String> cell_payment_date;
    @FXML private TableColumn<PaymentItem, Integer> cell_payment_id;
    @FXML private TableColumn<PaymentItem, String> cell_payment_option;
    @FXML private TableColumn<PaymentItem, String> cell_payment_time;
    @FXML private TableColumn<PaymentItem, Double> cell_payment_total;
    @FXML private TableColumn<PaymentItem, Double> cell_tax;
    @FXML private TableColumn<PaymentItem, Double> cell_total;
    @FXML private TableView<PaymentItem> payment_record;


    public void initialize() {
        LoadPaymentTable();
        ConfigurePaymentTable();

        payment_record.setRowFactory(tv -> new TableRow<PaymentItem>() {
            @Override
            protected void updateItem(PaymentItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if (isSelected()) {
                    setStyle("-fx-background-color: red;");
                } else {
                    setStyle("");
                }
            }
        });
    }



    private ObservableList<PaymentItem> PaymentList = FXCollections.observableArrayList();

    private void ConfigurePaymentTable(){
        cell_payment_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        cell_payment_option.setCellValueFactory(new PropertyValueFactory<>("paymentOption"));
        cell_payment_total.setCellValueFactory(new PropertyValueFactory<>("subTotal"));
        cell_tax.setCellValueFactory(new PropertyValueFactory<>("tax"));
        cell_discount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        cell_total.setCellValueFactory(new PropertyValueFactory<>("total"));
        cell_payment_time.setCellValueFactory(new PropertyValueFactory<>("paymentTime"));
        cell_payment_date.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
    }
    public class PaymentItem {
        private int id;
        private String paymentOption;
        private double subTotal;
        private double tax;
        private double discount;
        private double total;
        private String paymentDate;
        private String paymentTime;

        public PaymentItem(int id, String paymentOption, double subTotal, double tax, double discount, double total, String paymentDate, String paymentTime) {
            this.id = id;
            this.paymentOption = paymentOption;
            this.subTotal = subTotal;
            this.tax = tax;
            this.discount = discount;
            this.total = total;
            this.paymentDate = paymentDate;
            this.paymentTime = paymentTime;
        }

        public int getId() {return id;}
        public String getPaymentOption() {return paymentOption;}
        public double getSubTotal() {return subTotal;}
        public double getTax() {return tax;}
        public double getDiscount() {return discount;}
        public double getTotal() {return total;}
        public String getPaymentDate() {return paymentDate;}
        public String getPaymentTime() {return paymentTime;}

    }

    private void LoadPaymentTable(){
        PaymentList.clear();
        try(Connection conn = dbconn.connect()){
            String sql = "SELECT * FROM payments";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                int id =rs.getInt("payment_id");
                String pay_option = rs.getNString("payment_option");
                double subtotal = rs.getDouble("sub_total");
                double tax = rs.getDouble("tax");
                double discount = rs.getDouble("discount");
                double total = rs.getDouble("total");
                String paymentdate = rs.getString("payment_date");
                String paymenttime = rs.getString("payment_time");

                PaymentList.add(new PaymentItem(id,pay_option,subtotal,tax,discount,total,paymentdate,paymenttime));
            }
            payment_record.setItems(PaymentList);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }











    @FXML
    public void clickmenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickinventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_inventory.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickcustomers(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_customers.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickorders(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_orders.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickreports(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_report.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    public void clickope(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_operator.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    @FXML
    void clicklogout(ActionEvent event) {

    }
    @FXML void clicksearch(ActionEvent event) {
        String input = txtsearch.getText();
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        try {
            int searchId = Integer.parseInt(input.trim());
            for (PaymentItem item : PaymentList) {
                if (item.getId() == searchId) {
                    payment_record.getSelectionModel().select(item);
                    payment_record.scrollTo(item);
                    break;
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format.");
        }
    }
}
