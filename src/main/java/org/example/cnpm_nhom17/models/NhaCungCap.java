package org.example.cnpm_nhom17.models;

public class NhaCungCap {
    private int maNhaCungCap;
    private String tenNhaCungCap;
    private String soDienThoai;
    private String diaChi;

    public NhaCungCap(int maNhaCungCap, String tenNhaCungCap, String soDienThoai, String diaChi) {
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.soDienThoai = soDienThoai;
        this.diaChi = diaChi;
    }

    public int getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public NhaCungCap setMaNhaCungCap(int maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
        return this;
    }

    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public NhaCungCap setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
        return this;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public NhaCungCap setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
        return this;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public NhaCungCap setDiaChi(String diaChi) {
        this.diaChi = diaChi;
        return this;
    }
}
