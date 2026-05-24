package org.example.cnpm_nhom17.models;

public class ChiTietPhieuNhap {
    private int maChiTietPhieuNhap;
    private int maPhieuNhap;
    private int maChiTietSanPham;
    private int soLuongNhap;
    private double giaNhap;

    public ChiTietPhieuNhap(int maChiTietPhieuNhap, int maPhieuNhap, int maChiTietSanPham, int soLuongNhap, double giaNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
        this.maPhieuNhap = maPhieuNhap;
        this.maChiTietSanPham = maChiTietSanPham;
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
    }

    public int getMaChiTietPhieuNhap() {
        return maChiTietPhieuNhap;
    }

    public ChiTietPhieuNhap setMaChiTietPhieuNhap(int maChiTietPhieuNhap) {
        this.maChiTietPhieuNhap = maChiTietPhieuNhap;
        return this;
    }

    public int getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public ChiTietPhieuNhap setMaPhieuNhap(int maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
        return this;
    }

    public int getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public ChiTietPhieuNhap setMaChiTietSanPham(int maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
        return this;
    }

    public int getSoLuongNhap() {
        return soLuongNhap;
    }

    public ChiTietPhieuNhap setSoLuongNhap(int soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
        return this;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public ChiTietPhieuNhap setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
        return this;
    }
}
