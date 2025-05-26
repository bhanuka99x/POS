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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.*;
import java.sql.*;

public class InventoryManagementController {

    @FXML
    private Button btnadd;

    @FXML
    private Button btncustomers;

    @FXML
    private Button btndelete;

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
    private Button btnupdate;

    @FXML
    private Button btnupload;

    @FXML
    private TableColumn<InventoryItem, Image> imagecell;

    @FXML
    private ImageView imageview;

    @FXML
    private TableColumn<InventoryItem, String> namecell;

    @FXML
    private TableColumn<InventoryItem, Double> pricecell;

    @FXML
    private TableColumn<InventoryItem, Integer > quantitycell;
    @FXML
    private TableColumn<InventoryItem, Void> cell_action;

    @FXML
    private TableView<InventoryItem> tblinventory;

    @FXML
    private Label txtlbl,txtName;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtprice;

    @FXML
    private TextField txtquantity;

    private ObservableList<InventoryItem> Inventorylist = FXCollections.observableArrayList();
    private File selectedImageFile;
    @FXML
    public void initialize() {
          ConfigureInventorytable();
    }
    @FXML
    void clickadd(ActionEvent event) {
        String name = txtname.getText();
        int qty = Integer.parseInt(txtquantity.getText());
        double price = Double.parseDouble(txtprice.getText());

        try (Connection conn = dbconn.connect()) {
            String sql = "INSERT INTO inventory (item_name, quantity, price, image) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, name);
            stmt.setInt(2, qty);
            stmt.setDouble(3, price);

            if (selectedImageFile != null) {
                InputStream is = new FileInputStream(selectedImageFile);
                stmt.setBinaryStream(4, is, (int) selectedImageFile.length());
            } else {
                stmt.setNull(4, Types.BLOB);
            }

            stmt.executeUpdate();
            LoadInventoryData();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML
    void clickupdate(ActionEvent event) {
        InventoryItem selected = tblinventory.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String name = txtname.getText();
        int qty = Integer.parseInt(txtquantity.getText());
        double price = Double.parseDouble(txtprice.getText());

        try (Connection conn = com.posapp.dbconnection.dbconn.connect()) {
            String sql = "UPDATE inventory SET item_name = ?,quantity = ?, price = ?, image = ? WHERE inventory_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1,name);
            stmt.setInt(2, qty);
            stmt.setDouble(3, price);

            if (selectedImageFile != null) {
                InputStream is = new FileInputStream(selectedImageFile);
                stmt.setBinaryStream(4, is, (int) selectedImageFile.length());
            } else {
                stmt.setNull(4, Types.BLOB);
            }
            stmt.setInt(5,selected.getId());
            stmt.executeUpdate();
            LoadInventoryData();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void clickupload(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("image Files", "*.png","*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(btnupload.getScene().getWindow());

        if(file  !=null){
            selectedImageFile = file;
            imageview.setImage(new Image(file.toURI().toString()));
        }

    }



    public class InventoryItem{
        private int id;
        private String ItemName;
        private int ItemQuantity;
        private double ItemPrice;
        private ImageView ItemImage;

        public InventoryItem(int id,String Itemname, int ItemQuantity, double ItemPrice, byte[] imagedata){
            this.id = id;
            this.ItemName=Itemname;
            this.ItemQuantity = ItemQuantity;

            this.ItemPrice = ItemPrice;
            if(imagedata !=null){
                Image img = new Image(new ByteArrayInputStream(imagedata));
                this.ItemImage = new ImageView(img);
                this.ItemImage.setFitWidth(90);
                this.ItemImage.setFitHeight(90);
            }else {
                this.ItemImage = new ImageView();
            }
        }
        public int getId(){
            return id;
        }
        public String getItemname(){
            return ItemName;
        }
        public int getItemquantity(){
            return ItemQuantity;
        }
        public double getItemprice(){
            return ItemPrice;
        }
        public ImageView getItemimage(){
            return ItemImage;
        }
    }
    public void ConfigureInventorytable(){
        namecell.setCellValueFactory(new PropertyValueFactory<>("itemname"));
        quantitycell.setCellValueFactory(new PropertyValueFactory<>("itemquantity"));
        pricecell.setCellValueFactory(new PropertyValueFactory<>("itemprice"));
        pricecell.setCellFactory(column -> new TableCell<InventoryItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "$" + price);
            }
        });

        imagecell.setCellValueFactory(new PropertyValueFactory<>("itemimage"));
        cell_action.setCellFactory(col -> new TableCell<>() {
            public   Button removebutton = new Button("Remove");

            {
                removebutton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;-fx-font-family:Calibri;-fx-font-size:15px;-fx-pref-width: 80px;-fx-pref-height: 30px;");
                removebutton.setOnAction(actionEvent -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    if (item == null) return;

                    try (Connection conn = dbconn.connect()) {
                        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
                        PreparedStatement stmt = conn.prepareStatement(sql);
                        stmt.setInt(1, item.getId());
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            Inventorylist.remove(item); // remove from UI list
                            clearFields();
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

        LoadInventoryData();
        tblinventory.setOnMouseClicked(event -> {
            InventoryItem selectedItem = tblinventory.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                txtname.setText(selectedItem.getItemname());
                txtquantity.setText(String.valueOf(selectedItem.getItemquantity()));
                txtprice.setText(String.valueOf(selectedItem.getItemprice()));
                imageview.setImage(selectedItem.getItemimage().getImage());
            }
        });

    }
    private void LoadInventoryData(){
        Inventorylist.clear();
        try (Connection conn = dbconn.connect()){
            String sql = "SELECT * FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()){
                int id = rs.getInt("inventory_id");
                String name = rs.getString("item_name");
                int quan = rs.getInt("quantity");
                double price = rs.getDouble("price");
                byte [] imagebyte = rs.getBytes("image");

                Inventorylist.add(new InventoryItem(id,name,quan,price,imagebyte));
            }
            tblinventory.setItems(Inventorylist);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void clearFields() {
        txtname.clear();
        txtquantity.clear();
        txtprice.clear();
        imageview.setImage(null);
        selectedImageFile = null;
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
            btnadd.setDisable(true);
            btnupdate.setDisable(true);
            btnupload.setDisable(true);
            btnlogout.setDisable(false);
            txtname.setDisable(true);
            txtprice.setDisable(true);
            txtquantity.setDisable(true);
            cell_action.setVisible(!"operator".equals(userRole));
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