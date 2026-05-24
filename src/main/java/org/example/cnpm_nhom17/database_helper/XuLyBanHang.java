package org.example.cnpm_nhom17.database_helper;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class XuLyBanHang {

    // =========================================================================
    // LỚP HỖ TRỢ ĐỂ NHẬN DỮ LIỆU TỪ GIAO DIỆN (UI) XUỐNG KHI QUÉT MÃ VẠCH
    // =========================================================================
    public static class Product {
        private String barcode;
        private int soLuong;

        public Product(String barcode, int quantity) {
            this.barcode = barcode;
            this.soLuong = quantity;
        }

        public String getBarcode() {
            return barcode;
        }

        public int getSoLuong() {
            return soLuong;
        }
    }

    // =========================================================================
    // HÀM 1: XỬ LÝ THANH TOÁN
    // =========================================================================
    public static int xuLyThanhToan (List<Product> danhSachSanPhamMua , int ma_giam_gia, String sdt_khach_hang, int ma_nhan_vien, double so_tien_truoc_khi_giam, double thanh_tien_phai_tra){
        String getKhachHang = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?";
        String insertHoaDon = "INSERT INTO HoaDon (MaKhachHang, MaNhanVien, MaGiamGia, TongTienTruocGiam, SoTienGiam, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String getSpInfo = "SELECT ct.MaChiTietSanPham, sp.GiaVonHienTai, sp.GiaBanHienTai FROM ChiTietSanPham ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham WHERE ct.Barcode = ?";
        String insertChiTietHD = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaChiTietSanPham, SoLuongBan, GiaBanTaiThoiDiem, GiaVonTaiThoiDiem, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String updateKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaChiTietSanPham = ?";
        String updateMaGiamGia = "UPDATE MaGiamGia SET SoLuong = SoLuong - 1 WHERE MaGiamGia = ?";
        String updateDiemKhachHang = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKhachHang = ?";

        try (Connection conn = SQLite.getConnection()) {
            conn.setAutoCommit(false);

            try {
                // 1. Lấy mã Khách Hàng
                int maKhachHang = -1;
                if (sdt_khach_hang != null && !sdt_khach_hang.trim().isEmpty()) {
                    try (PreparedStatement pstmtKH = conn.prepareStatement(getKhachHang)) {
                        pstmtKH.setString(1, sdt_khach_hang);
                        ResultSet rsKH = pstmtKH.executeQuery();
                        if (rsKH.next()) {
                            maKhachHang = rsKH.getInt("MaKhachHang");
                        }
                    }
                }

                // 2. Tạo Hóa Đơn Mới
                int maHoaDonMoi = -1;
                try (PreparedStatement pstmtHD = conn.prepareStatement(insertHoaDon, Statement.RETURN_GENERATED_KEYS)) {
                    if (maKhachHang != -1) pstmtHD.setInt(1, maKhachHang); else pstmtHD.setNull(1, java.sql.Types.INTEGER);
                    pstmtHD.setInt(2, ma_nhan_vien);
                    if (ma_giam_gia > 0) pstmtHD.setInt(3, ma_giam_gia); else pstmtHD.setNull(3, java.sql.Types.INTEGER);

                    pstmtHD.setDouble(4, so_tien_truoc_khi_giam);
                    pstmtHD.setDouble(5, so_tien_truoc_khi_giam - thanh_tien_phai_tra);
                    pstmtHD.setDouble(6, thanh_tien_phai_tra);
                    pstmtHD.executeUpdate();

                    ResultSet rsHD = pstmtHD.getGeneratedKeys();
                    if (rsHD.next()) {
                        maHoaDonMoi = rsHD.getInt(1);
                    } else {
                        conn.rollback();
                        return -1;
                    }
                }

                // 3. Xử lý Chi Tiết Hóa Đơn và Trừ Kho
                try (PreparedStatement pstmtSpInfo = conn.prepareStatement(getSpInfo);
                     PreparedStatement pstmtCTHD = conn.prepareStatement(insertChiTietHD);
                     PreparedStatement pstmtKho = conn.prepareStatement(updateKho)) {

                    for (Product p : danhSachSanPhamMua) {
                        String barcode = p.getBarcode();
                        int soLuongBan = p.getSoLuong();

                        // Lấy tự động thông tin từ Barcode
                        pstmtSpInfo.setString(1, barcode);
                        ResultSet rsSp = pstmtSpInfo.executeQuery();

                        if (rsSp.next()) {
                            int maChiTietSanPham = rsSp.getInt("MaChiTietSanPham");
                            double giaVon = rsSp.getDouble("GiaVonHienTai");
                            double giaBan = rsSp.getDouble("GiaBanHienTai");
                            double thanhTienCTHD = giaBan * soLuongBan;

                            pstmtCTHD.setInt(1, maHoaDonMoi);
                            pstmtCTHD.setInt(2, maChiTietSanPham);
                            pstmtCTHD.setInt(3, soLuongBan);
                            pstmtCTHD.setDouble(4, giaBan);
                            pstmtCTHD.setDouble(5, giaVon);
                            pstmtCTHD.setDouble(6, thanhTienCTHD);
                            pstmtCTHD.executeUpdate();

                            pstmtKho.setInt(1, soLuongBan);
                            pstmtKho.setInt(2, maChiTietSanPham);
                            pstmtKho.executeUpdate();
                        } else {
                            conn.rollback(); // Gặp barcode lỗi thì hủy nguyên hóa đơn
                            return -1;
                        }
                    }
                }

                // 4. Cập nhật Mã Giảm Giá
                if (ma_giam_gia > 0) {
                    try (PreparedStatement pstmtMGG = conn.prepareStatement(updateMaGiamGia)) {
                        pstmtMGG.setInt(1, ma_giam_gia);
                        pstmtMGG.executeUpdate();
                    }
                }

                // 5. Cập nhật Điểm Khách Hàng
                if (maKhachHang != -1) {
                    int diemThuongMoi = (int) (thanh_tien_phai_tra / 10000);
                    try (PreparedStatement pstmtDiem = conn.prepareStatement(updateDiemKhachHang)) {
                        pstmtDiem.setInt(1, diemThuongMoi);
                        pstmtDiem.setInt(2, maKhachHang);
                        pstmtDiem.executeUpdate();
                    }
                }

                conn.commit();
                return maHoaDonMoi;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // =========================================================================
    // HÀM 2: TRA CỨU MÃ GIẢM GIÁ
    // =========================================================================
    public static String traCuuMaGiamGia(String code_voucher) {
        String sql = "SELECT LoaiGiamGia, GiaTriGiam FROM MaGiamGia WHERE CodeVoucher = ? AND SoLuong > 0 AND TrangThai = 'HoatDong' AND NgayHetHan >= date('now')";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code_voucher);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String loaiGiamGia = rs.getString("LoaiGiamGia");
                double giaTriGiam = rs.getDouble("GiaTriGiam");
                return "Chương trình: " + loaiGiamGia + " | Mức giảm: " + giaTriGiam;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Lỗi: Mã giảm giá không hợp lệ, đã hết hạn hoặc hết lượt sử dụng!";
    }

    // =========================================================================
    // HÀM 3: TRA CỨU ĐIỂM KHÁCH HÀNG
    // =========================================================================
    public static int traCuuDiemKhachHang(String sdt_khach_hang) {
        String sql = "SELECT DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt_khach_hang);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("DiemTichLuy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // =========================================================================
    // HÀM 4: TRA CỨU SẢN PHẨM THEO BARCODE
    // =========================================================================
    public static String traCuuSanPhamTheoBarcode(String barcode_san_pham) {
        String sql = "SELECT sp.TenSanPham, sp.GiaBanHienTai, ct.MauSac, ct.Size " +
                "FROM ChiTietSanPham ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                "WHERE ct.Barcode = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, barcode_san_pham);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return "Tên SP: " + rs.getString("TenSanPham") +
                        " | Đơn giá: " + rs.getDouble("GiaBanHienTai") +
                        " | Màu sắc: " + rs.getString("MauSac") +
                        " | Size: " + rs.getInt("Size");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không tìm thấy sản phẩm với mã Barcode này!";
    }
}

