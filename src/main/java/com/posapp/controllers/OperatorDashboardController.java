package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;
import com.posapp.receipt.ReceiptGenerator;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class OperatorDashboardController {

    // FXML fields
    @FXML private Button btncustomers, btninventory, btnlogout, btnmenu, btnorders, btnreports;
    @FXML private Label txtlbl, lbltotal, lbltax, subtotal, lblamount, lbldiscount;
    @FXML private TextField txtamount, txtdiscount;
    @FXML private ComboBox<String> cmb_payment_method;

    // Product Table
    @FXML private TableView<Product> tbl_product_display;
    @FXML private TableColumn<Product, String> product_name;
    @FXML private TableColumn<Product, Double> price;
    @FXML private TableColumn<Product, Image> product_image;
    @FXML private TableColumn<Product, Integer> add_quantity;
    @FXML private TableColumn<Product, Void> action;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    // Receipt Table
    @FXML private TableView<ReceiptItem> tbl_receipt;
    @FXML private TableColumn<ReceiptItem, String> receipt_product_name;
    @FXML private TableColumn<ReceiptItem, Integer> receipt_quantity;
    @FXML private TableColumn<ReceiptItem, Double> receipt_price;
    @FXML private TableColumn<ReceiptItem, Void> re_action;

    private final ObservableList<ReceiptItem> receiptList = FXCollections.observableArrayList();

    private double calculatedSubTotal = 0.0;
    private double calculatedTax = 0.0;
    private double calculatedDiscount = 0.0;
    private double calculatedTotal = 0.0;

    public void initialize() {
        cmb_payment_method.setItems(FXCollections.observableArrayList("Cash", "Card"));
        loadInventory();
        configureProductTable();
        configureReceiptTable();
        configurePaymentLogic();

        txtdiscount.setOnAction(e -> totalprice());

        txtdiscount.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case BACK_SPACE:
                    if (txtdiscount.getText().length() <= 1) {
                        txtdiscount.clear();
                        calculatedDiscount = 0.0;
                        totalprice();
                    }
                    break;
                default:
                    break;
            }
        });

    }

    private void configurePaymentLogic() {
        cmb_payment_method.setOnAction(event -> {
            if ("Card".equals(cmb_payment_method.getValue())) {
                txtamount.setDisable(true);
                lblamount.setDisable(true);
            } else {
                txtamount.setDisable(false);
                lblamount.setDisable(false);
            }
        });
    }

    public void totalprice(){
        double total = receiptList.stream().mapToDouble(ReceiptItem::getPrice).sum();
        double taxRate = 0.15;
        calculatedTax = total * taxRate;
        calculatedSubTotal = total + calculatedTax;

        lbltotal.setText(String.format("$%.2f", total));
        lbltax.setText(String.format("$%.2f", calculatedTax));
        subtotal.setText(String.format("$%.2f", calculatedSubTotal));

        try {
            double discountPercent = Double.parseDouble(txtdiscount.getText());
            if (discountPercent < 0 || discountPercent > 100) throw new NumberFormatException();
            calculatedDiscount = calculatedSubTotal * discountPercent / 100;
        } catch (NumberFormatException e) {
            calculatedDiscount = 0.0;
        }

        lbldiscount.setText(String.format("$%.2f", calculatedDiscount));
        calculatedTotal = calculatedSubTotal - calculatedDiscount;
        subtotal.setText(String.format("$%.2f", calculatedTotal));
    }

    public void click_pay(ActionEvent event) {
        String paymentOption = cmb_payment_method.getValue();
        if (paymentOption == null) {
            showAlert("Payment Error", "Please select a payment method.");
            return;
        }

        if (receiptList.isEmpty()) {
            showAlert("Receipt Error", "Receipt is empty.");
            return;
        }

        totalprice();

        try (Connection conn = dbconn.connect()) {
            String sql = """
        INSERT INTO payments (payment_option, sub_total, tax, discount, total, payment_date, payment_time)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, paymentOption);
            stmt.setDouble(2, calculatedSubTotal);
            stmt.setDouble(3, calculatedTax);
            stmt.setDouble(4, calculatedDiscount);
            stmt.setDouble(5, calculatedTotal);
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setTime(7, Time.valueOf(LocalTime.now()));

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                // ✅ Ask if user wants to generate receipt
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Generate Receipt");
                confirm.setHeaderText("Do you want to generate a receipt?");
                confirm.setContentText("Click OK to generate the receipt, or Cancel to skip.");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    ReceiptGenerator.generateReceipt(
                            receiptList,
                            calculatedSubTotal,
                            calculatedTax,
                            calculatedDiscount,
                            calculatedTotal,
                            paymentOption
                    );
                }

                showAlert("Success", "Payment recorded successfully!");
                receiptList.clear();
                totalprice();
                txtdiscount.clear();
                cmb_payment_method.getSelectionModel().clearSelection();
            } else {
                showAlert("Database Error", "Failed to record payment.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Exception", "Error inserting payment: " + e.getMessage());
        }
    }



    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void click_add_discount(ActionEvent event) {
    }

    public void click_remove_discount(ActionEvent event) {
    }

    // ------------ Product & Receipt Logic (No Changes) ------------

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

    private void configureProductTable() {
        product_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "$" + price);
            }
        });

        product_image.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override protected void updateItem(Image img, boolean empty) {
                super.updateItem(img, empty);
                if (empty || img == null) setGraphic(null);
                else {
                    imageView.setImage(img);
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    setGraphic(imageView);
                }
            }
        });
        product_image.setCellValueFactory(new PropertyValueFactory<>("image"));

        add_quantity.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(getTableView().getItems().get(getIndex()).getSpinner());
            }
        });

        action.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button("Add");
            {
                addButton.setOnAction(e -> {
                    Product p = getTableView().getItems().get(getIndex());
                    int qty = p.getSpinner().getValue();
                    double total = qty * p.getPrice();
                    receiptList.add(new ReceiptItem(p.getName(), qty, total));
                    totalprice();
                });
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : addButton);
            }
        });

        tbl_product_display.setItems(productList);
    }

    private void configureReceiptTable() {
        receipt_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        receipt_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        receipt_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        receipt_price.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "$" + price);
            }
        });

        re_action.setCellFactory(col -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setOnAction(e -> {
                    ReceiptItem item = getTableView().getItems().get(getIndex());
                    receiptList.remove(item);
                    totalprice();
                });
            }

            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeButton);
            }
        });

        tbl_receipt.setItems(receiptList);
    }

    private void loadInventory() {
        try (Connection conn = dbconn.connect()) {
            String query = "SELECT * FROM inventory";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                InputStream is = rs.getBinaryStream("image");
                Image img = is != null ? new Image(is) : null;
                Product product = new Product(
                        rs.getInt("inventory_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        img
                );
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String userRole;
    public void setUserRole(String role) {
        this.userRole = role;
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
        }
    }


    public void clickmenu(ActionEvent event) {

    }
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
    public void clickcustomers(ActionEvent event) {}
    public void clickorders(ActionEvent event) {}
    public void clickreports(ActionEvent event) {}
    public void clicklogout(ActionEvent event) {}
    public void clickreceipt(ActionEvent event) {}
    public void clickremove(ActionEvent event) {}
    public void clickope(ActionEvent event) {}
}
