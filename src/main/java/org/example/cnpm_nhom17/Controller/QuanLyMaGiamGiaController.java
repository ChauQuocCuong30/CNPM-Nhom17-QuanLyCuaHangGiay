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
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.XuLyMaGiamGia;
import org.example.cnpm_nhom17.models.MaGiamGia;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class QuanLyMaGiamGiaController {

    @FXML private TableView<MaGiamGia> bangVoucher;
    @FXML private TextField txtTimKiem, txtCodeVoucher, txtGiaTriGiam, txtDiemCanDoi;
    @FXML private ComboBox<String> cbLoaiGiamGia, cbTrangThai;
    @FXML private DatePicker dpNgayHetHan;
    @FXML private TextField txtSoLuong; // Ô nhập liệu Số lượng mới

    private ObservableList<MaGiamGia> danhSachVoucher = FXCollections.observableArrayList();
    private MaGiamGia voucherDangChon = null;

    @FXML
    public void initialize() {
        caiDatCotChoBang();
        taiDuLieuVoucher();

        // Mặc định chọn Trạng thái Hoạt động khi mở form
        cbTrangThai.setValue("HoatDong");
        // Mặc định ngày hết hạn là ngày hôm sau
        dpNgayHetHan.setValue(LocalDate.now().plusDays(1));

        // Nhấp vào bảng để sửa
        bangVoucher.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                voucherDangChon = bangVoucher.getSelectionModel().getSelectedItem();
                if (voucherDangChon != null) {
                    napDuLieuLenForm(voucherDangChon);
                }
            }
        });

        // Tìm kiếm live theo mã code
        txtTimKiem.textProperty().addListener((observable, oldValue, newValue) -> locDuLieuVoucher(newValue));
    }

    private void caiDatCotChoBang() {
        TableColumn<MaGiamGia, Integer> colMa = (TableColumn<MaGiamGia, Integer>) bangVoucher.getColumns().get(0);
        colMa.setCellValueFactory(new PropertyValueFactory<>("maGiamGia"));

        TableColumn<MaGiamGia, String> colCode = (TableColumn<MaGiamGia, String>) bangVoucher.getColumns().get(1);
        colCode.setCellValueFactory(new PropertyValueFactory<>("codeVoucher"));

        TableColumn<MaGiamGia, String> colLoai = (TableColumn<MaGiamGia, String>) bangVoucher.getColumns().get(2);
        colLoai.setCellValueFactory(new PropertyValueFactory<>("loaiGiamGia"));

        TableColumn<MaGiamGia, Double> colGiaTri = (TableColumn<MaGiamGia, Double>) bangVoucher.getColumns().get(3);
        colGiaTri.setCellValueFactory(new PropertyValueFactory<>("giaTriGiam"));

        TableColumn<MaGiamGia, Integer> colSoLuong = (TableColumn<MaGiamGia, Integer>) bangVoucher.getColumns().get(4);
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        TableColumn<MaGiamGia, Integer> colMucGiaCan = (TableColumn<MaGiamGia, Integer>) bangVoucher.getColumns().get(5);
        colMucGiaCan.setCellValueFactory(new PropertyValueFactory<>("diemCanDoi"));

        TableColumn<MaGiamGia, String> colNgay = (TableColumn<MaGiamGia, String>) bangVoucher.getColumns().get(6);
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayHetHan"));

        TableColumn<MaGiamGia, String> colTrangThai = (TableColumn<MaGiamGia, String>) bangVoucher.getColumns().get(7);
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        bangVoucher.setItems(danhSachVoucher);
    }

    @FXML
    private void taiDuLieuVoucher() {
        danhSachVoucher.clear();
        List<MaGiamGia> data = XuLyMaGiamGia.layToanBoDanhSachMaGiamGia();
        if (data != null) danhSachVoucher.addAll(data);
        xuLyXoaTrangForm();
    }

    private void napDuLieuLenForm(MaGiamGia mg) {
        txtCodeVoucher.setText(mg.getCodeVoucher());
        cbLoaiGiamGia.setValue(mg.getLoaiGiamGia());
        txtGiaTriGiam.setText(String.valueOf(mg.getGiaTriGiam()));
        // Nạp số lượng lên UI
        txtSoLuong.setText(String.valueOf(mg.getSoLuong()));
        txtDiemCanDoi.setText(String.valueOf(mg.getDiemCanDoi())); // Đóng vai trò là Mức giá cần
        cbTrangThai.setValue(mg.getTrangThai());

        if (mg.getNgayHetHan() != null && !mg.getNgayHetHan().isEmpty()) {
            dpNgayHetHan.setValue(LocalDate.parse(mg.getNgayHetHan()));
        }
    }

    private void locDuLieuVoucher(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            bangVoucher.setItems(danhSachVoucher);
            return;
        }
        ObservableList<MaGiamGia> listLoc = FXCollections.observableArrayList();
        for (MaGiamGia mg : danhSachVoucher) {
            if (mg.getCodeVoucher().toLowerCase().contains(keyword.toLowerCase())) {
                listLoc.add(mg);
            }
        }
        bangVoucher.setItems(listLoc);
    }

    @FXML
    private void xuLyThemVoucher() {
        try {
            String code = txtCodeVoucher.getText().trim();
            String loai = cbLoaiGiamGia.getValue();
            double giaTri = Double.parseDouble(txtGiaTriGiam.getText().trim());
            // Lấy thêm số lượng
            int soLuong = txtSoLuong.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtSoLuong.getText().trim());
            int mucGiaCan = txtDiemCanDoi.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtDiemCanDoi.getText().trim());
            String trangThai = cbTrangThai.getValue();
            String ngayHetHan = dpNgayHetHan.getValue() != null ? dpNgayHetHan.getValue().toString() : "";

            if (code.isEmpty() || loai == null) {
                hienThiThongBao(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng nhập Code và chọn Loại giảm giá!");
                return;
            }

            if (XuLyMaGiamGia.themMaGiamGia(code, loai, giaTri, soLuong, mucGiaCan, ngayHetHan, trangThai)) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm voucher mới!");
                taiDuLieuVoucher();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Thất bại", "Mã voucher này có thể đã tồn tại!");
            }
        } catch (NumberFormatException e) {
            hienThiThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Giá trị giảm, số lượng và mức giá cần phải là số!");
        }
    }

    @FXML
    private void xuLyCapNhatVoucher() {
        if (voucherDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn voucher trên bảng!");
            return;
        }
        try {
            String code = txtCodeVoucher.getText().trim();
            String loai = cbLoaiGiamGia.getValue();
            double giaTri = Double.parseDouble(txtGiaTriGiam.getText().trim());
            int soLuong = txtSoLuong.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtSoLuong.getText().trim());
            int mucGiaCan = txtDiemCanDoi.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtDiemCanDoi.getText().trim());
            String trangThai = cbTrangThai.getValue();
            String ngayHetHan = dpNgayHetHan.getValue() != null ? dpNgayHetHan.getValue().toString() : "";

            if (XuLyMaGiamGia.capNhatMaGiamGia(voucherDangChon.getMaGiamGia(), code, loai, giaTri, soLuong, mucGiaCan, ngayHetHan, trangThai)) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật voucher thành công!");
                taiDuLieuVoucher();
            }
        } catch (NumberFormatException e) {
            hienThiThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Giá trị giảm, số lượng và mức giá cần phải là số!");
        }
    }

    @FXML
    private void xuLyXoaVoucher() {
        if (voucherDangChon == null) {
            hienThiThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn voucher cần xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Xóa voucher " + voucherDangChon.getCodeVoucher() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            if (XuLyMaGiamGia.xoaMaGiamGia(voucherDangChon.getMaGiamGia())) {
                hienThiThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa voucher!");
                taiDuLieuVoucher();
            } else {
                hienThiThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa voucher này!");
            }
        }
    }

    @FXML
    private void xuLyXoaTrangForm() {
        txtCodeVoucher.clear();
        cbLoaiGiamGia.getSelectionModel().clearSelection();
        txtGiaTriGiam.clear();
        txtSoLuong.clear();
        txtDiemCanDoi.clear();
        cbTrangThai.setValue("HoatDong");
        dpNgayHetHan.setValue(LocalDate.now().plusDays(1));

        voucherDangChon = null;
        bangVoucher.getSelectionModel().clearSelection();
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