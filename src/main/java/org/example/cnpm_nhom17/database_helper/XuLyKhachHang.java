package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.KhachHang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XuLyKhachHang {

    // 1. THÊM KHÁCH HÀNG
    public static boolean themKhachHangMoi(String hoTen, String soDienThoai) {
        String sql = "INSERT INTO KhachHang (HoTen, SoDienThoai, DiemTichLuy) VALUES (?, ?, 0)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoTen);
            pstmt.setString(2, soDienThoai);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm khách hàng: " + e.getMessage());
            return false;
        }
    }

    // 2. TRA CỨU 1 KHÁCH HÀNG THEO SĐT (Dùng khi thanh toán)
    public static KhachHang traCuuKhachHang(String soDienThoai) {
        String sql = "SELECT MaKhachHang, HoTen, SoDienThoai, DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, soDienThoai);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(rs.getInt("MaKhachHang"), rs.getString("HoTen"), rs.getString("SoDienThoai"), rs.getInt("DiemTichLuy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. TRỪ ĐIỂM
    public static boolean truDiemKhachHang(int maKhachHang, int diemCanTru) {
        String sql = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy - ? WHERE MaKhachHang = ? AND DiemTichLuy >= ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, diemCanTru);
            pstmt.setInt(2, maKhachHang);
            pstmt.setInt(3, diemCanTru);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ================== CÁC HÀM MỚI BỔ SUNG CHO TRANG QUẢN LÝ ==================

    // 4. LẤY TOÀN BỘ DANH SÁCH KHÁCH HÀNG
    public static List<KhachHang> layDanhSachKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY MaKhachHang DESC";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new KhachHang(rs.getInt("MaKhachHang"), rs.getString("HoTen"), rs.getString("SoDienThoai"), rs.getInt("DiemTichLuy")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. CẬP NHẬT THÔNG TIN
    public static boolean capNhatKhachHang(int maKH, String hoTen, String sdt, int diem) {
        String sql = "UPDATE KhachHang SET HoTen = ?, SoDienThoai = ?, DiemTichLuy = ? WHERE MaKhachHang = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoTen);
            pstmt.setString(2, sdt);
            pstmt.setInt(3, diem);
            pstmt.setInt(4, maKH);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 6. XÓA KHÁCH HÀNG
    public static boolean xoaKhachHang(int maKH) {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maKH);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}