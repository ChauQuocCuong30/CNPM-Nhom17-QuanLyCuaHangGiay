module org.example.cnpm_nhom17 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens org.example.cnpm_nhom17 to javafx.fxml;
    exports org.example.cnpm_nhom17;
}