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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

public class OperatorDashboardController {

    @FXML private Button btncustomers;
    @FXML private Button btninventory;
    @FXML private Button btnlogout;
    @FXML private Button btnmenu;
    @FXML private Button btnorders;
    @FXML private Button btnreports;
    @FXML private Label txtlbl;
    @FXML private Label lbltotal;
    @FXML private Label lbltax;
    @FXML private Label subtotal;
    @FXML private ComboBox<String> cmb_payment_method;

    public void clickmenu(ActionEvent event) {}
    public void clickinventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/posapp/views/dashboard_screen_inventory.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        Stage current = (Stage) txtlbl.getScene().getWindow();
        current.close();

    }
    public void clickcustomers(ActionEvent event) {}
    public void clickorders(ActionEvent event) {}
    public void clickreports(ActionEvent event) {}
    public void clickope(ActionEvent event) {}
    public void clicklogout(ActionEvent event) {}
    public void clickremove(ActionEvent event) {}
    public void click_pay(ActionEvent event) {}
    public void clickreceipt(ActionEvent event) {}
    public void click_add_custormer(ActionEvent event) {}

    public void initialize() {
        loadInventory();
        configureProductTable();
        configureReceiptTable();
        setupcombo();
        //totalprice();
    }



  //product table
    @FXML private TableView<Product> tbl_product_display;
    @FXML private TableColumn<Product, String> product_name;
    @FXML private TableColumn<Product, Double> price;
    @FXML private TableColumn<Product, Image> product_image;
    @FXML private TableColumn<Product, Integer> add_quantity;
    @FXML private TableColumn<Product, Void> action;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    public static class Product {
        private final int id;
        private final String name;
        private final int quantity;
        private final double price;
        private final Image image;
        private final Spinner<Integer> spinner;

        public Product(int id, String name, int quantity, double price, Image image) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.image = image;
            this.spinner = new Spinner<>(1, quantity, 1);
            this.spinner.setEditable(true);
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        public Image getImage() { return image; }
        public Spinner<Integer> getSpinner() { return spinner; }
    }

    private void configureProductTable() {
        product_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setCellFactory(column -> new TableCell<Product, Double>(){
            @Override
            protected void updateItem(Double price,boolean empty){
                super.updateItem(price,empty);
                setText(empty || price == null ? null : "$" + price );
            }
        });

        product_image.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(Image img, boolean empty) {
                super.updateItem(img, empty);
                if (empty || img == null) {
                    setGraphic(null);
                } else {
                    imageView.setImage(img);
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });
        product_image.setCellValueFactory(new PropertyValueFactory<>("image"));

        add_quantity.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    setGraphic(product.getSpinner());
                }
            }
        });

        action.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add");

            {
                addButton.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    int qty = product.getSpinner().getValue();
                    double total = qty * product.getPrice();
                    ReceiptItem item = new ReceiptItem(product.getName(), qty, total);
                    receiptList.add(item);
                    totalprice();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });
    }

 // receipt table

    @FXML private TableView<ReceiptItem> tbl_receipt;
    @FXML private TableColumn<ReceiptItem, String> receipt_product_name;
    @FXML private TableColumn<ReceiptItem, Integer> receipt_quantity;
    @FXML private TableColumn<ReceiptItem, Double> receipt_price;
    @FXML private TableColumn<ReceiptItem, Void >re_action;

    private final ObservableList<ReceiptItem> receiptList = FXCollections.observableArrayList();

    private void configureReceiptTable() {
        receipt_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        receipt_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        receipt_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        receipt_price.setCellFactory(column ->new TableCell<ReceiptItem,Double>(){
            @Override
            protected void updateItem(Double price,boolean empty){
                super.updateItem(price,empty);
                setText(empty ||price == null ? null : "$" + price);
            }
        });



        tbl_receipt.setItems(receiptList);
    }

    public static class ReceiptItem {
        private final String productName;
        private final int quantity;
        private final double price;

        public ReceiptItem(String productName, int quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
    }

    private void setupcombo(){
        cmb_payment_method.setItems(FXCollections.observableArrayList("Cash","Card"));
    }

    public void totalprice(){
        double total =0;
        double taxrate = 0.15;
        double tax;
        double sub_total;

        for (ReceiptItem item : receiptList){
            total +=item.getPrice();
        }
        lbltotal.setText(String.format("$%.2f " , total));
        tax = taxrate * total;
        lbltax.setText(String.format("$%.2f " , tax));
        sub_total = tax + total;
        subtotal.setText(String.format("$%.2f ", sub_total));
        System.out.println(total);

    }


    private void loadInventory() {
        try (Connection conn = dbconn.connect()) {
            String query = "SELECT * FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InputStream is = rs.getBinaryStream("image");
                Image img = null;
                if (is != null) {
                    img = new Image(is);
                }

                Product product = new Product(
                        rs.getInt("inventory_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        img
                );
                productList.add(product);
            }
            tbl_product_display.setItems(productList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private String userRole;

    public void setUserRole(String role) {
        this.userRole = role;
        System.out.println("User role in dashboard: " + userRole);
        configureDashboard();
    }

    private void configureDashboard() {
        if ("admin".equals(userRole)) {
            btnmenu.setVisible(true);
            btncustomers.setVisible(true);
            btninventory.setVisible(true);
            btnorders.setVisible(true);
            btnreports.setVisible(true);
            btnlogout.setVisible(true);
            txtlbl.setText(userRole);
        } else if ("operator".equals(userRole)) {
            btnmenu.setVisible(true);
            btninventory.setVisible(true);
            btncustomers.setVisible(true);
            btnorders.setVisible(false);
            btnreports.setVisible(false);
            btnlogout.setVisible(true);
            txtlbl.setText(userRole);
        } else {
            System.out.println("Invalid role detected: " + userRole);
        }
    }
}
