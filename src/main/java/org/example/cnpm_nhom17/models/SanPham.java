package org.example.cnpm_nhom17.models;

public class SanPham {
    private int maSanPham;
    private String tenSanPham;
    private int maDanhMuc;
    private double giaVonHienTai;
    private double giaBanHienTai;
    private String moTa;

    public SanPham(int maSanPham, String tenSanPham, int maDanhMuc, double giaVonHienTai, double giaBanHienTai, String moTa) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.maDanhMuc = maDanhMuc;
        this.giaVonHienTai = giaVonHienTai;
        this.giaBanHienTai = giaBanHienTai;
        this.moTa = moTa;
    }

    public int getMaSanPham() {
        return maSanPham;
    }

    public SanPham setMaSanPham(int maSanPham) {
        this.maSanPham = maSanPham;
        return this;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public SanPham setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
        return this;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public SanPham setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        return this;
    }

    public double getGiaVonHienTai() {
        return giaVonHienTai;
    }

    public SanPham setGiaVonHienTai(double giaVonHienTai) {
        this.giaVonHienTai = giaVonHienTai;
        return this;
    }

    public double getGiaBanHienTai() {
        return giaBanHienTai;
    }

    public SanPham setGiaBanHienTai(double giaBanHienTai) {
        this.giaBanHienTai = giaBanHienTai;
        return this;
    }

    public String getMoTa() {
        return moTa;
    }

    public SanPham setMoTa(String moTa) {
        this.moTa = moTa;
        return this;
    }
}
