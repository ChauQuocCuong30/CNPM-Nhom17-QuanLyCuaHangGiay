package org.example.cnpm_nhom17.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.XuLyBanHang;
import org.example.cnpm_nhom17.database_helper.XuLyDanhMuc;
import org.example.cnpm_nhom17.database_helper.XuLyKho;
import org.example.cnpm_nhom17.database_helper.XuLyNhaCungCap; // Thêm thư viện này
import org.example.cnpm_nhom17.models.DanhMuc;
import org.example.cnpm_nhom17.models.NhaCungCap; // Thêm thư viện này
import org.example.cnpm_nhom17.models.ScannedProductInfo;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    // =========================================================
    // KHAI BÁO CÁC THÀNH PHẦN FXML TỪ GIAO DIỆN
    // =========================================================
    @FXML private TableView<ScannedProductInfo> bangSanPham;
    @FXML private TableColumn<ScannedProductInfo, String> colQlyBarcode, colQlyTen, colQlyMau;
    @FXML private TableColumn<ScannedProductInfo, String> colQlyNhaCungCap;
    @FXML private TableColumn<ScannedProductInfo, Integer> colQlyDanhMuc, colQlySize, colQlyTon;
    @FXML private TableColumn<ScannedProductInfo, Double> colQlyGia;
    @FXML private TextField txtSpTen, txtSpSize, txtSpMau, txtSpBarcode, txtSpGiaNhap, txtSpGiaBan, txtSpMin;
    @FXML private ComboBox<String> cbSpDanhMuc;
    @FXML private ComboBox<String> cbSpNhaCungCap;

    private ObservableList<ScannedProductInfo> danhSachKho = FXCollections.observableArrayList();
    private ScannedProductInfo sanPhamDangChon = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        caiDatBangSanPham();
        taiDuLieuKho();
        loadDuLieuDanhMuc();
        loadDuLieuNhaCungCap();

        // Bắt sự kiện click chuột vào 1 dòng trên bảng để đưa dữ liệu qua form
        bangSanPham.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                sanPhamDangChon = bangSanPham.getSelectionModel().getSelectedItem();
                if (sanPhamDangChon != null) {
                    napDuLieuLenForm(sanPhamDangChon);
                }
            }
        });
    }

    // =========================================================
    // CÀI ĐẶT BẢNG VÀ TẢI DỮ LIỆU
    // =========================================================
    private void caiDatBangSanPham() {
        colQlyBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colQlyTen.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));
        colQlyDanhMuc.setCellValueFactory(new PropertyValueFactory<>("maDanhMuc"));

        // Thiết lập cột Nhà Cung Cấp
        colQlyNhaCungCap.setCellValueFactory(new PropertyValueFactory<>("tenNhaCungCap"));

        colQlySize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colQlyMau.setCellValueFactory(new PropertyValueFactory<>("mauSac"));
        colQlyTon.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));

        // Định dạng tiền tệ cho cột Giá bán
        colQlyGia.setCellValueFactory(new PropertyValueFactory<>("giaBanHienTai"));
        colQlyGia.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%,.0f", price).replace(",", "."));
                }
            }
        });

        bangSanPham.setItems(danhSachKho);
    }

    private void taiDuLieuKho() {
        danhSachKho.clear();
        List<ScannedProductInfo> ketQua = XuLyBanHang.layDanhSachTatCaSanPham();
        if (ketQua != null) danhSachKho.addAll(ketQua);
    }

    private void loadDuLieuDanhMuc() {
        cbSpDanhMuc.getItems().clear();
        List<DanhMuc> listDM = XuLyDanhMuc.layDanhSachToanBoDanhMuc();
        for (DanhMuc dm : listDM) {
            cbSpDanhMuc.getItems().add(dm.getMaDanhMuc() + " - " + dm.getTenDanhMuc());
        }
    }

    private void loadDuLieuNhaCungCap() {
        cbSpNhaCungCap.getItems().clear();
        List<NhaCungCap> listNCC = XuLyNhaCungCap.layDanhSachToanBoNhaCungCap();
        for (NhaCungCap ncc : listNCC) {
            cbSpNhaCungCap.getItems().add(ncc.getMaNhaCungCap() + " - " + ncc.getTenNhaCungCap());
        }
    }

    private void napDuLieuLenForm(ScannedProductInfo sp) {
        txtSpTen.setText(sp.getTenSanPham());
        txtSpSize.setText(String.valueOf(sp.getSize()));
        txtSpMau.setText(sp.getMauSac());
        txtSpBarcode.setText(sp.getBarcode());

        // Nạp Giá nhập và Giá bán
        txtSpGiaNhap.setText(String.format("%.0f", sp.getGiaNhap()));
        txtSpGiaBan.setText(String.format("%.0f", sp.getGiaBanHienTai()));
        txtSpMin.setText(String.valueOf(sp.getMucCanhBaoToiThieu()));

        // Tìm và chọn Danh mục
        for (String item : cbSpDanhMuc.getItems()) {
            if (item.startsWith(sp.getMaDanhMuc() + " -")) {
                cbSpDanhMuc.setValue(item);
                break;
            }
        }

        // Tìm và chọn Nhà cung cấp
        for (String item : cbSpNhaCungCap.getItems()) {
            if (item.startsWith(sp.getMaNhaCungCap() + " -")) {
                cbSpNhaCungCap.setValue(item);
                break;
            }
        }
    }

    @FXML
    private void lamMoiFormSanPham() {
        txtSpTen.clear(); txtSpSize.clear(); txtSpMau.clear();
        txtSpBarcode.clear(); txtSpGiaBan.clear(); txtSpMin.clear();
        txtSpGiaNhap.clear(); // Xóa trắng trường giá nhập
        cbSpDanhMuc.getSelectionModel().clearSelection();
        cbSpNhaCungCap.getSelectionModel().clearSelection(); // Xóa trắng NCC
        sanPhamDangChon = null;
    }

    // =========================================================
    // NGHIỆP VỤ THÊM / XÓA DANH MỤC NHANH
    // =========================================================
    @FXML
    private void xuLyThemDanhMucNhanh(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm Danh Mục Mới");
        dialog.setHeaderText("Tạo một danh mục sản phẩm mới");
        dialog.setContentText("Nhập tên danh mục (VD: Giày Chạy Bộ):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tenDM -> {
            if (!tenDM.trim().isEmpty()) {
                boolean thanhCong = XuLyDanhMuc.themDanhMuc(tenDM.trim());
                if (thanhCong) {
                    xuatThongBao("Thành công", "Đã thêm danh mục: " + tenDM);
                    loadDuLieuDanhMuc();
                    for (String item : cbSpDanhMuc.getItems()) {
                        if (item.endsWith("- " + tenDM.trim())) {
                            cbSpDanhMuc.setValue(item);
                            break;
                        }
                    }
                } else {
                    xuatCanhBao("Lỗi", "Tên danh mục có thể đã tồn tại hoặc có lỗi xảy ra!");
                }
            }
        });
    }

    @FXML
    private void xuLyXoaDanhMucNhanh(ActionEvent event) {
        String selectedDM = cbSpDanhMuc.getValue();
        if (selectedDM == null || selectedDM.isEmpty()) {
            xuatCanhBao("Lỗi", "Vui lòng chọn một danh mục trong danh sách để xóa!");
            return;
        }
        try {
            int maDM = Integer.parseInt(selectedDM.split(" - ")[0]);
            String tenDM = selectedDM.split(" - ")[1];

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa danh mục: '" + tenDM + "'?\n\nLưu ý: Không thể xóa danh mục nếu đang có sản phẩm thuộc danh mục này!", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                boolean thanhCong = XuLyDanhMuc.xoaDanhMuc(maDM);
                if (thanhCong) {
                    xuatThongBao("Thành công", "Đã xóa danh mục: " + tenDM);
                    loadDuLieuDanhMuc();
                } else {
                    xuatCanhBao("Không thể xóa", "Danh mục này đang chứa sản phẩm!\nVui lòng xóa hết sản phẩm bên trong hoặc chuyển chúng sang danh mục khác trước khi xóa.");
                }
            }
        } catch (Exception e) {
            xuatCanhBao("Lỗi", "Đã xảy ra lỗi khi đọc thông tin danh mục!");
        }
    }

    // =========================================================
    // NGHIỆP VỤ THÊM / XÓA NHÀ CUNG CẤP NHANH
    // =========================================================
    @FXML
    private void xuLyThemNhaCungCapNhanh(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Thêm Nhà Cung Cấp Mới");
        dialog.setHeaderText("Thêm nhà cung cấp sản phẩm");
        dialog.setContentText("Nhập tên nhà cung cấp:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(tenNCC -> {
            if (!tenNCC.trim().isEmpty()) {
                boolean thanhCong = XuLyNhaCungCap.themNhaCungCap(tenNCC.trim(), "", "");

                if (thanhCong) {
                    xuatThongBao("Thành công", "Đã thêm nhà cung cấp: " + tenNCC);
                    loadDuLieuNhaCungCap();
                } else {
                    xuatCanhBao("Lỗi", "Có lỗi xảy ra hoặc dữ liệu không hợp lệ!");
                }
            }
        });
    }

    @FXML
    private void xuLyXoaNhaCungCapNhanh(ActionEvent event) {
        String selectedNCC = cbSpNhaCungCap.getValue();
        if (selectedNCC == null || selectedNCC.isEmpty()) {
            xuatCanhBao("Lỗi", "Vui lòng chọn một nhà cung cấp để xóa!");
            return;
        }
        try {
            int maNCC = Integer.parseInt(selectedNCC.split(" - ")[0]);
            String tenNCC = selectedNCC.split(" - ")[1];

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa nhà cung cấp: '" + tenNCC + "'?", ButtonType.YES, ButtonType.NO);
            confirm.showAndWait();

            if (confirm.getResult() == ButtonType.YES) {
                boolean thanhCong = XuLyNhaCungCap.xoaNhaCungCap(maNCC);

                if (thanhCong) {
                    xuatThongBao("Thành công", "Đã xóa nhà cung cấp: " + tenNCC);
                    loadDuLieuNhaCungCap();
                } else {
                    xuatCanhBao("Không thể xóa", "Nhà cung cấp này đang ràng buộc với sản phẩm khác.");
                }
            }
        } catch (Exception e) {
            xuatCanhBao("Lỗi", "Đã xảy ra lỗi khi đọc thông tin nhà cung cấp!");
        }
    }

    // =========================================================
    // NGHIỆP VỤ CRUD SẢN PHẨM
    // =========================================================
    @FXML
    private void xuLyThemSanPham(ActionEvent event) {
        try {
            if (cbSpDanhMuc.getValue() == null || cbSpNhaCungCap.getValue() == null) {
                xuatCanhBao("Thiếu thông tin", "Vui lòng chọn Danh mục và Nhà cung cấp!");
                return;
            }
            int maDM = Integer.parseInt(cbSpDanhMuc.getValue().split(" - ")[0]);
            int maNCC = Integer.parseInt(cbSpNhaCungCap.getValue().split(" - ")[0]);

            double giaNhap = Double.parseDouble(txtSpGiaNhap.getText());
            double giaBan = Double.parseDouble(txtSpGiaBan.getText());
            int size = Integer.parseInt(txtSpSize.getText());
            int min = Integer.parseInt(txtSpMin.getText());

            // Đã truyền đủ 6 tham số
            int maSpMoi = XuLyDanhMuc.themSanPhamMoi(txtSpTen.getText(), maDM, maNCC, giaNhap, giaBan, "");

            if (maSpMoi > 0) {
                XuLyDanhMuc.themChiTietSanPham(maSpMoi, txtSpBarcode.getText(), size, txtSpMau.getText(), min);
                xuatThongBao("Thành công", "Đã thêm sản phẩm mới!");
                taiDuLieuKho();
                lamMoiFormSanPham();
            }
        } catch (Exception e) {
            xuatCanhBao("Lỗi nhập liệu", "Vui lòng điền đủ thông tin và đúng định dạng số!");
        }
    }

    @FXML
    private void xuLyCapNhatSanPham(ActionEvent event) {
        if (sanPhamDangChon == null) {
            xuatCanhBao("Lỗi", "Vui lòng chọn một sản phẩm trên bảng để cập nhật!");
            return;
        }
        try {
            int maDM = Integer.parseInt(cbSpDanhMuc.getValue().split(" - ")[0]);
            int maNCC = Integer.parseInt(cbSpNhaCungCap.getValue().split(" - ")[0]);

            double giaNhap = Double.parseDouble(txtSpGiaNhap.getText());
            double giaBan = Double.parseDouble(txtSpGiaBan.getText());
            int size = Integer.parseInt(txtSpSize.getText());
            int min = Integer.parseInt(txtSpMin.getText());

            // Đã truyền đủ 7 tham số
            XuLyDanhMuc.capNhatThongTinSanPham(sanPhamDangChon.getMaSanPham(), txtSpTen.getText(), maDM, maNCC, giaNhap, giaBan, sanPhamDangChon.getMoTa());

            XuLyKho.capNhatChiTietSanPham(sanPhamDangChon.getBarcode(), txtSpBarcode.getText(), size, txtSpMau.getText(), min);

            xuatThongBao("Thành công", "Đã cập nhật thông tin sản phẩm!");
            taiDuLieuKho();
        } catch (Exception e) {
            xuatCanhBao("Lỗi nhập liệu", "Vui lòng kiểm tra lại các trường số liệu!");
        }
    }

    @FXML
    private void xuLyXoaSanPham(ActionEvent event) {
        if (sanPhamDangChon == null) {
            xuatCanhBao("Lỗi", "Vui lòng chọn một sản phẩm trên bảng để xóa!");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa Barcode: " + sanPhamDangChon.getBarcode() + "?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            if (XuLyKho.xoaChiTietSanPham(sanPhamDangChon.getBarcode())) {
                xuatThongBao("Thành công", "Đã xóa sản phẩm khỏi hệ thống!");
                taiDuLieuKho();
                lamMoiFormSanPham();
            } else {
                xuatCanhBao("Lỗi", "Không thể xóa sản phẩm này vì có thể nó đã phát sinh giao dịch!");
            }
        }
    }

    private void xuatCanhBao(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    private void xuatThongBao(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(content);
        alert.showAndWait();
    }

    // =========================================================
    // MENU ĐIỀU HƯỚNG
    // =========================================================
    private void chuyenTrang(ActionEvent event, String fxmlFileName) {
        try {
            Scene currentScene = ((Node) event.getSource()).getScene();
            javafx.scene.Parent root = FXMLLoader.load(getClass().getResource("/org/example/cnpm_nhom17/" + fxmlFileName));
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML private void moTrangKhachHang(ActionEvent event) { chuyenTrang(event, "quan-ly-khach-hang-view.fxml"); }
    @FXML private void moTrangMaGiamGia(ActionEvent event) { chuyenTrang(event, "quan-ly-ma-giam-gia-view.fxml"); }
    @FXML private void moTrangNhanVien(ActionEvent event) { chuyenTrang(event, "quan-ly-nhan-vien-view.fxml"); }
    @FXML private void moTrangHoaDon(ActionEvent event) { chuyenTrang(event, "quan-ly-hoa-don-view.fxml"); }
    @FXML private void moTrangQuanLyKho(ActionEvent event) { chuyenTrang(event, "quan-ly-kho-view.fxml"); }
    @FXML private void moTrangThanhToan(ActionEvent event) { chuyenTrang(event, "thanh-toan-view.fxml"); }
}