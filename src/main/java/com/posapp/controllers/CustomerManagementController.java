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
import java.sql.*;
import java.time.LocalDate;

public class CustomerManagementController {

    @FXML private Button btnadd, btncustomers, btnedit, btninventory, btnlogout, btnmenu, btnope, btnorders, btnreports, btnsend;
    @FXML private TableColumn<Customer, Void> cell_action;
    @FXML private TableColumn<Customer, CheckBox> cell_checkbox;
    @FXML private TableColumn<Customer, Integer> cell_cus_id;
    @FXML private TableColumn<Customer, String> cell_date;
    @FXML private TableColumn<Customer, Integer> cell_mobile_number;
    @FXML private TableView<Customer> tablecustomers;
    @FXML private Label txtName, txtlbl;
    @FXML private TextArea txtmessage;
    @FXML private TextField txtmobilenumber;

    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Customer selectedUser = null;

    @FXML
    public void initialize() {
        loadCustomerData();
        setupTableColumns();
    }

    private void setupTableColumns() {
        cell_cus_id.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        cell_mobile_number.setCellValueFactory(new PropertyValueFactory<>("phone"));
        cell_date.setCellValueFactory(new PropertyValueFactory<>("date"));
        cell_checkbox.setCellFactory(col -> new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    customer.getSelect().setSelected(checkBox.isSelected());
                });
            }

            @Override
            protected void updateItem(CheckBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Customer customer = getTableView().getItems().get(getIndex());
                    checkBox.setSelected(customer.getSelect().isSelected());
                    setGraphic(checkBox);
                }
            }
        });


        cell_action.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Delete");

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    int customerId = customer.getCustomerId();

                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                    confirm.setTitle("Delete Confirmation");
                    confirm.setHeaderText(null);
                    confirm.setContentText("Are you sure you want to delete Customer ID " + customerId + "?");

                    confirm.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try (Connection con = dbconn.connect();
                                 PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE customer_id = ?")) {
                                ps.setInt(1, customerId);
                                ps.executeUpdate();
                                showAlert("Success", "Customer deleted successfully.");
                                loadCustomerData();
                            } catch (SQLException ex) {
                                showAlert("Database Error", "Failed to delete customer.");
                                ex.printStackTrace();
                            }
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });

        tablecustomers.setOnMouseClicked(event -> {
            selectedUser = tablecustomers.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                txtmobilenumber.setText(String.valueOf(selectedUser.getPhone()));
            } else {
                txtmobilenumber.clear();
            }
        });
        ;

    }

    private void loadCustomerData() {
        customerList.clear();
        try (Connection con = dbconn.connect();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM customers");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("customer_id");
                int phone = rs.getInt("phone");
                String date = rs.getString("date");
                customerList.add(new Customer(id, phone, date));
            }
            tablecustomers.setItems(customerList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void clickadd(ActionEvent event) {
        String mobileText = txtmobilenumber.getText().trim();

        if (mobileText.isEmpty()) {
            showAlert("Validation Error", "Mobile number is required.");
            return;
        }

        try {
            int phone = Integer.parseInt(mobileText);

            try (Connection con = dbconn.connect();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO customers(phone, date) VALUES(?, ?)")) {
                ps.setInt(1, phone);
                ps.setDate(2, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
                showAlert("Success", "Customer added successfully.");
                txtmobilenumber.clear();
                loadCustomerData();
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Phone number must be numeric.");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add customer.");
            e.printStackTrace();
        }
    }

    @FXML
    void clicksend(ActionEvent event) {
        String message = txtmessage.getText().trim();
        if (message.isEmpty()) {
            showAlert("Validation Error", "Message cannot be empty.");
            return;
        }

        boolean anySelected = false;
        for (Customer customer : customerList) {
            if (customer.getSelect().isSelected()) {
                anySelected = true;
                // Simulate sending message (e.g., print to console or use real API)
                System.out.println("Sending to " + customer.getPhone() + ": " + message);
            }
        }

        if (!anySelected) {
            showAlert("Validation Error", "No customers selected.");
        } else {
            showAlert("Success", "Messages sent to selected customers.");
            txtmessage.clear();
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void clickedit(ActionEvent event) {
        Customer selected = tablecustomers.getSelectionModel().getSelectedItem();
        String newPhoneText = txtmobilenumber.getText().trim();

        if (selected == null) {
            showAlert("Validation Error", "Please select a customer to edit.");
            return;
        }

        if (newPhoneText.isEmpty()) {
            showAlert("Validation Error", "Mobile number is required.");
            return;
        }

        try {
            int newPhone = Integer.parseInt(newPhoneText);

            try (Connection con = dbconn.connect();
                 PreparedStatement ps = con.prepareStatement("UPDATE customers SET phone = ? WHERE customer_id = ?")) {
                ps.setInt(1, newPhone);
                ps.setInt(2, selected.getCustomerId());
                ps.executeUpdate();

                showAlert("Success", "Customer updated successfully.");
                txtmobilenumber.clear();
                loadCustomerData();

            } catch (SQLException e) {
                showAlert("Error", "Failed to update customer.");
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Phone number must be Numbers");
        }
    }

    public class Customer {
        private int customerId;
        private int phone;
        private String date;
        private CheckBox select;

        public Customer(int customerId, int phone, String date) {
            this.customerId = customerId;
            this.phone = phone;
            this.date = date;
            this.select = new CheckBox();
        }

        public int getCustomerId() { return customerId; }
        public int getPhone() { return phone; }
        public String getDate() { return date; }
        public CheckBox getSelect() { return select; }
    }

    public void clearfield(){
        txtmobilenumber.clear();
        txtmessage.clear();
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
            cell_action.setVisible(!"operator".equals(userRole));
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