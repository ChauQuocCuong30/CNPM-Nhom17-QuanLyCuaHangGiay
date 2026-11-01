package org.example.cnpm_nhom17.database_helper;

import org.example.cnpm_nhom17.Controller.QuanLyKhachHangController;
import org.example.cnpm_nhom17.models.*;

import java.util.ArrayList;
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
    public static int xuLyThanhToan(List<Product> danhSachSanPhamMua, int ma_giam_gia, String sdt_khach_hang, int ma_nhan_vien, double so_tien_truoc_khi_giam, double thanh_tien_phai_tra, int diem_su_dung) {
        String getKhachHang = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?";
        String insertHoaDon = "INSERT INTO HoaDon (MaKhachHang, MaNhanVien, MaGiamGia, TongTienTruocGiam, SoTienGiam, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String getSpInfo = "SELECT ct.MaChiTietSanPham, sp.GiaVonHienTai, sp.GiaBanHienTai FROM ChiTietSanPham ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham WHERE ct.Barcode = ?";
        String insertChiTietHD = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaChiTietSanPham, SoLuongBan, GiaBanTaiThoiDiem, GiaVonTaiThoiDiem, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String updateKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaChiTietSanPham = ?";
        String updateMaGiamGia = "UPDATE MaGiamGia SET SoLuong = SoLuong - 1 WHERE MaGiamGia = ?";

        String updateDiemKhachHang = "UPDATE KhachHang SET DiemTichLuy = (DiemTichLuy - ?) + ? WHERE MaKhachHang = ?";

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
                    if (maKhachHang != -1) pstmtHD.setInt(1, maKhachHang);
                    else pstmtHD.setNull(1, java.sql.Types.INTEGER);
                    pstmtHD.setInt(2, ma_nhan_vien);
                    if (ma_giam_gia > 0) pstmtHD.setInt(3, ma_giam_gia);
                    else pstmtHD.setNull(3, java.sql.Types.INTEGER);

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
                            conn.rollback();
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
                    int tiLe = QuanLyKhachHangController.tiLeQuyDoiHienTai;
                    int diemThuongMoi = (int) (thanh_tien_phai_tra / tiLe);
                    try (PreparedStatement pstmtDiem = conn.prepareStatement(updateDiemKhachHang)) {
                        pstmtDiem.setInt(1, diem_su_dung);
                        pstmtDiem.setInt(2, diemThuongMoi);
                        pstmtDiem.setInt(3, maKhachHang);
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
    // HÀM 2 & 3: TRA CỨU MÃ GIẢM GIÁ & TRA CỨU ĐIỂM
    // =========================================================================
    public static MaGiamGia traCuuMaGiamGia(String code_voucher) {
        String sql = "SELECT MaGiamGia, CodeVoucher, LoaiGiamGia, GiaTriGiam, DiemCanDoi " +
                "FROM MaGiamGia " +
                "WHERE CodeVoucher = ? AND SoLuong > 0 AND TrangThai = 'HoatDong' AND NgayHetHan >= date('now')";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code_voucher);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new MaGiamGia(
                        rs.getInt("MaGiamGia"), rs.getString("CodeVoucher"),
                        rs.getString("LoaiGiamGia"), rs.getDouble("GiaTriGiam"),
                        rs.getInt("DiemCanDoi"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static KhachHang traCuuDiemKhachHang(String sdt_khach_hang) {
        String sql = "SELECT HoTen, DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sdt_khach_hang);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(rs.getString("HoTen"), rs.getInt("DiemTichLuy"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================================
    // HÀM 4: TRA CỨU SẢN PHẨM THEO BARCODE
    // =========================================================================
    public static ScannedProductInfo traCuuSanPhamTheoBarcode(String barcode_san_pham) {
        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.MaDanhMuc, sp.GiaVonHienTai, sp.GiaBanHienTai, sp.MoTa, " +
                "ct.MaChiTietSanPham, ct.Barcode, ct.Size, ct.MauSac, ct.SoLuongTon, ct.MucCanhBaoToiThieu, " +
                "sp.MaNhaCungCap, sp.GiaNhap, ncc.TenNhaCungCap " +
                "FROM ChiTietSanPham ct " +
                "JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                "LEFT JOIN NhaCungCap ncc ON sp.MaNhaCungCap = ncc.MaNhaCungCap " +
                "WHERE ct.Barcode = ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barcode_san_pham);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                ScannedProductInfo info = new ScannedProductInfo(
                        rs.getInt("MaSanPham"), rs.getString("TenSanPham"), rs.getInt("MaDanhMuc"),
                        rs.getDouble("GiaVonHienTai"), rs.getDouble("GiaBanHienTai"), rs.getString("MoTa"),
                        rs.getInt("MaChiTietSanPham"), rs.getString("Barcode"), rs.getInt("Size"),
                        rs.getString("MauSac"), rs.getInt("SoLuongTon"), rs.getInt("MucCanhBaoToiThieu")
                );

                info.setMaNhaCungCap(rs.getInt("MaNhaCungCap"));
                info.setGiaNhap(rs.getDouble("GiaNhap"));
                info.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                return info;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================================================================
    // HÀM 5 & 6 & 7: VOUCHER, NHÂN VIÊN, TÍNH TIỀN
    // =========================================================================
    public static List<MaGiamGia> danhsachMaGiamGia(int soDiemKhachHang) {
        List<MaGiamGia> danhSachVoucher = new ArrayList<>();
        String sql = "SELECT MaGiamGia, CodeVoucher, LoaiGiamGia, GiaTriGiam, DiemCanDoi FROM MaGiamGia WHERE TrangThai = 'HoatDong' AND DiemCanDoi <= ?";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, soDiemKhachHang);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    danhSachVoucher.add(new MaGiamGia(rs.getInt("MaGiamGia"), rs.getString("CodeVoucher"), rs.getString("LoaiGiamGia"), rs.getDouble("GiaTriGiam"), rs.getInt("DiemCanDoi")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachVoucher;
    }

    public static List<NhanVien> nhanvienThanhToan() {
        List<NhanVien> danhSachNhanVien = new ArrayList<>();
        String sql = "SELECT MaNhanVien, HoTen FROM NhanVien WHERE ChucVu = 'ThuNgan' OR ChucVu = 'ThanhToan'";
        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                danhSachNhanVien.add(new NhanVien(rs.getInt("MaNhanVien"), rs.getString("HoTen")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachNhanVien;
    }

    public static KetQuaGiamGia tinhTienGiamGia(double tongTienTruocGiam, MaGiamGia voucher, KhachHang khachHang) {
        if (voucher == null) return new KetQuaGiamGia(false, 0, "Không tìm thấy Voucher !!!");

        if (voucher.getDiemCanDoi() > 0) {
            if (khachHang == null) return new KetQuaGiamGia(false, 0, "Yêu cầu số điện thoại khách hàng!");
            if (khachHang.getDiemTichLuy() < voucher.getDiemCanDoi()) return new KetQuaGiamGia(false, 0, "Khách không đủ điểm!");
        }

        switch (voucher.getLoaiGiamGia()) {
            case "PhanTram": return new KetQuaGiamGia(true, tongTienTruocGiam * (voucher.getGiaTriGiam() / 100.0), "Thành công !");
            case "SoTien": return new KetQuaGiamGia(true, Math.min(voucher.getGiaTriGiam(), tongTienTruocGiam), "Thành công !");
            default: return new KetQuaGiamGia(false, 0, "Loại giảm giá không hợp lệ!");
        }
    }

    // =========================================================================
    // HÀM 8: LẤY DANH SÁCH TẤT CẢ SẢN PHẨM
    // =========================================================================
    public static List<ScannedProductInfo> layDanhSachTatCaSanPham() {
        List<ScannedProductInfo> list = new ArrayList<>();
        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.MaDanhMuc, sp.GiaVonHienTai, sp.GiaBanHienTai, sp.MoTa, " +
                "ct.MaChiTietSanPham, ct.Barcode, ct.Size, ct.MauSac, ct.SoLuongTon, ct.MucCanhBaoToiThieu, " +
                "sp.MaNhaCungCap, sp.GiaNhap, ncc.TenNhaCungCap " +
                "FROM ChiTietSanPham ct " +
                "JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                "LEFT JOIN NhaCungCap ncc ON sp.MaNhaCungCap = ncc.MaNhaCungCap";

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                ScannedProductInfo info = new ScannedProductInfo(
                        rs.getInt("MaSanPham"), rs.getString("TenSanPham"), rs.getInt("MaDanhMuc"),
                        rs.getDouble("GiaVonHienTai"), rs.getDouble("GiaBanHienTai"), rs.getString("MoTa"),
                        rs.getInt("MaChiTietSanPham"), rs.getString("Barcode"), rs.getInt("Size"),
                        rs.getString("MauSac"), rs.getInt("SoLuongTon"), rs.getInt("MucCanhBaoToiThieu")
                );

                info.setMaNhaCungCap(rs.getInt("MaNhaCungCap"));
                info.setGiaNhap(rs.getDouble("GiaNhap"));
                info.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                list.add(info);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // HÀM 9: TÌM KIẾM SẢN PHẨM NÂNG CAO
    // =========================================================================
    public static List<ScannedProductInfo> timKiemSanPhamNangCao(String ten, String size, String mau, String maDanhMuc) {
        List<ScannedProductInfo> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT sp.MaSanPham, sp.TenSanPham, sp.MaDanhMuc, sp.GiaVonHienTai, sp.GiaBanHienTai, sp.MoTa, " +
                        "ct.MaChiTietSanPham, ct.Barcode, ct.Size, ct.MauSac, ct.SoLuongTon, ct.MucCanhBaoToiThieu, " +
                        "sp.MaNhaCungCap, sp.GiaNhap, ncc.TenNhaCungCap " +
                        "FROM ChiTietSanPham ct " +
                        "JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " +
                        "LEFT JOIN NhaCungCap ncc ON sp.MaNhaCungCap = ncc.MaNhaCungCap WHERE 1=1"
        );

        List<Object> params = new ArrayList<>();

        if (ten != null && !ten.trim().isEmpty()) {
            sql.append(" AND sp.TenSanPham LIKE ?");
            params.add("%" + ten.trim() + "%");
        }

        if (size != null && !size.trim().isEmpty()) {
            sql.append(" AND ct.Size = ?");
            params.add(Integer.parseInt(size.trim()));
        }

        if (mau != null && !mau.trim().isEmpty()) {
            sql.append(" AND ct.MauSac = ?");
            params.add(mau.trim());
        }

        if (maDanhMuc != null && !maDanhMuc.trim().isEmpty()) {
            sql.append(" AND sp.MaDanhMuc = ?");
            params.add(Integer.parseInt(maDanhMuc.trim()));
        }

        try (Connection conn = SQLite.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ScannedProductInfo info = new ScannedProductInfo(
                            rs.getInt("MaSanPham"), rs.getString("TenSanPham"), rs.getInt("MaDanhMuc"),
                            rs.getDouble("GiaVonHienTai"), rs.getDouble("GiaBanHienTai"), rs.getString("MoTa"),
                            rs.getInt("MaChiTietSanPham"), rs.getString("Barcode"), rs.getInt("Size"),
                            rs.getString("MauSac"), rs.getInt("SoLuongTon"), rs.getInt("MucCanhBaoToiThieu")
                    );

                    info.setMaNhaCungCap(rs.getInt("MaNhaCungCap"));
                    info.setGiaNhap(rs.getDouble("GiaNhap"));
                    info.setTenNhaCungCap(rs.getString("TenNhaCungCap"));

                    list.add(info);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // =========================================================================
    // HÀM 10: LẤY DANH SÁCH SIZE, MÀU SẮC ĐỂ LỌC
    // =========================================================================
    public static List<String> layDanhSachSize() {
        List<String> list = new ArrayList<>();
        try (Connection conn = SQLite.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT Size FROM ChiTietSanPham ORDER BY Size ASC"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(String.valueOf(rs.getInt("Size")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<String> layDanhSachMau() {
        List<String> list = new ArrayList<>();
        try (Connection conn = SQLite.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT MauSac FROM ChiTietSanPham WHERE MauSac IS NOT NULL ORDER BY MauSac ASC"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(rs.getString("MauSac"));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public static List<String> layDanhSachDanhMuc() {
        List<String> list = new ArrayList<>();
        try (Connection conn = SQLite.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT MaDanhMuc FROM SanPham ORDER BY MaDanhMuc ASC"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) list.add(String.valueOf(rs.getInt("MaDanhMuc")));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}