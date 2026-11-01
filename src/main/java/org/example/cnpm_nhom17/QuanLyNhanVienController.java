package org.example.cnpm_nhom17;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.XuLyNhanVien;
import org.example.cnpm_nhom17.models.NhanVien;

import java.io.IOException;
import java.util.List;

public class QuanLyNhanVienController {

    @FXML private TableView<NhanVien> bangNhanVien;
    @FXML private TextField txtTimKiem;
    @FXML private TextField txtHoTen;
    @FXML private ComboBox<String> cbChucVu;

    private ObservableList<NhanVien> danhSachNhanVien = FXCollections.observableArrayList();
    private NhanVien nhanVienDangChon = null;

    @FXML
    public void initialize() {
        caiDatCotChoBang();
        taiDuLieuNhanVien();

        // Nhấp vào bảng để sửa
        bangNhanVien.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                nhanVienDangChon = bangNhanVien.getSelectionModel().getSelectedItem();
                if (nhanVienDangChon != null) {
                    txtHoTen.setText(nhanVienDangChon.getHoTen());
                    cbChucVu.setValue(nhanVienDangChon.getChucVu());
                }
            }
        });

        // Tìm kiếm live theo tên
        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> locDuLieuNhanVien(newValue));
    }

    private void caiDatCotChoBang() {
        TableColumn<NhanVien, Integer> colMa = (TableColumn<NhanVien, Integer>) bangNhanVien.getColumns().get(0);
        colMa.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));

        TableColumn<NhanVien, String> colTen = (TableColumn<NhanVien, String>) bangNhanVien.getColumns().get(1);
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        TableColumn<NhanVien, String> colChucVu = (TableColumn<NhanVien, String>) bangNhanVien.getColumns().get(2);
        colChucVu.setCellValueFactory(new PropertyValueFactory<>("chucVu"));

        bangNhanVien.setItems(danhSachNhanVien);
    }

    @FXML
    private void taiDuLieuNhanVien() {
        danhSachNhanVien.clear();
        List<NhanVien> data = XuLyNhanVien.layDanhSachNhanVien();
        if (data != null) danhSachNhanVien.addAll(data);
        xuLyXoaTrangForm();
    }

    private void locDuLieuNhanVien(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            bangNhanVien.setItems(danhSachNhanVien);
            return;
        }
        ObservableList<NhanVien> listLoc = FXCollections.observableArrayList();
        for (NhanVien nv : danhSachNhanVien) {
            if (nv.getHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                listLoc.add(nv);
            }
        }
        bangNhanVien.setItems(listLoc);
    }

    @FXML
    private void xuLyThemNhanVien() {
        String hoTen = txtHoTen.getText().trim();
        String chucVu = cbChucVu.getValue();

        if (hoTen.isEmpty() || chucVu == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Tên và chọn Chức vụ!");
            return;
        }

        if (XuLyNhanVien.themNhanVien(hoTen, chucVu)) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm nhân viên mới!");
            taiDuLieuNhanVien();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", "Đã có lỗi xảy ra trong database!");
        }
    }

    @FXML
    private void xuLyCapNhatNhanVien() {
        if (nhanVienDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn nhân viên trên bảng!");
            return;
        }
        String hoTen = txtHoTen.getText().trim();
        String chucVu = cbChucVu.getValue();

        if (XuLyNhanVien.capNhatNhanVien(nhanVienDangChon.getMaNhanVien(), hoTen, chucVu)) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thành công!");
            taiDuLieuNhanVien();
        }
    }

    @FXML
    private void xuLyXoaNhanVien() {
        if (nhanVienDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn nhân viên cần xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa nhân viên " + nhanVienDangChon.getHoTen() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            if (XuLyNhanVien.xoaNhanVien(nhanVienDangChon.getMaNhanVien())) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa nhân viên!");
                taiDuLieuNhanVien();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa nhân viên này (Có thể do ràng buộc phiếu nhập/hóa đơn).");
            }
        }
    }

    @FXML
    private void xuLyXoaTrangForm() {
        txtHoTen.clear();
        cbChucVu.getSelectionModel().clearSelection();
        nhanVienDangChon = null;
        bangNhanVien.getSelectionModel().clearSelection();
    }

    private void hienThiThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe); alert.setHeaderText(null); alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @FXML
    private void quayVeTrangBanHang(ActionEvent event) {
        try {
            String absolutePath = "/org/example/cnpm_nhom17/main-view.fxml";
            java.net.URL url = getClass().getResource(absolutePath);

            if (url == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi đường dẫn");
                alert.setContentText("Không tìm thấy file giao diện: main-view.fxml");
                alert.showAndWait();
                return;
            }

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(FXMLLoader.load(url));

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}