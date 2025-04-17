module com.posapp {
    requires javafx.fxml;
    requires com.almasb.fxgl.scene;
    requires javafx.controls;

    opens com.posapp.controllers to javafx.fxml;
    exports com.posapp;
}