package org.example.cnpm_nhom17.Controller;

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
import org.example.cnpm_nhom17.database_helper.XuLyKhachHang;
import org.example.cnpm_nhom17.models.KhachHang;

import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;

public class QuanLyKhachHangController {
    @FXML private TableView<KhachHang> bangKhachHang;
    @FXML private TextField txtTimKiem;
    @FXML private TextField txtHoTen;
    @FXML private TextField txtSoDienThoai;
    @FXML private TextField txtDiemTichLuy;
    @FXML private TextField txtTiLeQuyDoi;

    private ObservableList<KhachHang> danhSachKhachHang = FXCollections.observableArrayList();
    private KhachHang khachHangDangChon = null;
    public static int tiLeQuyDoiHienTai = 10000;
    private Preferences prefs = Preferences.userNodeForPackage(QuanLyKhachHangController.class);

    @FXML
    public void initialize() {
        caiDatCotChoBang();
        taiDuLieuKhachHang();

        tiLeQuyDoiHienTai = prefs.getInt("tiLeQuyDoi", 10000);
        txtTiLeQuyDoi.setText(String.valueOf(tiLeQuyDoiHienTai));

        bangKhachHang.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                khachHangDangChon = bangKhachHang.getSelectionModel().getSelectedItem();
                if (khachHangDangChon != null) {
                    txtHoTen.setText(khachHangDangChon.getHoTen());
                    txtSoDienThoai.setText(khachHangDangChon.getSoDienThoai());
                    txtDiemTichLuy.setText(String.valueOf(khachHangDangChon.getDiemTichLuy()));
                }
            }
        });

        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> {
            locDuLieuKhachHang(newValue);
        });
    }

    private void caiDatCotChoBang() {
        TableColumn<KhachHang, Integer> colMa = (TableColumn<KhachHang, Integer>) bangKhachHang.getColumns().get(0);
        colMa.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));

        TableColumn<KhachHang, String> colTen = (TableColumn<KhachHang, String>) bangKhachHang.getColumns().get(1);
        colTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));

        TableColumn<KhachHang, String> colSdt = (TableColumn<KhachHang, String>) bangKhachHang.getColumns().get(2);
        colSdt.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));

        TableColumn<KhachHang, Integer> colDiem = (TableColumn<KhachHang, Integer>) bangKhachHang.getColumns().get(3);
        colDiem.setCellValueFactory(new PropertyValueFactory<>("diemTichLuy"));

        bangKhachHang.setItems(danhSachKhachHang);
    }

    @FXML
    private void taiDuLieuKhachHang() {
        danhSachKhachHang.clear();
        List<KhachHang> data = XuLyKhachHang.layDanhSachKhachHang();
        if (data != null) {
            danhSachKhachHang.addAll(data);
        }
        xuLyXoaTrangForm();
    }

    private void locDuLieuKhachHang(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            bangKhachHang.setItems(danhSachKhachHang);
            return;
        }
        ObservableList<KhachHang> listLoc = FXCollections.observableArrayList();
        for (KhachHang kh : danhSachKhachHang) {
            if (kh.getHoTen().toLowerCase().contains(keyword.toLowerCase()) ||
                    kh.getSoDienThoai().contains(keyword.trim())) {
                listLoc.add(kh);
            }
        }
        bangKhachHang.setItems(listLoc);
    }

    @FXML
    private void xuLyThemKhachHang() {
        String hoTen = txtHoTen.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();

        if (hoTen.isEmpty() || sdt.isEmpty()) {
            hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập đầy đủ Tên và Số điện thoại!");
            return;
        }

        boolean thanhCong = XuLyKhachHang.themKhachHangMoi(hoTen, sdt);
        if (thanhCong) {
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới!");
            taiDuLieuKhachHang();
        } else {
            hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", "Số điện thoại có thể đã tồn tại trên hệ thống!");
        }
    }

    @FXML
    private void xuLyCapNhatKhachHang() {
        if (khachHangDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn khách hàng cần chỉnh sửa trên bảng!");
            return;
        }

        try {
            String hoTen = txtHoTen.getText().trim();
            String sdt = txtSoDienThoai.getText().trim();
            int diem = Integer.parseInt(txtDiemTichLuy.getText().trim());

            boolean thanhCong = XuLyKhachHang.capNhatKhachHang(khachHangDangChon.getMaKhachHang(), hoTen, sdt, diem);
            if (thanhCong) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật thông tin khách hàng thành công!");
                taiDuLieuKhachHang();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", "Không thể cập nhật, kiểm tra lại số điện thoại trùng lặp!");
            }
        } catch (NumberFormatException e) {
            hienThiThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Điểm tích lũy phải là một số nguyên hợp lệ!");
        }
    }

    @FXML
    private void xuLyXoaKhachHang() {
        if (khachHangDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn khách hàng cần xóa trên bảng!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa khách hàng " + khachHangDangChon.getHoTen() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            boolean thanhCong = XuLyKhachHang.xoaKhachHang(khachHangDangChon.getMaKhachHang());
            if (thanhCong) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa khách hàng khỏi hệ thống!");
                taiDuLieuKhachHang();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", "Không thể xóa khách hàng này!");
            }
        }
    }

    @FXML
    private void xuLyXoaTrangForm() {
        txtHoTen.clear();
        txtSoDienThoai.clear();
        txtDiemTichLuy.clear();
        khachHangDangChon = null;
        bangKhachHang.getSelectionModel().clearSelection();
    }

    private void hienThiThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }

    @FXML
    private void quayVeTrangBanHang(ActionEvent event) {
        try {
            String absolutePath = "/org/example/cnpm_nhom17/main-view.fxml";
            java.net.URL url = getClass().getResource(absolutePath);

            if (url == null) {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi đường dẫn", "Không tìm thấy file giao diện: main-view.fxml");
                return;
            }

            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(FXMLLoader.load(url));

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void xuLyCapNhatTiLe(ActionEvent event) {
        try {
            int tiLeMoi = Integer.parseInt(txtTiLeQuyDoi.getText().trim());
            if(tiLeMoi <= 0) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Tỉ lệ quy đổi phải lớn hơn 0!");
                return;
            }
            tiLeQuyDoiHienTai = tiLeMoi;
            prefs.putInt("tiLeQuyDoi", tiLeQuyDoiHienTai);
            hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật tỉ lệ quy đổi hiện tại thành: " + tiLeQuyDoiHienTai);
        } catch (NumberFormatException e) {
            hienThiThongBao(Alert.AlertType.ERROR, "Lỗi định dạng", "Vui lòng nhập số hợp lệ cho tỉ lệ quy đổi!");
        }
    }
}