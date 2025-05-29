package com.posapp.controllers;

import com.posapp.dbconnection.dbconn;
import com.posapp.receipt.ReceiptGenerator;
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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.ProgressIndicator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class

 OperatorDashboardController {

    // FXML fields
    @FXML private Button btncustomers, btninventory, btnlogout, btnmenu, btnorders, btnreports,btnope;
    @FXML private Label txtlbl, lbltotal, lbltax, subtotal, lblamount, lbldiscount,txtName,lbl_re_balance,lblre_balance;
    @FXML private TextField txtamount, txtdiscount;
     @FXML private ComboBox<String> cmb_payment_method;
     @FXML private ProgressIndicator progressindicator;


    public void initialize() {
        cmb_payment_method.setItems(FXCollections.observableArrayList("Cash", "Card"));
        loadInventory();
        configureProductTable();
        configureReceiptTable();
        configurePaymentLogic();

        txtamount.setOnAction(  e -> totalprice());
        txtamount.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case BACK_SPACE:
                    if (txtamount.getText().length() <= 1) {
                        txtamount.clear();
                        calculatedAmount = 0.0;
                        totalprice();
                    }
                    break;
                default:
                    break;
            }
        });

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
                lblre_balance.setDisable(true);
                lbl_re_balance.setDisable(true);

            } else {
                txtamount.setDisable(false);
                lblamount.setDisable(false);
                lblre_balance.setDisable(false);
                lbl_re_balance.setDisable(false);
            }
        });
    }

    private double calculatedSubTotal = 0.0;
    private double calculatedTax = 0.0;
    private double calculatedDiscount = 0.0;
    private double calculatedTotal = 0.0;
    private double calculatedAmount =0.0;

    public void totalprice(){

        double total = receiptList.stream().mapToDouble(ReceiptItem::getPrice).sum();
        double taxRate = 0.15;
        calculatedTax = total * taxRate;
        calculatedSubTotal = total + calculatedTax;

        lbltotal.setText(String.format("$%.2f ", total));
        lbltax.setText(String.format("$%.2f ", calculatedTax));
        subtotal.setText(String.format("$%.2f ", calculatedSubTotal));

        try {
            double discountPercent = Double.parseDouble(txtdiscount.getText());
            if (discountPercent < 0 || discountPercent > 100) throw new NumberFormatException();
            calculatedDiscount = calculatedSubTotal * discountPercent / 100;
        } catch (NumberFormatException e) {
            calculatedDiscount = 0.0;
        }
        try {
            double amount = Double.parseDouble(txtamount.getText());
            calculatedAmount = amount - calculatedSubTotal;

            lbl_re_balance.setText(String.format("$%.2f ", calculatedAmount));
        } catch (NumberFormatException ex) {
            lbl_re_balance.setText("0.00");
        }
        lbldiscount.setText(String.format("$%.2f ", calculatedDiscount));
        calculatedTotal = calculatedSubTotal - calculatedDiscount;
        subtotal.setText(String.format("$%.2f ", calculatedTotal));
    }

    public void click_pay(ActionEvent event) {
        String paymentOption = cmb_payment_method.getValue();
        if (paymentOption == null) {
          Alerts.showError("Error","Please select a payment method ");
            return;
        }

        if (receiptList.isEmpty()) {
            Alerts.showError("Error","Receipt is empty");
            return;
        }
        totalprice();

        try (Connection conn = dbconn.connect()) {
            int paymentId = -1; // Default if something fails

            String sql = """
    INSERT INTO payments (payment_option, sub_total, tax, discount, total, payment_date, payment_time, taken_items)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?)""";

            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, paymentOption);
            stmt.setDouble(2, calculatedSubTotal);
            stmt.setDouble(3, calculatedTax);
            stmt.setDouble(4, calculatedDiscount);
            stmt.setDouble(5, calculatedTotal);
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            stmt.setTime(7, Time.valueOf(LocalTime.now()));
            stmt.setString(8, receiptList.toString());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    paymentId = rs.getInt(1); // Store the generated payment_id
                }

                for (ReceiptItem item : receiptList) {
                    String updateInventorySql = "UPDATE inventory SET quantity = quantity - ? WHERE item_name = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateInventorySql)) {
                        updateStmt.setInt(1, item.getQuantity());
                        updateStmt.setString(2, item.getProductName());
                        updateStmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alerts.showError("Error", "Error updating inventory for " + item.getProductName());
                    }
                }

                // generate receipt
                if (Alerts.showconfirmation("Generate Receipt", "Do you want to generate a receipt?", "Click OK to generate the receipt, or Cancel to skip.")) {
                    ReceiptGenerator.generateReceipt(
                            receiptList,
                            calculatedSubTotal,
                            calculatedTax,
                            calculatedDiscount,
                            calculatedTotal,
                            paymentOption,
                            paymentId
                    );
                }

                Alerts.showSuccess("Success", "Payment recorded successfully!");
                receiptList.clear();
                totalprice();
                txtdiscount.clear();
                cmb_payment_method.getSelectionModel().clearSelection();
                txtamount.clear();
            } else {
                Alerts.showError("Error", "Failed to record payment.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            Alerts.showError("Error","Error inserting payment: " + e.getMessage());
        }
    }

    // ------------ Product & Receipt Logic ------------

    // Product Table
    @FXML private TableView<Product> tbl_product_display;
    @FXML private TableColumn<Product, String> product_name;
    @FXML private TableColumn<Product, Double> price;
    @FXML private TableColumn<Product, Image> product_image;
    @FXML private TableColumn<Product, Integer> add_quantity;
    @FXML private TableColumn<Product, Void> action;

    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    private void configureProductTable() {
        product_name.setCellValueFactory(new PropertyValueFactory<>("name"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        price.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "$ " + price);
            }
        });

        product_image.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            @Override protected void updateItem(Image img, boolean empty) {
                super.updateItem(img, empty);
                if (empty || img == null) setGraphic(null);
                else {
                    imageView.setImage(img);
                    imageView.setFitHeight(90);
                    imageView.setFitWidth(90);
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
                addButton.setStyle("-fx-background-color: #fd6f6f; -fx-text-fill: white; -fx-font-weight: bold;-fx-font-family:Calibri;-fx-font-size:15px;-fx-pref-width: 100px;-fx-pref-height: 30px;");
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

    // Receipt Table
    @FXML private TableView<ReceiptItem> tbl_receipt;
    @FXML private TableColumn<ReceiptItem, String> receipt_product_name;
    @FXML private TableColumn<ReceiptItem, Integer> receipt_quantity;
    @FXML private TableColumn<ReceiptItem, Double> receipt_price;
    @FXML private TableColumn<ReceiptItem, Void> re_action;

    private final ObservableList<ReceiptItem> receiptList = FXCollections.observableArrayList();

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

        @Override
        public String toString() {
            return String.format("'%s qty=%d'", productName, quantity);
        }
    }

    private void configureReceiptTable() {
        receipt_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        receipt_quantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        receipt_price.setCellValueFactory(new PropertyValueFactory<>("price"));
        receipt_price.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : "$ " + price);
            }
        });

        re_action.setCellFactory(col -> new TableCell<>() {
            private final Button removeButton = new Button("Remove");
            {
                removeButton.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-font-weight: bold;-fx-font-family:Calibri;-fx-font-size:15px;-fx-pref-width: 80px;-fx-pref-height: 30px;");
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
