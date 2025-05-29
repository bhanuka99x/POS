package com.posapp.controllers;
import com.posapp.dbconnection.dbconn;

import com.posapp.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderManagementController {

    @FXML private Button btncustomers,btninventory,btnlogout,btnmenu,btnope,btnorders,btnreports,btnsearch;

    @FXML private Label txtlbl,txtName;
    @FXML private TextField txtsearch;

    @FXML private TableColumn<PaymentItem, Double> cell_discount;
    @FXML private TableColumn<PaymentItem, Void> cell_payment_action;
    @FXML private TableColumn<PaymentItem, String> cell_payment_date;
    @FXML private TableColumn<PaymentItem, Integer> cell_payment_id;
    @FXML private TableColumn<PaymentItem, String> cell_payment_option;
    @FXML private TableColumn<PaymentItem, String> cell_payment_time;
    @FXML private TableColumn<PaymentItem, Double> cell_payment_total;
    @FXML private TableColumn<PaymentItem, Double> cell_tax;
    @FXML private TableColumn<PaymentItem, Double> cell_total;
    @FXML private TableColumn<PaymentItem,String>cell_taken_items;
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
        cell_taken_items.setCellValueFactory(new PropertyValueFactory<>("TakenItems"));
        cell_payment_action.setCellFactory(e->new TableCell<>(){
            private final Button removebutton = new Button("Remove");
            {
                removebutton.setStyle("-fx-background-color: #fd6f6f; -fx-text-fill: white; -fx-font-weight: bold;-fx-font-family:Calibri;-fx-font-size:15px;-fx-pref-width: 80px;-fx-pref-height: 30px;");
                removebutton.setOnAction(actionEvent -> {
                    PaymentItem item = getTableView().getItems().get(getIndex());
                    if (item == null) return;

                    try (Connection conn = dbconn.connect()) {
                        String sql = "DELETE FROM payments WHERE payment_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, item.getId());
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            PaymentList.remove(item);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removebutton);
                }
            }
        });

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
        private String TakenItems;

        public PaymentItem(int id, String paymentOption, double subTotal, double tax, double discount, double total, String paymentDate, String paymentTime,String TakenItems) {
            this.id = id;
            this.paymentOption = paymentOption;
            this.subTotal = subTotal;
            this.tax = tax;
            this.discount = discount;
            this.total = total;
            this.paymentDate = paymentDate;
            this.paymentTime = paymentTime;
            this.TakenItems = TakenItems;
        }

        public int getId() {return id;}
        public String getPaymentOption() {return paymentOption;}
        public double getSubTotal() {return subTotal;}
        public double getTax() {return tax;}
        public double getDiscount() {return discount;}
        public double getTotal() {return total;}
        public String getPaymentDate() {return paymentDate;}
        public String getPaymentTime() {return paymentTime;}
        public String getTakenItems(){return TakenItems;}

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
                String takenitems = rs.getString("taken_items");

                PaymentList.add(new PaymentItem(id,pay_option,subtotal,tax,discount,total,paymentdate,paymenttime,takenitems));
            }
            payment_record.setItems(PaymentList);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }





    @FXML
    void clicksearch(ActionEvent event) {
        String input = txtsearch.getText();
        if (input == null || input.trim().isEmpty()) {
            Alerts.showError("Error", "Please enter a Payment ID.");
            return;
        }

        int searchId;
        try {
            searchId = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            Alerts.showError("Error", "Invalid ID format");
            return;
        }

        String checksql = "SELECT * FROM payments WHERE payment_id = ?";
        try (Connection conn = dbconn.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checksql)) {

            checkStmt.setInt(1, searchId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                for (PaymentItem item : PaymentList) {
                    if (item.getId() == searchId) {
                        payment_record.getSelectionModel().select(item);
                        payment_record.scrollTo(item);
                        break;
                    }
                }
            } else {
                Alerts.showError("Error", "Payment ID not found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showError("Error", "Error accessing payment records");
        }
    }

    private String userRole;
    private String operator;
    public void setUserRole(String role,String operator) {
        this.userRole = role;
        this.operator =operator;
        configureDashboard();
    }

    private void configureDashboard() {
        if ("admin".equals(userRole)) {
            btnmenu.setDisable(false);
            btncustomers.setDisable(false);
            btninventory.setDisable(false);
            btnorders.setDisable(false);
            btnreports.setDisable(false);
            btnlogout.setDisable(false);
            btnope.setDisable(false);
            txtlbl.setText(userRole);
            txtName.setText(operator);

        } else if ("operator".equals(userRole)) {
            btnmenu.setDisable(false);
            btncustomers.setDisable(false);
            btninventory.setDisable(false);
            btnorders.setDisable(true);
            btnreports.setDisable(true);
            btnope.setDisable(true);
            btnlogout.setDisable(false);
            txtlbl.setText(userRole);
            txtName.setText(operator);
        }
    }


    public void clickmenu(ActionEvent event) throws IOException {
        clickmenu(userRole,operator);
    }
    @FXML
    public void clickmenu(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        OperatorDashboardController operatorDashboardController = loader.getController();
        operatorDashboardController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }

    public void clickinventory(ActionEvent event) throws IOException {
        clickinventory(userRole,operator);
    }
    @FXML
    public void clickinventory(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_inventory.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        InventoryManagementController inventoryManagementController = loader.getController();
        inventoryManagementController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }

    public void clickcustomers(ActionEvent event) throws IOException {
        clickcustomers(userRole,operator);
    }
    public void clickcustomers(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_customers.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        CustomerManagementController customerManagementController = loader.getController();
        customerManagementController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    public void clickorders(ActionEvent event) throws IOException {
        clickorders(userRole,operator);
    }
    public void clickorders(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_orders.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        OrderManagementController orderManagementController = loader.getController();
        orderManagementController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    public void clickreports(ActionEvent event) throws IOException {
        clickreports(userRole,operator);
    }
    public void clickreports(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_report.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        ReportsAnalyticsController reportsAnalyticsController = loader.getController();
        reportsAnalyticsController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    public void clickope(ActionEvent event) throws IOException {
        clickope(userRole,operator);
    }
    public void clickope(String role,String operator) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_operator.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setMaximized(true);
        AdminDashboardController adminDashboardController = loader.getController();
        adminDashboardController.setUserRole(role,operator);
        stage.show();
        Stage currentstage = (Stage)txtlbl.getScene().getWindow();
        currentstage.close();
    }
    public void clicklogout(ActionEvent event) throws IOException {

        if (Alerts.showconfirmation("Exit","","Are you sure you want to logout?")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/login_screen.fxml"));
            Scene loginscene = new Scene(loader.load());
            Stage loginstage = new Stage();
            loginstage.setScene(loginscene);
            loginstage.initStyle(StageStyle.UNDECORATED);
            loginscene.getStylesheets().add(getClass().getResource("/com/posapp/css/Application.css").toExternalForm());
            Stage currentstage = (Stage)txtlbl.getScene().getWindow();
            currentstage.close();
            loginstage.show();
        }
    }
}