package org.example.cnpm_nhom17.models;

public class CanhBaoTonKho {
    private String tenSanPham;
    private String mauSac;
    private int size;
    private String barcode;
    private int soLuongTon;
    private int mucCanhBaoToiThieu;

    public CanhBaoTonKho(String tenSanPham, String mauSac, int size, String barcode, int soLuongTon, int mucCanhBaoToiThieu) {
        this.tenSanPham = tenSanPham;
        this.mauSac = mauSac;
        this.size = size;
        this.barcode = barcode;
        this.soLuongTon = soLuongTon;
        this.mucCanhBaoToiThieu = mucCanhBaoToiThieu;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public String getMauSac() {
        return mauSac;
    }

    public int getSize() {
        return size;
    }

    public String getBarcode() {
        return barcode;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public int getMucCanhBaoToiThieu() {
        return mucCanhBaoToiThieu;
    }
}
