package org.example.cnpm_nhom17.models;

public class ScannedProductInfo {
    private int maSanPham;
    private String tenSanPham;
    private int maDanhMuc;
    private double giaVonHienTai;
    private double giaBanHienTai;
    private String moTa;

    private int maChiTietSanPham;
    private String barcode;
    private int size;
    private String mauSac;
    private int soLuongTon;
    private int mucCanhBaoToiThieu;

    private int maNhaCungCap;
    private String tenNhaCungCap;
    private double giaNhap;

    public int getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public ScannedProductInfo setMaNhaCungCap(int maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
        return this;
    }

    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public ScannedProductInfo setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
        return this;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public ScannedProductInfo setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
        return this;
    }

    public ScannedProductInfo(int maSanPham, String tenSanPham, int maDanhMuc, double giaVonHienTai, double giaBanHienTai, String moTa, int maChiTietSanPham, String barcode, int size, String mauSac, int soLuongTon, int mucCanhBaoToiThieu) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.maDanhMuc = maDanhMuc;
        this.giaVonHienTai = giaVonHienTai;
        this.giaBanHienTai = giaBanHienTai;
        this.moTa = moTa;
        this.maChiTietSanPham = maChiTietSanPham;
        this.barcode = barcode;
        this.size = size;
        this.mauSac = mauSac;
        this.soLuongTon = soLuongTon;
        this.mucCanhBaoToiThieu = mucCanhBaoToiThieu;
    }

    public int getMaSanPham() {
        return maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public double getGiaVonHienTai() {
        return giaVonHienTai;
    }

    public double getGiaBanHienTai() {
        return giaBanHienTai;
    }

    public String getMoTa() {
        return moTa;
    }

    public int getMaChiTietSanPham() {
        return maChiTietSanPham;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getSize() {
        return size;
    }

    public String getMauSac() {
        return mauSac;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public int getMucCanhBaoToiThieu() {
        return mucCanhBaoToiThieu;
    }
}
