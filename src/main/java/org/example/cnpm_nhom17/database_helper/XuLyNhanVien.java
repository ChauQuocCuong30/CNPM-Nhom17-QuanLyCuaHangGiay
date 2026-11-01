package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.NhanVien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class XuLyNhanVien {

    public static List<NhanVien> layDanhSachNhanVien() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien ORDER BY MaNhanVien ASC";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // Giả sử model NhanVien có constructor (int, String, String)
                list.add(new NhanVien(rs.getInt("MaNhanVien"), rs.getString("HoTen"), rs.getString("ChucVu")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean themNhanVien(String hoTen, String chucVu) {
        String sql = "INSERT INTO NhanVien (HoTen, ChucVu) VALUES (?, ?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoTen);
            pstmt.setString(2, chucVu);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean capNhatNhanVien(int maNV, String hoTen, String chucVu) {
        String sql = "UPDATE NhanVien SET HoTen = ?, ChucVu = ? WHERE MaNhanVien = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, hoTen);
            pstmt.setString(2, chucVu);
            pstmt.setInt(3, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean xoaNhanVien(int maNV) {
        String sql = "DELETE FROM NhanVien WHERE MaNhanVien = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}