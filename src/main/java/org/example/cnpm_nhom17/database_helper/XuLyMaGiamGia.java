package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.MaGiamGia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XuLyMaGiamGia {

    public static List<MaGiamGia> layToanBoDanhSachMaGiamGia() {
        List<MaGiamGia> danhSach = new ArrayList<>();

        String sql = "SELECT MaGiamGia, CodeVoucher, LoaiGiamGia, GiaTriGiam, DiemCanDoi, SoLuong, NgayHetHan, TrangThai " +
                "FROM MaGiamGia ORDER BY MaGiamGia DESC";

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int maGiamGia = rs.getInt("MaGiamGia");
                String codeVoucher = rs.getString("CodeVoucher");
                String loaiGiamGia = rs.getString("LoaiGiamGia");
                double giaTriGiam = rs.getDouble("GiaTriGiam");
                int diemCanDoi = rs.getInt("DiemCanDoi");
                int soLuong = rs.getInt("SoLuong"); // Lấy dữ liệu số lượng từ DB
                String ngayHetHan = rs.getString("NgayHetHan");
                String trangThai = rs.getString("TrangThai");

                MaGiamGia voucher = new MaGiamGia(maGiamGia, codeVoucher, loaiGiamGia, giaTriGiam, diemCanDoi, soLuong, ngayHetHan, trangThai);

                danhSach.add(voucher);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy toàn bộ danh sách mã giảm giá: " + e.getMessage());
            e.printStackTrace();
        }

        return danhSach;
    }

    // =========================================================================
    // HÀM 1: THÊM MÃ GIẢM GIÁ MỚI
    // =========================================================================
    public static boolean themMaGiamGia(String code, String loai, double giaTri, int soLuong, int diem, String ngayHetHan, String trangThai) {
        String sql = "INSERT INTO MaGiamGia (CodeVoucher, LoaiGiamGia, GiaTriGiam, DiemCanDoi, SoLuong, NgayHetHan, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setString(2, loai);
            pstmt.setDouble(3, giaTri);
            pstmt.setInt(4, diem);
            pstmt.setInt(5, soLuong); // Nạp số lượng
            pstmt.setString(6, ngayHetHan);
            pstmt.setString(7, trangThai);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi thêm voucher (Có thể trùng Code): " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // HÀM 2: CẬP NHẬT THÔNG TIN MÃ GIẢM GIÁ
    // =========================================================================
    public static boolean capNhatMaGiamGia(int maGiamGia, String code, String loai, double giaTri, int soLuong, int diem, String ngayHetHan, String trangThai) {

        String sql = "UPDATE MaGiamGia SET CodeVoucher = ?, LoaiGiamGia = ?, GiaTriGiam = ?, SoLuong = ?, DiemCanDoi = ?, NgayHetHan = ?, TrangThai = ? WHERE MaGiamGia = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            pstmt.setString(2, loai);
            pstmt.setDouble(3, giaTri);
            pstmt.setInt(4, soLuong); // Cập nhật số lượng
            pstmt.setInt(5, diem);
            pstmt.setString(6, ngayHetHan);
            pstmt.setString(7, trangThai);
            pstmt.setInt(8, maGiamGia);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật voucher: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // HÀM 3: XÓA MÃ GIẢM GIÁ
    // =========================================================================
    public static boolean xoaMaGiamGia(int maGiamGia) {
        String sql = "DELETE FROM MaGiamGia WHERE MaGiamGia = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maGiamGia);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa voucher: " + e.getMessage());
            return false;
        }
    }
}