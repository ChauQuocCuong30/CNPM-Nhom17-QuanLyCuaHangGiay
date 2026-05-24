package org.example.cnpm_nhom17.models;

public class HoaDon {
    private int maHoaDon;
    private Integer maKhachHang;            // Dùng Integer để có thể gán null nếu không có thẻ thành viên
    private int maNhanVien;
    private Integer maGiamGia;              // Dùng Integer để có thể gán null
    private String ngayTao;
    private double tongTienTruocGiam;
    private double soTienGiam;
    private double thanhTien;

    public HoaDon(int maHoaDon, Integer maKhachHang, int maNhanVien, Integer maGiamGia, String ngayTao, double tongTienTruocGiam, double soTienGiam, double thanhTien) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.maNhanVien = maNhanVien;
        this.maGiamGia = maGiamGia;
        this.ngayTao = ngayTao;
        this.tongTienTruocGiam = tongTienTruocGiam;
        this.soTienGiam = soTienGiam;
        this.thanhTien = thanhTien;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public HoaDon setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
        return this;
    }

    public Integer getMaKhachHang() {
        return maKhachHang;
    }

    public HoaDon setMaKhachHang(Integer maKhachHang) {
        this.maKhachHang = maKhachHang;
        return this;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public HoaDon setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        return this;
    }

    public Integer getMaGiamGia() {
        return maGiamGia;
    }

    public HoaDon setMaGiamGia(Integer maGiamGia) {
        this.maGiamGia = maGiamGia;
        return this;
    }

    public String getNgayTao() {
        return ngayTao;
    }

    public HoaDon setNgayTao(String ngayTao) {
        this.ngayTao = ngayTao;
        return this;
    }

    public double getTongTienTruocGiam() {
        return tongTienTruocGiam;
    }

    public HoaDon setTongTienTruocGiam(double tongTienTruocGiam) {
        this.tongTienTruocGiam = tongTienTruocGiam;
        return this;
    }

    public double getSoTienGiam() {
        return soTienGiam;
    }

    public HoaDon setSoTienGiam(double soTienGiam) {
        this.soTienGiam = soTienGiam;
        return this;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public HoaDon setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
        return this;
    }
}
