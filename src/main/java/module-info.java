module org.example.cnpm_nhom17 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires org.apache.poi.ooxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.desktop;
    requires java.prefs;

    opens org.example.cnpm_nhom17 to javafx.fxml;
    exports org.example.cnpm_nhom17;

    opens org.example.cnpm_nhom17.models to javafx.base;

    opens org.example.cnpm_nhom17.Controller to javafx.fxml, javafx.base;
    exports org.example.cnpm_nhom17.Controller;

    opens org.example.cnpm_nhom17.database_helper to javafx.fxml, javafx.base;
}