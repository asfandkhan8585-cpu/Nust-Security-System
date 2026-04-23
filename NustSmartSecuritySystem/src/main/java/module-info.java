module com.campus.security {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.campus.security.controller to javafx.fxml;
    exports com.campus.security;
}