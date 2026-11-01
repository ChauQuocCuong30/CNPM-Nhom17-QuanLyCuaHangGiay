package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.models.CanhBaoTonKho;
import org.example.cnpm_nhom17.models.NhanVien; // Thêm thư viện NhanVien

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class XuLyKho {

    // Lớp trung gian nhận dữ liệu từ màn hình Nhập Hàng & Mã vạch
    public static class ItemNhap {
        private String barcode;
        private int soLuongNhap;
        private double giaNhap;

        public ItemNhap(String barcode, int soLuongNhap, double giaNhap) {
            this.barcode = barcode;
            this.soLuongNhap = soLuongNhap;
            this.giaNhap = giaNhap;
        }

        public String getBarcode() {
            return barcode;
        }

        public int getSoLuongNhap() {
            return soLuongNhap;
        }

        public double getGiaNhap() {
            return giaNhap;
        }
    }

    // =========================================================================
    // HÀM 1: XỬ LÝ NHẬP HÀNG & TÍNH LẠI GIÁ VỐN BÌNH QUÂN
    // =========================================================================
    public static boolean taoPhieuNhap(int maNhaCungCap, int maNhanVien, List<ItemNhap> danhSachNhap) {
        String insertPhieuNhap = "INSERT INTO PhieuNhap (MaNhaCungCap, MaNhanVien, TongTien) VALUES (?, ?, ?)";
        String insertChiTietPN = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaChiTietSanPham, SoLuongNhap, GiaNhap) VALUES (?, ?, ?, ?)";
        String updateKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaChiTietSanPham = ?";

        // Truy vấn để lấy thông tin tính giá vốn
        String getThongTinSp = "SELECT ct.MaChiTietSanPham, sp.MaSanPham, sp.GiaVonHienTai FROM ChiTietSanPham ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham WHERE ct.Barcode = ?";
        String getTongTonKho = "SELECT SUM(SoLuongTon) AS TongTon FROM ChiTietSanPham WHERE MaSanPham = ?";
        String updateGiaVon = "UPDATE SanPham SET GiaVonHienTai = ? WHERE MaSanPham = ?";

        try (Connection conn = SQLite.getConnection()) {
            conn.setAutoCommit(false); // Bật Transaction

            try {
                // 1. Tạo Phiếu Nhập tạm thời (chưa có tổng tiền)
                int maPhieuNhapMoi = -1;
                try (PreparedStatement pstmtPN = conn.prepareStatement(insertPhieuNhap, Statement.RETURN_GENERATED_KEYS)) {
                    pstmtPN.setInt(1, maNhaCungCap);
                    pstmtPN.setInt(2, maNhanVien);
                    pstmtPN.setDouble(3, 0); // Sẽ update lại sau khi tính tổng
                    pstmtPN.executeUpdate();

                    ResultSet rsPN = pstmtPN.getGeneratedKeys();
                    if (rsPN.next()) maPhieuNhapMoi = rsPN.getInt(1);
                    else { conn.rollback(); return false; }
                }

                double tongTienPhieuNhap = 0;

                // 2. Xử lý từng sản phẩm nhập vào
                try (PreparedStatement pstmtGetThongTin = conn.prepareStatement(getThongTinSp);
                     PreparedStatement pstmtGetTongTon = conn.prepareStatement(getTongTonKho);
                     PreparedStatement pstmtCTPN = conn.prepareStatement(insertChiTietPN);
                     PreparedStatement pstmtKho = conn.prepareStatement(updateKho);
                     PreparedStatement pstmtUpdateGiaVon = conn.prepareStatement(updateGiaVon)) {

                    for (ItemNhap item : danhSachNhap) {
                        pstmtGetThongTin.setString(1, item.getBarcode());
                        ResultSet rsThongTin = pstmtGetThongTin.executeQuery();

                        if (rsThongTin.next()) {
                            int maChiTietSanPham = rsThongTin.getInt("MaChiTietSanPham");
                            int maSanPham = rsThongTin.getInt("MaSanPham");
                            double giaVonCu = rsThongTin.getDouble("GiaVonHienTai");

                            // Tính tiền cho phiếu nhập
                            double thanhTienItem = item.getSoLuongNhap() * item.getGiaNhap();
                            tongTienPhieuNhap += thanhTienItem;

                            // Insert Chi tiết phiếu nhập
                            pstmtCTPN.setInt(1, maPhieuNhapMoi);
                            pstmtCTPN.setInt(2, maChiTietSanPham);
                            pstmtCTPN.setInt(3, item.getSoLuongNhap());
                            pstmtCTPN.setDouble(4, item.getGiaNhap());
                            pstmtCTPN.executeUpdate();

                            // Lấy TỔNG tồn kho hiện tại của mẫu giày này (tất cả các size/màu) TRƯỚC KHI cộng thêm
                            pstmtGetTongTon.setInt(1, maSanPham);
                            ResultSet rsTongTon = pstmtGetTongTon.executeQuery();
                            int tongTonCu = rsTongTon.next() ? rsTongTon.getInt("TongTon") : 0;

                            // TÍNH GIÁ VỐN BÌNH QUÂN MỚI
                            double giaVonMoi = ((tongTonCu * giaVonCu) + thanhTienItem) / (tongTonCu + item.getSoLuongNhap());

                            // Cập nhật giá vốn mới vào bảng SanPham
                            pstmtUpdateGiaVon.setDouble(1, giaVonMoi);
                            pstmtUpdateGiaVon.setInt(2, maSanPham);
                            pstmtUpdateGiaVon.executeUpdate();

                            // Cập nhật số lượng tồn vào kho cho đúng barcode (size/màu) đó
                            pstmtKho.setInt(1, item.getSoLuongNhap());
                            pstmtKho.setInt(2, maChiTietSanPham);
                            pstmtKho.executeUpdate();
                        } else {
                            conn.rollback(); // Barcode không tồn tại
                            return false;
                        }
                    }
                }

                // 3. Cập nhật lại Tổng Tiền cho Phiếu Nhập
                String updateTongTien = "UPDATE PhieuNhap SET TongTien = ? WHERE MaPhieuNhap = ?";
                try (PreparedStatement pstmtUpdateTong = conn.prepareStatement(updateTongTien)) {
                    pstmtUpdateTong.setDouble(1, tongTienPhieuNhap);
                    pstmtUpdateTong.setInt(2, maPhieuNhapMoi);
                    pstmtUpdateTong.executeUpdate();
                }

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // HÀM 2: CẢNH BÁO HẾT HÀNG
    // =========================================================================
    public static List<CanhBaoTonKho> layDanhSachCanhBaoHetHang() {
        List<CanhBaoTonKho> danhSachCanhBao = new ArrayList<>();

        String sql = "SELECT sp.TenSanPham, ct.MauSac, ct.Size, ct.SoLuongTon, ct.MucCanhBaoToiThieu, ct.Barcode " +
                "FROM ChiTietSanPham ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                "WHERE ct.SoLuongTon <= ct.MucCanhBaoToiThieu";

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String ten = rs.getString("TenSanPham");
                String mau = rs.getString("MauSac");
                int size = rs.getInt("Size");
                String barcode = rs.getString("Barcode");
                int ton = rs.getInt("SoLuongTon");
                int min = rs.getInt("MucCanhBaoToiThieu");

                danhSachCanhBao.add(new CanhBaoTonKho(ten, mau, size, barcode, ton, min));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachCanhBao;
    }

    // =========================================================================
    // HÀM 3: CẬP NHẬT THÔNG TIN CHI TIẾT SẢN PHẨM (Form bên phải)
    // =========================================================================
    // Cho phép sửa Barcode, Size, Màu sắc và Mức cảnh báo của 1 đôi giày
    public static boolean capNhatChiTietSanPham(String barcodeCu, String barcodeMoi, int size, String mauSac, int mucCanhBao) {
        String sql = "UPDATE ChiTietSanPham SET Barcode = ?, Size = ?, MauSac = ?, MucCanhBaoToiThieu = ? WHERE Barcode = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcodeMoi);
            pstmt.setInt(2, size);
            pstmt.setString(3, mauSac);
            pstmt.setInt(4, mucCanhBao);
            pstmt.setString(5, barcodeCu);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật sản phẩm (Có thể Barcode mới bị trùng): " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // HÀM 4: XÓA SẢN PHẨM KHỎI KHO (Nút Xóa)
    // =========================================================================
    // Xóa dựa trên Barcode
    public static boolean xoaChiTietSanPham(String barcode) {
        String sql = "DELETE FROM ChiTietSanPham WHERE Barcode = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi xóa sản phẩm: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // HÀM 5: LẤY DANH SÁCH NHÀ CUNG CẤP
    // =========================================================================
    public static List<String> layDanhSachTenNhaCungCap() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT MaNhaCungCap, TenNhaCungCap FROM NhaCungCap";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(rs.getInt("MaNhaCungCap") + " - " + rs.getString("TenNhaCungCap"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // HÀM 6: LẤY DANH SÁCH NHÂN VIÊN KHO VÀ QUẢN LÝ (MỚI THÊM)
    // =========================================================================
    public static List<NhanVien> nhanvienKhoVaQuanLy() {
        List<NhanVien> danhSachNhanVien = new ArrayList<>();
        // Lọc nhân viên có chức vụ Kho hoặc QuanLy
        String sql = "SELECT MaNhanVien, HoTen " +
                "FROM NhanVien " +
                "WHERE ChucVu = 'Kho' OR ChucVu = 'QuanLy'";

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int maNhanVien = rs.getInt("MaNhanVien");
                String hoTen = rs.getString("HoTen");
                NhanVien nv = new NhanVien(maNhanVien, hoTen);
                danhSachNhanVien.add(nv);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách NV Kho & Quản lý: " + e.getMessage());
            e.printStackTrace();
        }
        return danhSachNhanVien;
    }
}