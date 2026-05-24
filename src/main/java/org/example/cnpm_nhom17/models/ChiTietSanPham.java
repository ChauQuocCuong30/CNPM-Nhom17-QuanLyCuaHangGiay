package org.example.cnpm_nhom17.models;

public class ChiTietSanPham {
    private int maChiTietSanPham;
    private int maSanPham;
    private String barcode;
    private int size;
    private String mauSac;
    private int soLuongTon;
    private int mucCanhBaoToiThieu;

    public ChiTietSanPham(int maChiTietSanPham, int maSanPham, String barcode, int size, String mauSac, int soLuongTon, int mucCanhBaoToiThieu) {
        this.maChiTietSanPham = maChiTietSanPham;
        this.maSanPham = maSanPham;
        this.barcode = barcode;
        this.size = size;
        this.mauSac = mauSac;
        this.soLuongTon = soLuongTon;
        this.mucCanhBaoToiThieu = mucCanhBaoToiThieu;
    }

    public int getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public ChiTietSanPham setMaChiTietSanPham(int maChiTietSanPham) {
        this.maChiTietSanPham = maChiTietSanPham;
        return this;
    }

    public int getMaSanPham() {
        return maSanPham;
    }

    public ChiTietSanPham setMaSanPham(int maSanPham) {
        this.maSanPham = maSanPham;
        return this;
    }

    public String getBarcode() {
        return barcode;
    }

    public ChiTietSanPham setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    public int getSize() {
        return size;
    }

    public ChiTietSanPham setSize(int size) {
        this.size = size;
        return this;
    }

    public String getMauSac() {
        return mauSac;
    }

    public ChiTietSanPham setMauSac(String mauSac) {
        this.mauSac = mauSac;
        return this;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public ChiTietSanPham setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
        return this;
    }

    public int getMucCanhBaoToiThieu() {
        return mucCanhBaoToiThieu;
    }

    public ChiTietSanPham setMucCanhBaoToiThieu(int mucCanhBaoToiThieu) {
        this.mucCanhBaoToiThieu = mucCanhBaoToiThieu;
        return this;
    }
}
