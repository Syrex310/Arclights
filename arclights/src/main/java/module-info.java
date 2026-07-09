module com.arclights {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.arclights to javafx.fxml;
    exports com.arclights;
}
