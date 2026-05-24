package org.example.cnpm_nhom17.models;

import java.util.Date;

public class PhieuNhap {
    private int maPhieuNhap;
    private int maNhaCungCap;
    private int maNhanVien;
    private Date ngayNhap;
    private double tongTien;

    public PhieuNhap(int maPhieuNhap, int maNhaCungCap, int maNhanVien, Date ngayNhap, double tongTien) {
        this.maPhieuNhap = maPhieuNhap;
        this.maNhaCungCap = maNhaCungCap;
        this.maNhanVien = maNhanVien;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
    }

    public int getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public PhieuNhap setMaPhieuNhap(int maPhieuNhap) {
        this.maPhieuNhap = maPhieuNhap;
        return this;
    }

    public int getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public PhieuNhap setMaNhaCungCap(int maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
        return this;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public PhieuNhap setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        return this;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public PhieuNhap setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
        return this;
    }

    public double getTongTien() {
        return tongTien;
    }

    public PhieuNhap setTongTien(double tongTien) {
        this.tongTien = tongTien;
        return this;
    }
}
