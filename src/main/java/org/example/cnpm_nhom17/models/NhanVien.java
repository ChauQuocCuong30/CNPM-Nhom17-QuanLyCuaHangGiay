package org.example.cnpm_nhom17.models;

public class NhanVien {
    private int maNhanVien;
    private String hoTen;
    private String chucVu;

    public NhanVien(int maNhanVien, String hoTen, String chucVu) {
        this.maNhanVien = maNhanVien;
        this.hoTen = hoTen;
        this.chucVu = chucVu;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public NhanVien setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
        return this;
    }

    public String getHoTen() {
        return hoTen;
    }

    public NhanVien setHoTen(String hoTen) {
        this.hoTen = hoTen;
        return this;
    }

    public String getChucVu() {
        return chucVu;
    }

    public NhanVien setChucVu(String chucVu) {
        this.chucVu = chucVu;
        return this;
    }
}
