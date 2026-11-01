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
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.XuLyBanHang;
import org.example.cnpm_nhom17.database_helper.XuLyKho;
import org.example.cnpm_nhom17.database_helper.XuLyNhaCungCap;
import org.example.cnpm_nhom17.models.NhaCungCap;
import org.example.cnpm_nhom17.models.NhanVien;
import org.example.cnpm_nhom17.models.ScannedProductInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuanLyKhoController {

    // =========================================================
    // KHAI BÁO BIẾN - TAB 1: TỒN KHO & CẢNH BÁO
    // =========================================================
    @FXML private TableView<ScannedProductInfo> bangKho;
    @FXML private TextField txtTimKiemKho;
    private ObservableList<ScannedProductInfo> danhSachKho = FXCollections.observableArrayList();

    // =========================================================
    // KHAI BÁO BIẾN - TAB 2: NHẬP HÀNG
    // =========================================================
    @FXML private TextField txtNhapBarcode, txtNhapSoLuong, txtNhapGiaVon;
    @FXML private Label lblNhapTenGiay;

    @FXML private ComboBox<NhanVien> cbNhanVienNhap;
    @FXML private ComboBox<NhaCungCap> cbNhaCungCap;

    private ScannedProductInfo sanPhamDangNhap = null;

    @FXML
    public void initialize() {
        caiDatBangKho();
        taiDuLieuKho();

        loadDuLieuNhanVien();
        loadDuLieuNhaCungCap();

        txtTimKiemKho.textProperty().addListener((observable, oldValue, newValue) -> locDuLieuBangKho(newValue));

        txtNhapBarcode.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ENTER: timSanPhamDeNhap(); break;
                default: break;
            }
        });

        txtNhapBarcode.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue && !txtNhapBarcode.getText().trim().isEmpty()) {
                timSanPhamDeNhap();
            }
        });
    }

    // =========================================================
    // LOAD DỮ LIỆU COMBBOX BẰNG CELL FACTORY (
    // =========================================================
    private void loadDuLieuNhanVien() {
        // Gọi hàm lọc Nhân viên Kho & Quản lý
        List<NhanVien> listNV = XuLyKho.nhanvienKhoVaQuanLy();
        if (listNV != null) {
            cbNhanVienNhap.setItems(FXCollections.observableArrayList(listNV));

            // Tùy chỉnh hiển thị danh sách xổ xuống
            cbNhanVienNhap.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(NhanVien item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getHoTen());
                }
            });
            // Tùy chỉnh hiển thị ô đã chọn
            cbNhanVienNhap.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(NhanVien item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getHoTen());
                }
            });
        }
    }

    private void loadDuLieuNhaCungCap() {
        List<NhaCungCap> listNCC = XuLyNhaCungCap.layDanhSachToanBoNhaCungCap();
        if (listNCC != null) {
            cbNhaCungCap.setItems(FXCollections.observableArrayList(listNCC));

            cbNhaCungCap.setCellFactory(lv -> new ListCell<>() {
                @Override
                protected void updateItem(NhaCungCap item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTenNhaCungCap());
                }
            });
            cbNhaCungCap.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(NhaCungCap item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item.getTenNhaCungCap());
                }
            });
        }
    }

    // =========================================================
    // NGHIỆP VỤ TỒN KHO (TAB 1)
    // =========================================================
    private void caiDatBangKho() {
        TableColumn<ScannedProductInfo, String> colBarcode = (TableColumn<ScannedProductInfo, String>) bangKho.getColumns().get(0);
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));

        TableColumn<ScannedProductInfo, String> colTen = (TableColumn<ScannedProductInfo, String>) bangKho.getColumns().get(1);
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));

        TableColumn<ScannedProductInfo, Integer> colSize = (TableColumn<ScannedProductInfo, Integer>) bangKho.getColumns().get(2);
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<ScannedProductInfo, String> colMau = (TableColumn<ScannedProductInfo, String>) bangKho.getColumns().get(3);
        colMau.setCellValueFactory(new PropertyValueFactory<>("mauSac"));

        TableColumn<ScannedProductInfo, Double> colGiaVon = (TableColumn<ScannedProductInfo, Double>) bangKho.getColumns().get(4);
        colGiaVon.setCellValueFactory(new PropertyValueFactory<>("giaVonHienTai"));
        dinhDangCotTien(colGiaVon);

        TableColumn<ScannedProductInfo, Double> colGiaBan = (TableColumn<ScannedProductInfo, Double>) bangKho.getColumns().get(5);
        colGiaBan.setCellValueFactory(new PropertyValueFactory<>("giaBanHienTai"));
        dinhDangCotTien(colGiaBan);

        TableColumn<ScannedProductInfo, Integer> colTonKho = (TableColumn<ScannedProductInfo, Integer>) bangKho.getColumns().get(6);
        colTonKho.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));

        TableColumn<ScannedProductInfo, Integer> colCanhBao = (TableColumn<ScannedProductInfo, Integer>) bangKho.getColumns().get(7);
        colCanhBao.setCellValueFactory(new PropertyValueFactory<>("mucCanhBaoToiThieu"));

        bangKho.setRowFactory(tv -> new TableRow<ScannedProductInfo>() {
            @Override
            protected void updateItem(ScannedProductInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else {
                    int tonKho = item.getSoLuongTon();
                    int canhBao = item.getMucCanhBaoToiThieu();

                    if (tonKho <= 0) {
                        setStyle("-fx-background-color: #fadbd8; -fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    } else if (tonKho <= canhBao) {
                        setStyle("-fx-background-color: #fcf3cf; -fx-text-fill: #d35400; -fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        bangKho.setItems(danhSachKho);
    }

    @FXML
    private void taiDuLieuKho() {
        danhSachKho.clear();
        List<ScannedProductInfo> ketQua = XuLyBanHang.layDanhSachTatCaSanPham();
        if (ketQua != null) danhSachKho.addAll(ketQua);
        txtTimKiemKho.clear();
    }

    private void locDuLieuBangKho(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            bangKho.setItems(danhSachKho);
            return;
        }
        ObservableList<ScannedProductInfo> listLoc = FXCollections.observableArrayList();
        for (ScannedProductInfo sp : danhSachKho) {
            if (sp.getTenSanPham().toLowerCase().contains(keyword.toLowerCase()) ||
                    sp.getBarcode().toLowerCase().contains(keyword.toLowerCase())) {
                listLoc.add(sp);
            }
        }
        bangKho.setItems(listLoc);
    }

    // =========================================================
    // NGHIỆP VỤ NHẬP HÀNG (TAB 2)
    // =========================================================
    @FXML
    private void timSanPhamDeNhap() {
        String barcode = txtNhapBarcode.getText().trim();
        if (barcode.isEmpty()) return;

        sanPhamDangNhap = XuLyBanHang.traCuuSanPhamTheoBarcode(barcode);

        if (sanPhamDangNhap != null) {
            lblNhapTenGiay.setText("Đang nhập: " + sanPhamDangNhap.getTenSanPham() + " (Size " + sanPhamDangNhap.getSize() + ")");
            lblNhapTenGiay.setTextFill(javafx.scene.paint.Color.web("#27ae60"));

            txtNhapGiaVon.setText(String.format("%.0f", sanPhamDangNhap.getGiaVonHienTai()));
        } else {
            lblNhapTenGiay.setText("Mã vạch không tồn tại! Hãy kiểm tra lại.");
            lblNhapTenGiay.setTextFill(javafx.scene.paint.Color.web("#c0392b"));
            sanPhamDangNhap = null;
            txtNhapGiaVon.clear();
        }
    }

    @FXML
    private void xuLyNhapKho() {
        if (cbNhanVienNhap.getValue() == null || cbNhaCungCap.getValue() == null) {
            xuatCanhBao("Thiếu thông tin", "Vui lòng chọn Nhân viên và Nhà cung cấp lập phiếu!");
            return;
        }

        if (sanPhamDangNhap == null) {
            xuatCanhBao("Lỗi", "Vui lòng quét mã vạch sản phẩm hợp lệ trước!");
            return;
        }

        try {
            int maNhanVien = cbNhanVienNhap.getValue().getMaNhanVien();
            int maNhaCungCap = cbNhaCungCap.getValue().getMaNhaCungCap();

            int soLuong = Integer.parseInt(txtNhapSoLuong.getText().trim());
            double giaVonNhap = Double.parseDouble(txtNhapGiaVon.getText().trim());

            if (soLuong <= 0 || giaVonNhap < 0) {
                xuatCanhBao("Lỗi", "Số lượng và Giá vốn phải lớn hơn 0!");
                return;
            }

            List<XuLyKho.ItemNhap> listNhap = new ArrayList<>();
            listNhap.add(new XuLyKho.ItemNhap(sanPhamDangNhap.getBarcode(), soLuong, giaVonNhap));

            if (XuLyKho.taoPhieuNhap(maNhaCungCap, maNhanVien, listNhap)) {
                xuatThongBao("Nhập kho thành công!", "Đã cộng thêm " + soLuong + " đôi vào kho.");

                txtNhapBarcode.clear(); txtNhapSoLuong.clear(); txtNhapGiaVon.clear();
                lblNhapTenGiay.setText("Chưa xác định sản phẩm");
                lblNhapTenGiay.setTextFill(javafx.scene.paint.Color.web("#2980b9"));
                cbNhanVienNhap.getSelectionModel().clearSelection();
                cbNhaCungCap.getSelectionModel().clearSelection();
                sanPhamDangNhap = null;

                taiDuLieuKho();
            } else {
                xuatCanhBao("Lỗi DB", "Lỗi trong quá trình tạo phiếu nhập!");
            }
        } catch (NumberFormatException e) {
            xuatCanhBao("Lỗi định dạng", "Số lượng và giá vốn phải là số hợp lệ!");
        }
    }

    // =========================================================
    // TIỆN ÍCH
    // =========================================================
    private void dinhDangCotTien(TableColumn<ScannedProductInfo, Double> cot) {
        cot.setCellFactory(tc -> new TableCell<>() {
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

    @FXML
    private void quayVeTrangBanHang(ActionEvent event) {
        try {
            Scene currentScene = ((Node) event.getSource()).getScene();
            currentScene.setRoot(FXMLLoader.load(getClass().getResource("/org/example/cnpm_nhom17/main-view.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}