package org.example.cnpm_nhom17.models;

public class ChiTietHoaDon {
    private int maChiTietHoaDon;
    private int maHoaDon;
    private int maChiTietSanPham;
    private int soLuongBan;
    private double giaBanTaiThoiDiem;
    private double giaVonTaiThoiDiem;
    private double thanhTien;

    public ChiTietHoaDon(int maChiTietHoaDon, int maHoaDon, int maChiTietSanPham, int soLuongBan, double giaBanTaiThoiDiem, double giaVonTaiThoiDiem, double thanhTien) {
        this.maChiTietHoaDon = maChiTietHoaDon;
        this.maHoaDon = maHoaDon;
        this.maChiTietSanPham = maChiTietSanPham;
        this.soLuongBan = soLuongBan;
        this.giaBanTaiThoiDiem = giaBanTaiThoiDiem;
        this.giaVonTaiThoiDiem = giaVonTaiThoiDiem;
        this.thanhTien = thanhTien;
    }

    public int getMaChiTietHoaDon() {
        return maChiTietHoaDon;
    }

    public ChiTietHoaDon setMaChiTietHoaDon(int maChiTietHoaDon) {
        this.maChiTietHoaDon = maChiTietHoaDon;
        return this;
    }

    public int getMaHoaDon() {
        return maHoaDon;
    }

    public ChiTietHoaDon setMaHoaDon(int maHoaDon) {
        this.maHoaDon = maHoaDon;
        return this;
    }

    public int getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public ChiTietHoaDon setMaChiTietSanPham(int maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
        return this;
    }

    public int getSoLuongBan() {
        return soLuongBan;
    }

    public ChiTietHoaDon setSoLuongBan(int soLuongBan) {
        this.soLuongBan = soLuongBan;
        return this;
    }

    public double getGiaBanTaiThoiDiem() {
        return giaBanTaiThoiDiem;
    }

    public ChiTietHoaDon setGiaBanTaiThoiDiem(double giaBanTaiThoiDiem) {
        this.giaBanTaiThoiDiem = giaBanTaiThoiDiem;
        return this;
    }

    public double getGiaVonTaiThoiDiem() {
        return giaVonTaiThoiDiem;
    }

    public ChiTietHoaDon setGiaVonTaiThoiDiem(double giaVonTaiThoiDiem) {
        this.giaVonTaiThoiDiem = giaVonTaiThoiDiem;
        return this;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public ChiTietHoaDon setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
        return this;
    }
}
