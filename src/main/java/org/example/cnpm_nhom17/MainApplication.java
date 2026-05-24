package org.example.cnpm_nhom17;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.SQLite;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // 1. Khởi tạo Cơ sở dữ liệu
        SQLite.CreateDatabase();

        // 2. Tải giao diện màn hình chính
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 1024, 768);

        stage.setTitle("Hệ thống Quản lý Cửa hàng Giày Nike");
        stage.setScene(scene);
        stage.show();
    }

    static void main(String[] args) {
        launch();
    }
}
