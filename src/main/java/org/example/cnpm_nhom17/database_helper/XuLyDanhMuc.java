package org.example.cnpm_nhom17.database_helper;

import java.sql.*;

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

    // =========================================================================
    // 2. QUẢN LÝ SẢN PHẨM GỐC (Ví dụ: Nike Air Force 1)
    // =========================================================================

    // Hàm thêm Sản phẩm gốc mới (Giá vốn sẽ mặc định là 0 cho đến khi có phiếu nhập hàng)
    public static int themSanPhamMoi(String tenSanPham, int maDanhMuc, double giaBanHienTai, String moTa) {
        String sql = "INSERT INTO SanPham (TenSanPham, MaDanhMuc, GiaBanHienTai, MoTa) VALUES (?, ?, ?, ?)";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tenSanPham);
            if (maDanhMuc > 0) pstmt.setInt(2, maDanhMuc); else pstmt.setNull(2, java.sql.Types.INTEGER);
            pstmt.setDouble(3, giaBanHienTai);
            pstmt.setString(4, moTa);

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
