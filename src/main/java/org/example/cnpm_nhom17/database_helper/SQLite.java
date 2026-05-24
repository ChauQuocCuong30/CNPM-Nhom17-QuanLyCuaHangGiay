package org.example.cnpm_nhom17.database_helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite {
    private static final String URL = "jdbc:sqlite:QuanLyGiay.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void CreateDatabase() {
        String sql = "PRAGMA foreign_keys = ON;\n" +
                "CREATE TABLE IF NOT EXISTS DanhMuc (\n" +
                "    MaDanhMuc INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    TenDanhMuc TEXT NOT NULL UNIQUE\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS NhaCungCap (\n" +
                "    MaNhaCungCap INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    TenNhaCungCap TEXT NOT NULL,\n" +
                "    SoDienThoai TEXT,\n" +
                "    DiaChi TEXT\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS NhanVien (\n" +
                "    MaNhanVien INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    HoTen TEXT NOT NULL,\n" +
                "    ChucVu TEXT NOT NULL DEFAULT 'NhanVienBanHang'\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS KhachHang (\n" +
                "    MaKhachHang INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    HoTen TEXT NOT NULL,\n" +
                "    SoDienThoai TEXT NOT NULL UNIQUE,\n" +
                "    DiemTichLuy INTEGER DEFAULT 0 CHECK (DiemTichLuy >= 0)\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS MaGiamGia (\n" +
                "    MaGiamGia INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    CodeVoucher TEXT NOT NULL UNIQUE,\n" +
                "    LoaiGiamGia TEXT NOT NULL,\n" +
                "    GiaTriGiam REAL NOT NULL CHECK (GiaTriGiam >= 0),\n" +
                "    DiemCanDoi INTEGER DEFAULT 0 CHECK (DiemCanDoi >= 0),\n" +
                "    SoLuong INTEGER DEFAULT 0 CHECK (SoLuong >= 0),\n" +
                "    NgayHetHan TEXT NOT NULL,\n" +
                "    TrangThai TEXT DEFAULT 'HoatDong'\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS SanPham (\n" +
                "    MaSanPham INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    TenSanPham TEXT NOT NULL,\n" +
                "    MaDanhMuc INTEGER,\n" +
                "    GiaVonHienTai REAL DEFAULT 0 CHECK (GiaVonHienTai >= 0),\n" +
                "    GiaBanHienTai REAL DEFAULT 0 CHECK (GiaBanHienTai >= 0),\n" +
                "    MoTa TEXT,\n" +
                "    FOREIGN KEY(MaDanhMuc) REFERENCES DanhMuc(MaDanhMuc) ON DELETE SET NULL\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS ChiTietSanPham (\n" +
                "    MaChiTietSanPham INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaSanPham INTEGER,\n" +
                "    Barcode TEXT NOT NULL UNIQUE,\n" +
                "    Size INTEGER NOT NULL,\n" +
                "    MauSac TEXT NOT NULL,\n" +
                "    SoLuongTon INTEGER DEFAULT 0 CHECK (SoLuongTon >= 0),\n" +
                "    MucCanhBaoToiThieu INTEGER DEFAULT 5,\n" +
                "    FOREIGN KEY(MaSanPham) REFERENCES SanPham(MaSanPham) ON DELETE CASCADE\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS PhieuNhap (\n" +
                "    MaPhieuNhap INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaNhaCungCap INTEGER,\n" +
                "    MaNhanVien INTEGER,\n" +
                "    NgayNhap TEXT DEFAULT CURRENT_TIMESTAMP,\n" +
                "    TongTien REAL DEFAULT 0,\n" +
                "    FOREIGN KEY(MaNhaCungCap) REFERENCES NhaCungCap(MaNhaCungCap),\n" +
                "    FOREIGN KEY(MaNhanVien) REFERENCES NhanVien(MaNhanVien)\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS ChiTietPhieuNhap (\n" +
                "    MaChiTietPhieuNhap INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaPhieuNhap INTEGER,\n" +
                "    MaChiTietSanPham INTEGER,\n" +
                "    SoLuongNhap INTEGER NOT NULL CHECK (SoLuongNhap > 0),\n" +
                "    GiaNhap REAL NOT NULL CHECK (GiaNhap >= 0),\n" +
                "    FOREIGN KEY(MaPhieuNhap) REFERENCES PhieuNhap(MaPhieuNhap) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY(MaChiTietSanPham) REFERENCES ChiTietSanPham(MaChiTietSanPham)\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS HoaDon (\n" +
                "    MaHoaDon INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaKhachHang INTEGER,\n" +
                "    MaNhanVien INTEGER,\n" +
                "    MaGiamGia INTEGER,\n" +
                "    NgayTao TEXT DEFAULT CURRENT_TIMESTAMP,\n" +
                "    TongTienTruocGiam REAL NOT NULL,\n" +
                "    SoTienGiam REAL DEFAULT 0,\n" +
                "    ThanhTien REAL NOT NULL,\n" +
                "    FOREIGN KEY(MaKhachHang) REFERENCES KhachHang(MaKhachHang) ON DELETE SET NULL,\n" +
                "    FOREIGN KEY(MaNhanVien) REFERENCES NhanVien(MaNhanVien),\n" +
                "    FOREIGN KEY(MaGiamGia) REFERENCES MaGiamGia(MaGiamGia) ON DELETE SET NULL\n" +
                ");\n" +
                "CREATE TABLE IF NOT EXISTS ChiTietHoaDon (\n" +
                "    MaChiTietHoaDon INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    MaHoaDon INTEGER,\n" +
                "    MaChiTietSanPham INTEGER,\n" +
                "    SoLuongBan INTEGER NOT NULL CHECK (SoLuongBan > 0),\n" +
                "    GiaBanTaiThoiDiem REAL NOT NULL,\n" +
                "    GiaVonTaiThoiDiem REAL NOT NULL,\n" +
                "    ThanhTien REAL NOT NULL,\n" +
                "    FOREIGN KEY(MaHoaDon) REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,\n" +
                "    FOREIGN KEY(MaChiTietSanPham) REFERENCES ChiTietSanPham(MaChiTietSanPham)\n" +
                ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String[] sqlStatements = sql.split(";");
            for (String statement : sqlStatements) {
                if (!statement.trim().isEmpty()) {
                    stmt.execute(statement);
                }
            }
            System.out.println("Đã khởi tạo cơ sở dữ liệu SQLite thành công!");
        } catch (SQLException e) {
            System.out.println("Lỗi khi khởi tạo CSDL: " + e.getMessage());
        }
    }
}
