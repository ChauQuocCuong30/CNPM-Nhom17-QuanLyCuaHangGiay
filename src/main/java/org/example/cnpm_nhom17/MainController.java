package org.example.cnpm_nhom17;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {

        welcomeText.setText("Hệ thống Quản lý Giày Nike đã kết nối thành công!");
    }
}
