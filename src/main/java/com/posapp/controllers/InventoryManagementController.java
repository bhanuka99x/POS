package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
    private TableView<InventoryItem> tblinventory;

    @FXML
    private Label txtlbl;

    @FXML
    private TextField txtname;

    @FXML
    private TextField txtprice;

    @FXML
    private TextField txtquantity;

    private ObservableList<InventoryItem> Inventorylist = FXCollections.observableArrayList();
    private File selectedImageFile;

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
    void clickcustomers(ActionEvent event) {

    }

    @FXML
    void clickdelete(ActionEvent event) {
        InventoryItem selected = tblinventory.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        try (Connection conn = com.posapp.dbconnection.dbconn.connect()) {
            String sql = "DELETE FROM inventory WHERE item_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, selected.getItemname());
            stmt.executeUpdate();
            LoadInventoryData();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
    @FXML
    public void initialize() {

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
                this.ItemImage.setFitWidth(50);
                this.ItemImage.setFitHeight(50);
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


}
