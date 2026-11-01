package org.example.cnpm_nhom17;

import org.example.cnpm_nhom17.database_helper.SQLite;

import java.sql.Connection;
import java.sql.Statement;

public class Launcher {
    public static void main(String[] args) {
        // 1. Tạo file Database
        SQLite.CreateDatabase();
        // 2. Nạp dữ liệu mẫu để test chức năng
//        try (Connection conn = SQLite.getConnection();
//             Statement stmt = conn.createStatement()) {
//
//            stmt.execute("INSERT OR IGNORE INTO DanhMuc (MaDanhMuc, TenDanhMuc) VALUES (1, 'Giày Thể Thao')");
//            stmt.execute("INSERT OR IGNORE INTO SanPham (MaSanPham, TenSanPham, MaDanhMuc, GiaVonHienTai, GiaBanHienTai, MoTa) VALUES (1, 'Nike Air Force 1 All White', 1, 1500000, 2500000, 'Mẫu giày classic trắng')");
//            stmt.execute("INSERT OR IGNORE INTO ChiTietSanPham (MaSanPham, Barcode, Size, MauSac, SoLuongTon, MucCanhBaoToiThieu) VALUES (1, '88889999', 42, 'Trắng', 50, 5)");
//            stmt.execute("INSERT OR IGNORE INTO ChiTietSanPham (MaSanPham, Barcode, Size, MauSac, SoLuongTon, MucCanhBaoToiThieu) VALUES (1, '88887777', 39, 'Trắng', 20, 5)");
//            stmt.execute("INSERT OR IGNORE INTO KhachHang (MaKhachHang, HoTen, SoDienThoai, DiemTichLuy) VALUES (1, 'Châu Quốc Cường', '0123456789', 500)");
//
//            System.out.println("========== ĐÃ NẠP DỮ LIỆU MẪU VÀO KHO THÀNH CÔNG ==========");
//        } catch (Exception e) {
//            System.out.println("Lỗi nạp dữ liệu: " + e.getMessage());
//        }

        // 2. Mở giao diện
        MainApplication.main(args);
    }
}