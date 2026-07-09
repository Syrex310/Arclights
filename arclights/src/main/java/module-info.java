module com.arclights {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.arclights to javafx.fxml;
    exports com.arclights;
}
