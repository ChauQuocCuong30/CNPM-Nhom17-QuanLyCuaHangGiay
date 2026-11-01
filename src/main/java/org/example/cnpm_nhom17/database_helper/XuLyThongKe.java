package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.Controller.QuanLyHoaDonController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class XuLyThongKe {

    // ==========================================
    // LỚP DTO ĐỂ HỨNG DỮ LIỆU ĐỔ LÊN BẢNG HIỂN THỊ
    // ==========================================
    public static class HoaDonDTO {
        private int maHoaDon;
        private String ngayTao;
        private String tenKhachHang;
        private double thanhTien;

        public HoaDonDTO(int maHoaDon, String ngayTao, String tenKhachHang, double thanhTien) {
            this.maHoaDon = maHoaDon;
            this.ngayTao = ngayTao;
            this.tenKhachHang = tenKhachHang;
            this.thanhTien = thanhTien;
        }
        public int getMaHoaDon() { return maHoaDon; }
        public String getNgayTao() { return ngayTao; }
        public String getTenKhachHang() { return tenKhachHang; }
        public double getThanhTien() { return thanhTien; }
    }

    public static class ChiTietHoaDonDTO {
        private String tenGiay;
        private int size;
        private String mauSac;
        private int soLuong;
        private double giaBan;

        public ChiTietHoaDonDTO(String tenGiay, int size, String mauSac, int soLuong, double giaBan) {
            this.tenGiay = tenGiay;
            this.size = size;
            this.mauSac = mauSac;
            this.soLuong = soLuong;
            this.giaBan = giaBan;
        }
        public String getTenGiay() { return tenGiay; }
        public int getSize() { return size; }
        public String getMauSac() { return mauSac; }
        public int getSoLuong() { return soLuong; }
        public double getGiaBan() { return giaBan; }
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ DATABASE
    // ==========================================

    // 1. Lấy danh sách Hóa Đơn (JOIN với bảng KhachHang để lấy tên)
    public static List<HoaDonDTO> layDanhSachHoaDon() {
        List<HoaDonDTO> list = new ArrayList<>();
        String sql = "SELECT h.MaHoaDon, h.NgayTao, IFNULL(k.HoTen, 'Khách vãng lai') AS TenKhach, h.ThanhTien " +
                "FROM HoaDon h LEFT JOIN KhachHang k ON h.MaKhachHang = k.MaKhachHang " +
                "ORDER BY h.MaHoaDon DESC";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new HoaDonDTO(rs.getInt("MaHoaDon"), rs.getString("NgayTao"), rs.getString("TenKhach"), rs.getDouble("ThanhTien")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 2. Lấy Chi Tiết Hóa Đơn theo ID Hóa Đơn
    public static List<ChiTietHoaDonDTO> layChiTietHoaDon(int maHD) {
        List<ChiTietHoaDonDTO> list = new ArrayList<>();
        String sql = "SELECT sp.TenSanPham, ctsp.Size, ctsp.MauSac, cthd.SoLuongBan, cthd.GiaBanTaiThoiDiem " +
                "FROM ChiTietHoaDon cthd " +
                "JOIN ChiTietSanPham ctsp ON cthd.MaChiTietSanPham = ctsp.MaChiTietSanPham " +
                "JOIN SanPham sp ON ctsp.MaSanPham = sp.MaSanPham " +
                "WHERE cthd.MaHoaDon = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maHD);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ChiTietHoaDonDTO(rs.getString("TenSanPham"), rs.getInt("Size"), rs.getString("MauSac"), rs.getInt("SoLuongBan"), rs.getDouble("GiaBanTaiThoiDiem")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 3. Tính Tổng Doanh Thu (Tiền thu được từ khách)
    public static double tinhTongDoanhThu(String tuNgay, String denNgay) {
        String sql = "SELECT SUM(ThanhTien) AS DoanhThu FROM HoaDon WHERE NgayTao >= ? AND NgayTao <= ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tuNgay);
            pstmt.setString(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("DoanhThu");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 4. Tính Tổng Giá Vốn (GiaVonTaiThoiDiem * SoLuongBan trong bảng ChiTietHoaDon)
    public static double tinhTongGiaVon(String tuNgay, String denNgay) {
        String sql = "SELECT SUM(cthd.GiaVonTaiThoiDiem * cthd.SoLuongBan) AS GiaVon " +
                "FROM ChiTietHoaDon cthd JOIN HoaDon hd ON cthd.MaHoaDon = hd.MaHoaDon " +
                "WHERE hd.NgayTao >= ? AND hd.NgayTao <= ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tuNgay);
            pstmt.setString(2, denNgay);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble("GiaVon");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static List<QuanLyHoaDonController.BaoCaoDoanhThuDTO> layChiTietBaoCao(String tuNgay, String denNgay, String tieuChi) {
        List<QuanLyHoaDonController.BaoCaoDoanhThuDTO> list = new ArrayList<>();

        // Dùng LinkedHashMap để tự động giữ đúng thứ tự thời gian tăng dần từ câu lệnh SQL
        Map<String, double[]> mapThongKe = new LinkedHashMap<>();

        // Khai báo bộ định dạng thời gian (Formatter) dựa theo Tiêu chí người dùng chọn
        DateTimeFormatter formatterHienThi;
        if (tieuChi.equals("Theo Ngày")) {
            formatterHienThi = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        } else if (tieuChi.equals("Theo Tháng")) {
            formatterHienThi = DateTimeFormatter.ofPattern("MM/yyyy");
        } else { // Theo Năm
            formatterHienThi = DateTimeFormatter.ofPattern("yyyy");
        }

        String sql = "SELECT NgayTao, ThanhTien AS DoanhThu, " +
                "(SELECT SUM(ct.SoLuong * sp.GiaVon) FROM ChiTietHoaDon ct JOIN ChiTietSanPham sp ON ct.MaChiTietSanPham = sp.MaChiTietSanPham WHERE ct.MaHoaDon = hd.MaHoaDon) AS GiaVon " +
                "FROM HoaDon hd " +
                "WHERE NgayTao BETWEEN ? AND ? " +
                "ORDER BY NgayTao ASC";


        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tuNgay);
            pstmt.setString(2, denNgay);
            ResultSet rs = pstmt.executeQuery();

            DateTimeFormatter formatterDB = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            while (rs.next()) {
                String ngayTaoDb = rs.getString("NgayTao");
                double doanhThu = rs.getDouble("DoanhThu");
                double giaVon = rs.getDouble("GiaVon");

                // BƯỚC 1: Chuyển chuỗi thô từ DB sang đối tượng thời gian của Java
                LocalDateTime dateTime = LocalDateTime.parse(ngayTaoDb, formatterDB);

                // BƯỚC 2: Định dạng lại thời gian thành String làm "Chìa khóa" (Key) để gom nhóm
                // VD: Chọn "Theo Tháng" -> "05/2026"
                String keyThoiGian = dateTime.format(formatterHienThi);

                // BƯỚC 3: Cộng dồn dữ liệu vào Map
                // Mảng double[] chứa 2 giá trị: Index 0 là Doanh Thu, Index 1 là Giá Vốn
                mapThongKe.putIfAbsent(keyThoiGian, new double[]{0.0, 0.0});
                mapThongKe.get(keyThoiGian)[0] += doanhThu;
                mapThongKe.get(keyThoiGian)[1] += giaVon;
            }

            // BƯỚC 4: Rút toàn bộ dữ liệu đã gom nhóm từ Map ra và đóng gói thành List DTO cho bảng
            for (Map.Entry<String, double[]> entry : mapThongKe.entrySet()) {
                String thoiGian = entry.getKey();
                double tongDoanhThu = entry.getValue()[0];
                double tongGiaVon = entry.getValue()[1];

                list.add(new QuanLyHoaDonController.BaoCaoDoanhThuDTO(thoiGian, tongDoanhThu, tongGiaVon));
            }

        } catch (Exception e) {
            System.out.println("Lỗi tính toán chi tiết báo cáo: " + e.getMessage());
            e.printStackTrace();
        }

        // Đảo ngược List để đưa dữ liệu của ngày/tháng mới nhất lên trên cùng của Bảng
        Collections.reverse(list);

        return list;
    }
}