package org.example.cnpm_nhom17.models;

public class KhachHang {
    private int maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private int diemTichLuy ;

    public KhachHang(int maKhachHang, String hoTen, String soDienThoai, int diemTichLuy) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diemTichLuy = diemTichLuy;
    }

    public int getMaKhachHang() {
        return maKhachHang;
    }

    public KhachHang setMaKhachHang(int maKhachHang) {
        this.maKhachHang = maKhachHang;
        return this;
    }

    public String getHoTen() {
        return hoTen;
    }

    public KhachHang setHoTen(String hoTen) {
        this.hoTen = hoTen;
        return this;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public KhachHang setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
        return this;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public KhachHang setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
        return this;
    }
}
