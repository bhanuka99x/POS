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

public class AdminDashboardController {


    @FXML
    private Button btnadd;

    @FXML
    private Button btncustomers;

    @FXML
    private Button btndelete;

    @FXML
    private Button btnedit;

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
    private Button btnreports;

    @FXML
    private TableView<UserRow> tblusers;

    @FXML
    private TableColumn<UserRow, Integer> colId;

    @FXML
    private TableColumn<UserRow,String> colUser;

    @FXML
    private TableColumn<UserRow,String> colPass;

    @FXML
    private TableColumn<UserRow,String> colRole;
    @FXML
    private TableColumn<UserRow,Void> cell_action;

    @FXML
    private Label txtlbl,txtName;

    @FXML
    private TextField txtpassword;

    @FXML
    private TextField txtusername;

    @FXML
    private ComboBox<String> cmbrole;

    private final ObservableList<UserRow> userList = FXCollections.observableArrayList();
    private UserRow selectedUser = null;



    @FXML
    public void initialize(){
        cmbrole.setItems(FXCollections.observableArrayList("admin", "operator"));
        colUser.setCellValueFactory(new PropertyValueFactory<>("username"));
        colPass.setCellValueFactory(new PropertyValueFactory<>("password"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        cell_action.setCellFactory(e->new TableCell<>(){
            private final Button removebutton = new Button("Remove");
            {
                removebutton.setStyle("-fx-background-color: #fd6f6f; -fx-text-fill: white; -fx-font-weight: bold;-fx-font-family:Calibri;-fx-font-size:15px;-fx-pref-width: 80px;-fx-pref-height: 30px;");
                removebutton.setOnAction(actionEvent -> {
                    if (Alerts.showconfirmation("Delete", "", "Are You sure you want to delete this user ? ")) {
                        UserRow item = getTableView().getItems().get(getIndex());
                        if (item == null) return;

                        try (Connection conn = dbconn.connect()) {
                            String sql = "DELETE FROM users WHERE user_id = ?";
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, item.getId());
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                userList.remove(item);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
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
        tblusers.setItems(userList);
        loaduser();

        tblusers.setOnMouseClicked(event ->{
            selectedUser = tblusers.getSelectionModel().getSelectedItem();
            txtusername.setText(selectedUser.getUsername());
            txtpassword.setText(selectedUser.getPassword());
            cmbrole.setValue(selectedUser.getRole());

        } );


    }
   private void loaduser(){
        String sql = "SELECT * FROM users";

        try(Connection conn= dbconn.connect();
            Statement stmt = conn.createStatement();
            ResultSet rs= stmt.executeQuery(sql)){
            userList.clear();
            while(rs.next()){
                int id = rs.getInt("user_id");
                String username = rs.getString("user_name");
                String password = rs.getString("password_hash");
                String role = rs.getString("role");
               userList.add(new UserRow(id,username, password, role));
            }
            tblusers.setItems(userList);

        }catch (SQLException e){
            e.printStackTrace();
        }
   }

    @FXML
    public void clickadd(ActionEvent event) {
        String username = txtusername.getText().trim();
        String password = txtpassword.getText().trim();
        String role = cmbrole.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            Alerts.showError("Error","Please fill in all fields");
            return;
        }

        String checkSql = "SELECT * FROM users WHERE user_name = ?";
        try (Connection conn = dbconn.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
             checkStmt.setString(1, username);
             ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                Alerts.showError("Error", "Username already exists. Please choose another.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String insertSql = "INSERT INTO users(user_name, password_hash, role) VALUES (?, ?, ?)";
        try (Connection conn = dbconn.connect();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            loaduser();
            txtusername.clear();
            txtpassword.clear();
            cmbrole.setValue(null);
            Alerts.showSuccess("Success", "User added successfully.");
            clearfield();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void clickedit(ActionEvent event) {
        if (selectedUser == null) return;

        String username = txtusername.getText();
        String password = txtpassword.getText();
        String role = cmbrole.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            Alerts.showError("Error", "All fields are required.");
            return;
        }

        String duplicateCheck = "SELECT * FROM users WHERE (user_name = ? OR password_hash = ?) AND user_id != ?";
        try (Connection conn = dbconn.connect();
             PreparedStatement checkStmt = conn.prepareStatement(duplicateCheck)) {

            checkStmt.setString(1, username);
            checkStmt.setString(2, password);
            checkStmt.setInt(3, selectedUser.getId());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                Alerts.showError("Error", "Username or Password already exists.");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "UPDATE users SET user_name = ?, password_hash = ?, role = ? WHERE user_id = ?";
        try (Connection conn = dbconn.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.setInt(4, selectedUser.getId());
            stmt.executeUpdate();

            loaduser();
            Alerts.showSuccess("Success", "User updated successfully.");
            clearfield();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class UserRow{
        private final  int id;
        private final String username;
        private final String password;
        private final String role;

        public UserRow( int id,String username,String password,String role){
            this.id = id;
            this.username = username;
            this.password = password;
            this.role = role;
        }
        public int getId() {
            return id;
        }

        public String getUsername(){
            return username;
        }
        public String getPassword(){
            return password;
        }
        public String getRole(){
            return role;
        }
    }

    public void clearfield(){
        txtpassword.clear();
        txtusername.clear();
        cmbrole.setValue(null);
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