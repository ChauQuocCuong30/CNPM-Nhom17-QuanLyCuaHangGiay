package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.DanhMuc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XuLyDanhMuc {
    // =========================================================================
    // 1. QUẢN LÝ DANH MỤC (Ví dụ: Giày Chạy Bộ, Giày Bóng Rổ...)
    // =========================================================================

    // Hàm thêm danh mục mới
    public static boolean themDanhMuc(String tenDanhMuc) {
        String sql = "INSERT INTO DanhMuc (TenDanhMuc) VALUES (?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDanhMuc);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi thêm danh mục (Có thể tên bị trùng): " + e.getMessage());
            return false;
        }
    }

    // Hàm lấy danh sách toàn bộ danh mục đổ lên bảng
    public static List<DanhMuc> layDanhSachToanBoDanhMuc() {
        List<DanhMuc> list = new ArrayList<>();
        String sql = "SELECT * FROM DanhMuc ORDER BY MaDanhMuc ASC";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                list.add(new DanhMuc(rs.getInt("MaDanhMuc"), rs.getString("TenDanhMuc")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Hàm cập nhật tên danh mục
    public static boolean capNhatDanhMuc(int maDanhMuc, String tenDanhMucMoi) {
        String sql = "UPDATE DanhMuc SET TenDanhMuc = ? WHERE MaDanhMuc = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, tenDanhMucMoi);
            pstmt.setInt(2, maDanhMuc);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật danh mục (Có thể tên bị trùng): " + e.getMessage());
            return false;
        }
    }

    // Hàm xóa danh mục
    public static boolean xoaDanhMuc(int maDanhMuc) {
        String sql = "DELETE FROM DanhMuc WHERE MaDanhMuc = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maDanhMuc);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa danh mục (Danh mục có thể đang chứa sản phẩm): " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // 2. QUẢN LÝ SẢN PHẨM GỐC (Ví dụ: Nike Air Force 1)
    // =========================================================================

    // Hàm thêm Sản phẩm gốc mới
    public static int themSanPhamMoi(String tenSanPham, int maDanhMuc, int maNhaCungCap, double giaNhap, double giaBanHienTai, String moTa) {
        String sql = "INSERT INTO SanPham (TenSanPham, MaDanhMuc, MaNhaCungCap, GiaNhap, GiaBanHienTai, MoTa) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tenSanPham);

            // Xử lý Danh Mục
            if (maDanhMuc > 0) pstmt.setInt(2, maDanhMuc);
            else pstmt.setNull(2, java.sql.Types.INTEGER);

            // Xử lý Nhà Cung Cấp
            if (maNhaCungCap > 0) pstmt.setInt(3, maNhaCungCap);
            else pstmt.setNull(3, java.sql.Types.INTEGER);

            pstmt.setDouble(4, giaNhap);
            pstmt.setDouble(5, giaBanHienTai);
            pstmt.setString(6, moTa);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Trả về ID của Sản phẩm vừa tạo
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Hàm cập nhật toàn bộ thông tin của Sản phẩm gốc
    public static boolean capNhatThongTinSanPham(int maSanPham, String tenSanPham, int maDanhMuc, int maNhaCungCap, double giaNhap, double giaBan, String moTa) {
        String sql = "UPDATE SanPham SET TenSanPham = ?, MaDanhMuc = ?, MaNhaCungCap = ?, GiaNhap = ?, GiaBanHienTai = ?, MoTa = ? WHERE MaSanPham = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenSanPham);

            if (maDanhMuc > 0) pstmt.setInt(2, maDanhMuc);
            else pstmt.setNull(2, java.sql.Types.INTEGER);

            if (maNhaCungCap > 0) pstmt.setInt(3, maNhaCungCap);
            else pstmt.setNull(3, java.sql.Types.INTEGER);

            pstmt.setDouble(4, giaNhap);
            pstmt.setDouble(5, giaBan);
            pstmt.setString(6, moTa);
            pstmt.setInt(7, maSanPham);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Hàm xóa Sản phẩm gốc
    public static boolean xoaSanPhamGoc(int maSanPham) {
        String sql = "DELETE FROM SanPham WHERE MaSanPham = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, maSanPham);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sản phẩm gốc: " + e.getMessage());
            return false;
        }
    }

    // Hàm cập nhật lại Giá Bán cho sản phẩm (khi muốn thay đổi giá niêm yết)
    public static boolean capNhatGiaBan(int maSanPham, double giaBanMoi) {
        String sql = "UPDATE SanPham SET GiaBanHienTai = ? WHERE MaSanPham = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, giaBanMoi);
            pstmt.setInt(2, maSanPham);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // 3. QUẢN LÝ CHI TIẾT SẢN PHẨM (Phân loại Size, Màu sắc và sinh Barcode)
    // =========================================================================

    // Hàm thêm một biến thể (Size/Màu) cho Sản phẩm gốc
    public static boolean themChiTietSanPham(int maSanPham, String barcode, int size, String mauSac, int mucCanhBaoToiThieu) {
        String sql = "INSERT INTO ChiTietSanPham (MaSanPham, Barcode, Size, MauSac, MucCanhBaoToiThieu, SoLuongTon) VALUES (?, ?, ?, ?, ?, 0)";
        // SoLuongTon khi mới tạo mặc định là 0. Hàng chỉ được thêm vào kho thông qua thao tác Nhập Hàng (PhieuNhap)

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, maSanPham);
            pstmt.setString(2, barcode);
            pstmt.setInt(3, size);
            pstmt.setString(4, mauSac);
            pstmt.setInt(5, mucCanhBaoToiThieu);

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Lỗi thêm chi tiết sản phẩm (Có thể Barcode bị trùng): " + e.getMessage());
            return false;
        }
    }
}