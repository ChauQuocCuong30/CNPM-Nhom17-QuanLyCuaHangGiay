package org.example.cnpm_nhom17.models;

public class CartItem {
    private String tenGiay;
    private int size;
    private int soLuong;
    private double donGia;
    private double thanhTien;
    private String barcode; // Lưu ngầm barcode để lát nữa trừ kho

    public CartItem(String tenGiay, int size, int soLuong, double donGia, String barcode) {
        this.tenGiay = tenGiay;
        this.size = size;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = soLuong * donGia;
        this.barcode = barcode;
    }

    // Các hàm Getter bắt buộc phải có để TableView đọc được dữ liệu
    public String getTenGiay() { return tenGiay; }
    public int getSize() { return size; }
    public int getSoLuong() { return soLuong; }
    public double getDonGia() { return donGia; }
    public double getThanhTien() { return thanhTien; }
    public String getBarcode() { return barcode; }

    // Hàm tăng số lượng khi quét trùng mã vạch
    public void tangSoLuong(int them) {
        this.soLuong += them;
        this.thanhTien = this.soLuong * this.donGia;
    }
}