package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;

import com.posapp.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportsAnalyticsController {

    public void initialize(){
        LoadInventoryData();
        Configuretable();
        loadTotalIncomes();
    }

    @FXML private Button btncustomers, btninventory, btnlogout, btnmenu, btnope, btnorders, btnreports;
    @FXML private Label lblincome, lblnumcashpayment, lblnumofcardpayment, lblnumorders, lblttlcustomers, txtlbl;

    @FXML private TableColumn<BestSellingTable, Image> cell_image;
    @FXML private TableColumn<BestSellingTable,String> cell_name;
    @FXML private TableColumn<BestSellingTable, Integer> cell_quantity;
    @FXML private TableView<BestSellingTable> tblbestselling;
    private ObservableList<BestSellingTable> Inventorylist = FXCollections.observableArrayList();
    public class BestSellingTable {
        private int Id;
        private String name;
        private int quantity;
        private ImageView itemimage;

        public BestSellingTable(int Id, String name, int quantitySold, byte[] imagedata) {
            this.Id = Id;
            this.name = name;
            this.quantity = 50 - quantitySold;

            if (imagedata != null) {
                Image img = new Image(new ByteArrayInputStream(imagedata));
                this.itemimage = new ImageView(img);
                this.itemimage.setFitHeight(90);
                this.itemimage.setFitWidth(90);
            } else {
                this.itemimage = new ImageView();
            }
        }

        public int getId() { return Id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public ImageView getItemimage() { return itemimage; }
    }

    public void Configuretable(){
        cell_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        cell_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        cell_quantity.setSortType(TableColumn.SortType.DESCENDING);
        cell_image.setCellValueFactory(new PropertyValueFactory<>("itemimage"));
    }
    private void LoadInventoryData() {
        Inventorylist.clear();

        try (Connection conn = dbconn.connect()) {
            String sql = "SELECT * FROM inventory";

            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("inventory_id");
                String name = rs.getString("item_name");
                int quan = rs.getInt("quantity");
                byte[] imageBytes = rs.getBytes("image");




                Inventorylist.add(new BestSellingTable(id, name,quan , imageBytes));
            }


            tblbestselling.setItems(Inventorylist);
            cell_quantity.setSortType(TableColumn.SortType.DESCENDING);
            tblbestselling.getSortOrder().setAll(cell_quantity);
            tblbestselling.sort();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTotalIncomes(){
        try(Connection conn = dbconn.connect()){
            String Totalincome = "SELECT SUM(total) AS total_income FROM payments";
            try(PreparedStatement stmt = conn.prepareStatement(Totalincome);
                ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    double income = rs.getDouble("total_income");
                    lblincome.setText("$ " + String.format("%.2f", income));
                }

            }

            String orderCountQuery = "SELECT COUNT(*) AS order_count FROM payments";
            try (PreparedStatement stmt = conn.prepareStatement(orderCountQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int orders = rs.getInt("order_count");
                    lblnumorders.setText(String.valueOf(orders));
                }
            }

            String cashQuery = "SELECT SUM(total) AS cash_total FROM payments WHERE payment_option = 'Cash'";
            try (PreparedStatement stmt = conn.prepareStatement(cashQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double cash = rs.getDouble("cash_total");
                    lblnumcashpayment.setText("$ " + String.format("%.2f", cash));
                }
            }

            String cardQuery = "SELECT SUM(total) AS card_total FROM payments WHERE payment_option = 'Card'";
            try (PreparedStatement stmt = conn.prepareStatement(cardQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double card = rs.getDouble("card_total");
                    lblnumofcardpayment.setText("$ " + String.format("%.2f", card));
                }
            }

            String query = "SELECT COUNT(*) AS total FROM customers";
            try (Connection con = dbconn.connect();
                 PreparedStatement ps = con.prepareStatement(query);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int total = rs.getInt("total");
                    lblttlcustomers.setText(String.valueOf(total));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                lblttlcustomers.setText("0");
            }


        }catch (SQLException e){
            e.printStackTrace();
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

        } else if ("operator".equals(userRole)) {
            btnmenu.setDisable(false);
            btncustomers.setDisable(false);
            btninventory.setDisable(false);
            btnorders.setDisable(true);
            btnreports.setDisable(true);
            btnope.setDisable(true);
            btnlogout.setDisable(false);
            txtlbl.setText(userRole);
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