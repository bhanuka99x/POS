module com.posapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.posapp.controllers to javafx.fxml;
    exports com.posapp;
}