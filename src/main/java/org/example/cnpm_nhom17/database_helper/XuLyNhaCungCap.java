package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.NhaCungCap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class XuLyNhaCungCap {
    public static List<NhaCungCap> layDanhSachToanBoNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi FROM NhaCungCap";

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                list.add(new NhaCungCap(
                        rs.getInt("MaNhaCungCap"),
                        rs.getString("TenNhaCungCap"),
                        rs.getString("SoDienThoai"),
                        rs.getString("DiaChi")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm nhà cung cấp mới
    public static boolean themNhaCungCap(String tenNCC, String sdt, String diaChi) {
        String sql = "INSERT INTO NhaCungCap (TenNhaCungCap, SoDienThoai, DiaChi) VALUES (?, ?, ?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, tenNCC);
            pst.setString(2, sdt != null ? sdt : ""); // Tránh null
            pst.setString(3, diaChi != null ? diaChi : ""); // Tránh null

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm xóa giữ nguyên
    public static boolean xoaNhaCungCap(int maNCC) {
        String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, maNCC);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
