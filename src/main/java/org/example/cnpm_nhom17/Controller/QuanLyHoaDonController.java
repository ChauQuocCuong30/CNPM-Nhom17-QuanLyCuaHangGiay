package org.example.cnpm_nhom17.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.cnpm_nhom17.database_helper.XuLyThongKe;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class QuanLyHoaDonController {

    // === TAB 1: DANH SÁCH HÓA ĐƠN ===
    @FXML private TableView<XuLyThongKe.HoaDonDTO> bangHoaDon;
    @FXML private TableView<XuLyThongKe.ChiTietHoaDonDTO> bangChiTietHD;
    private ObservableList<XuLyThongKe.HoaDonDTO> danhSachHoaDon = FXCollections.observableArrayList();
    private ObservableList<XuLyThongKe.ChiTietHoaDonDTO> danhSachChiTiet = FXCollections.observableArrayList();

    // === TAB 2: THỐNG KÊ KINH DOANH ===
    @FXML private DatePicker dpTuNgay, dpDenNgay;
    @FXML private Label lblDoanhThu, lblGiaVon, lblLoiNhuan;

    @FXML private ComboBox<String> cbTieuChiLoc;
    @FXML private TableView<BaoCaoDoanhThuDTO> bangBaoCao;
    @FXML private TableColumn<BaoCaoDoanhThuDTO, String> colThoiGian;
    @FXML private TableColumn<BaoCaoDoanhThuDTO, Double> colDoanhThuCT, colGiaVonCT, colLoiNhuanCT;

    private ObservableList<BaoCaoDoanhThuDTO> danhSachBaoCao = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        caiDatBang();
        taiDuLieuHoaDon();

        // Cài đặt bộ lọc
        cbTieuChiLoc.setItems(FXCollections.observableArrayList("Theo Ngày", "Theo Tháng", "Theo Năm"));
        cbTieuChiLoc.getSelectionModel().select("Theo Ngày");
        cbTieuChiLoc.setOnAction(e -> xuLyThongKe());

        // Mặc định xem trong tháng hiện tại
        dpTuNgay.setValue(LocalDate.now().withDayOfMonth(1));
        dpDenNgay.setValue(LocalDate.now());
        xuLyThongKe();

        // Sự kiện click vào 1 hóa đơn bên trái -> Hiện chi tiết bên phải
        bangHoaDon.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 1) {
                XuLyThongKe.HoaDonDTO hd = bangHoaDon.getSelectionModel().getSelectedItem();
                if (hd != null) {
                    taiChiTietHoaDon(hd.getMaHoaDon());
                }
            }
        });
    }

    private void caiDatBang() {
        // --- CÀI ĐẶT BẢNG HÓA ĐƠN ---
        TableColumn<XuLyThongKe.HoaDonDTO, Integer> colMa = (TableColumn<XuLyThongKe.HoaDonDTO, Integer>) bangHoaDon.getColumns().get(0);
        colMa.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));

        TableColumn<XuLyThongKe.HoaDonDTO, String> colNgay = (TableColumn<XuLyThongKe.HoaDonDTO, String>) bangHoaDon.getColumns().get(1);
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayTao"));

        TableColumn<XuLyThongKe.HoaDonDTO, String> colKhach = (TableColumn<XuLyThongKe.HoaDonDTO, String>) bangHoaDon.getColumns().get(2);
        colKhach.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));

        TableColumn<XuLyThongKe.HoaDonDTO, Double> colTongTien = (TableColumn<XuLyThongKe.HoaDonDTO, Double>) bangHoaDon.getColumns().get(3);
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        dinhDangCotTien(colTongTien);

        bangHoaDon.setItems(danhSachHoaDon);

        // --- CÀI ĐẶT BẢNG CHI TIẾT HÓA ĐƠN ---
        TableColumn<XuLyThongKe.ChiTietHoaDonDTO, String> colTenGiay = (TableColumn<XuLyThongKe.ChiTietHoaDonDTO, String>) bangChiTietHD.getColumns().get(0);
        colTenGiay.setCellValueFactory(new PropertyValueFactory<>("tenGiay"));

        TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Integer> colSize = (TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Integer>) bangChiTietHD.getColumns().get(1);
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));

        TableColumn<XuLyThongKe.ChiTietHoaDonDTO, String> colMau = (TableColumn<XuLyThongKe.ChiTietHoaDonDTO, String>) bangChiTietHD.getColumns().get(2);
        colMau.setCellValueFactory(new PropertyValueFactory<>("mauSac"));

        TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Integer> colSL = (TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Integer>) bangChiTietHD.getColumns().get(3);
        colSL.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Double> colGiaBan = (TableColumn<XuLyThongKe.ChiTietHoaDonDTO, Double>) bangChiTietHD.getColumns().get(4);
        colGiaBan.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        dinhDangCotTien(colGiaBan);

        bangChiTietHD.setItems(danhSachChiTiet);

        // --- CÀI ĐẶT BẢNG BÁO CÁO MỚI ---
        colThoiGian.setCellValueFactory(cellData -> cellData.getValue().thoiGianProperty());

        colDoanhThuCT.setCellValueFactory(cellData -> cellData.getValue().doanhThuProperty().asObject());
        dinhDangCotTien(colDoanhThuCT);

        colGiaVonCT.setCellValueFactory(cellData -> cellData.getValue().giaVonProperty().asObject());
        dinhDangCotTien(colGiaVonCT);

        colLoiNhuanCT.setCellValueFactory(cellData -> cellData.getValue().loiNhuanProperty().asObject());
        dinhDangCotTien(colLoiNhuanCT);

        bangBaoCao.setItems(danhSachBaoCao);
    }


    private void taiDuLieuHoaDon() {
        danhSachHoaDon.clear();
        List<XuLyThongKe.HoaDonDTO> list = XuLyThongKe.layDanhSachHoaDon();
        if (list != null) danhSachHoaDon.addAll(list);
    }

    private void taiChiTietHoaDon(int maHD) {
        danhSachChiTiet.clear();
        List<XuLyThongKe.ChiTietHoaDonDTO> list = XuLyThongKe.layChiTietHoaDon(maHD);
        if (list != null) danhSachChiTiet.addAll(list);
    }

    private <T> void dinhDangCotTien(TableColumn<T, Double> cot) {
        cot.setCellFactory(tc -> new TableCell<T, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) setText(null);
                else setText(String.format("%,.0f đ", price).replace(",", "."));
            }
        });
    }

    @FXML
    private void quayVeTrangBanHang(ActionEvent event) {
        try {
            String absolutePath = "/org/example/cnpm_nhom17/main-view.fxml";
            java.net.URL url = getClass().getResource(absolutePath);

            if (url == null) {
                new Alert(Alert.AlertType.ERROR, "Không tìm thấy file giao diện: main-view.fxml").showAndWait();
                return;
            }
            Scene currentScene = ((Node) event.getSource()).getScene();

            javafx.scene.Parent root = FXMLLoader.load(url);

            currentScene.setRoot(root);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void xuLyThongKe() {
        LocalDate tuNgayDate = dpTuNgay.getValue();
        LocalDate denNgayDate = dpDenNgay.getValue();

        if (tuNgayDate == null || denNgayDate == null || tuNgayDate.isAfter(denNgayDate)) {
            return;
        }

        // 1. Tính TỔNG CHUNG cho 3 ô màu phía trên
        String tuNgayStr = tuNgayDate.toString() + " 00:00:00";
        String denNgayStr = denNgayDate.toString() + " 23:59:59";

        double tongDoanhThu = XuLyThongKe.tinhTongDoanhThu(tuNgayStr, denNgayStr);
        double tongGiaVon = XuLyThongKe.tinhTongGiaVon(tuNgayStr, denNgayStr);
        double tongLoiNhuan = tongDoanhThu - tongGiaVon;

        lblDoanhThu.setText(String.format("%,.0f đ", tongDoanhThu).replace(",", "."));
        lblGiaVon.setText(String.format("%,.0f đ", tongGiaVon).replace(",", "."));
        lblLoiNhuan.setText(String.format("%,.0f đ", tongLoiNhuan).replace(",", "."));

        // 2. LẬP BẢNG CHI TIẾT
        danhSachBaoCao.clear();
        String tieuChi = cbTieuChiLoc.getValue();

        if (tieuChi.equals("Theo Ngày")) {
            LocalDate current = tuNgayDate;
            while (!current.isAfter(denNgayDate)) {
                String start = current.toString() + " 00:00:00";
                String end = current.toString() + " 23:59:59";

                double dt = XuLyThongKe.tinhTongDoanhThu(start, end);
                double gv = XuLyThongKe.tinhTongGiaVon(start, end);

                if (dt > 0 || gv > 0) {
                    String nhanThoiGian = current.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    danhSachBaoCao.add(new BaoCaoDoanhThuDTO(nhanThoiGian, dt, gv));
                }
                current = current.plusDays(1);
            }
        }
        else if (tieuChi.equals("Theo Tháng")) {
            YearMonth currentMonth = YearMonth.from(tuNgayDate);
            YearMonth endMonth = YearMonth.from(denNgayDate);

            while (!currentMonth.isAfter(endMonth)) {
                LocalDate firstDay = currentMonth.atDay(1);
                LocalDate lastDay = currentMonth.atEndOfMonth();

                String start = firstDay.toString() + " 00:00:00";
                String end = lastDay.toString() + " 23:59:59";

                double dt = XuLyThongKe.tinhTongDoanhThu(start, end);
                double gv = XuLyThongKe.tinhTongGiaVon(start, end);

                if (dt > 0 || gv > 0) {
                    String nhanThoiGian = currentMonth.format(DateTimeFormatter.ofPattern("MM/yyyy"));
                    danhSachBaoCao.add(new BaoCaoDoanhThuDTO(nhanThoiGian, dt, gv));
                }
                currentMonth = currentMonth.plusMonths(1);
            }
        }
        else if (tieuChi.equals("Theo Năm")) {
            int startYear = tuNgayDate.getYear();
            int endYear = denNgayDate.getYear();

            for (int year = startYear; year <= endYear; year++) {
                String start = year + "-01-01 00:00:00";
                String end = year + "-12-31 23:59:59";

                double dt = XuLyThongKe.tinhTongDoanhThu(start, end);
                double gv = XuLyThongKe.tinhTongGiaVon(start, end);

                if (dt > 0 || gv > 0) {
                    danhSachBaoCao.add(new BaoCaoDoanhThuDTO(String.valueOf(year), dt, gv));
                }
            }
        }

        Collections.reverse(danhSachBaoCao);
    }
    @FXML
    private void xuatFileExcel(ActionEvent event) {
        if (danhSachBaoCao == null || danhSachBaoCao.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Không có dữ liệu nào để xuất ra Excel!").showAndWait();
            return;
        }

        // 1. Mở cửa sổ chọn nơi lưu file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo doanh thu");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("BaoCaoDoanhThu_NikePOS.xlsx");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                // 2. Tạo một Workbook (File Excel) và một Sheet mới
                org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
                org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Báo Cáo Doanh Thu");

                // 3. Tạo dòng Tiêu đề (Header)
                org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Thời Gian");
                headerRow.createCell(1).setCellValue("Tổng Doanh Thu (VNĐ)");
                headerRow.createCell(2).setCellValue("Tổng Giá Vốn (VNĐ)");
                headerRow.createCell(3).setCellValue("Lợi Nhuận Gộp (VNĐ)");

                // 4. Lặp qua danh sách báo cáo trên bảng và ghi từng dòng vào Excel
                int rowNum = 1;
                for (BaoCaoDoanhThuDTO dto : danhSachBaoCao) {
                    org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(dto.thoiGianProperty().get());
                    row.createCell(1).setCellValue(dto.doanhThuProperty().get());
                    row.createCell(2).setCellValue(dto.giaVonProperty().get());
                    row.createCell(3).setCellValue(dto.loiNhuanProperty().get());
                }

                for (int i = 0; i < 4; i++) {
                    sheet.autoSizeColumn(i);
                }

                // 5. Ghi file ra ổ cứng
                try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(file)) {
                    workbook.write(fileOut);
                }
                workbook.close();

                // 6. Thông báo thành công
                new Alert(Alert.AlertType.INFORMATION, "Xuất file Excel thành công tại:\n" + file.getAbsolutePath()).showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Có lỗi xảy ra khi xuất file Excel:\n" + e.getMessage()).showAndWait();
            }
        }
    }

    // =========================================================
    // LỚP BAO BỌC DỮ LIỆU BÁO CÁO (DTO)
    // =========================================================
    public static class BaoCaoDoanhThuDTO {
        private final SimpleStringProperty thoiGian;
        private final SimpleDoubleProperty doanhThu;
        private final SimpleDoubleProperty giaVon;
        private final SimpleDoubleProperty loiNhuan;

        public BaoCaoDoanhThuDTO(String thoiGian, double doanhThu, double giaVon) {
            this.thoiGian = new SimpleStringProperty(thoiGian);
            this.doanhThu = new SimpleDoubleProperty(doanhThu);
            this.giaVon = new SimpleDoubleProperty(giaVon);
            this.loiNhuan = new SimpleDoubleProperty(doanhThu - giaVon);
        }

        public SimpleStringProperty thoiGianProperty() { return thoiGian; }
        public SimpleDoubleProperty doanhThuProperty() { return doanhThu; }
        public SimpleDoubleProperty giaVonProperty() { return giaVon; }
        public SimpleDoubleProperty loiNhuanProperty() { return loiNhuan; }
    }
}