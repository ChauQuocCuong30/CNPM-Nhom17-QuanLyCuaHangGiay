package org.example.cnpm_nhom17.models;

public class MaGiamGia {
    private int maGiamGia;
    private String codeVoucher;
    private String loaiGiamGia;
    private double giaTriGiam;
    private int diemCanDoi;
    private int soLuong;
    private String ngayHetHan;
    private String trangThai;

    public MaGiamGia(int maGiamGia, String codeVoucher, String loaiGiamGia, double giaTriGiam, int diemCanDoi) {
        this.maGiamGia = maGiamGia;
        this.codeVoucher = codeVoucher;
        this.loaiGiamGia = loaiGiamGia;
        this.giaTriGiam = giaTriGiam;
        this.diemCanDoi = diemCanDoi;
    }

    public MaGiamGia(int maGiamGia, String codeVoucher, String loaiGiamGia, double giaTriGiam, int diemCanDoi, int soLuong, String ngayHetHan, String trangThai) {
        this.maGiamGia = maGiamGia;
        this.codeVoucher = codeVoucher;
        this.loaiGiamGia = loaiGiamGia;
        this.giaTriGiam = giaTriGiam;
        this.diemCanDoi = diemCanDoi;
        this.soLuong = soLuong;
        this.ngayHetHan = ngayHetHan;
        this.trangThai = trangThai;
    }

    public int getMaGiamGia() {
        return maGiamGia;
    }

    public MaGiamGia setMaGiamGia(int maGiamGia) {
        this.maGiamGia = maGiamGia;
        return this;
    }

    public String getCodeVoucher() {
        return codeVoucher;
    }

    public MaGiamGia setCodeVoucher(String codeVoucher) {
        this.codeVoucher = codeVoucher;
        return this;
    }

    public String getLoaiGiamGia() {
        return loaiGiamGia;
    }

    public MaGiamGia setLoaiGiamGia(String loaiGiamGia) {
        this.loaiGiamGia = loaiGiamGia;
        return this;
    }

    public double getGiaTriGiam() {
        return giaTriGiam;
    }

    public MaGiamGia setGiaTriGiam(double giaTriGiam) {
        this.giaTriGiam = giaTriGiam;
        return this;
    }

    public int getDiemCanDoi() {
        return diemCanDoi;
    }

    public MaGiamGia setDiemCanDoi(int diemCanDoi) {
        this.diemCanDoi = diemCanDoi;
        return this;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public MaGiamGia setSoLuong(int soLuong) {
        this.soLuong = soLuong;
        return this;
    }

    public String getNgayHetHan() {
        return ngayHetHan;
    }

    public MaGiamGia setNgayHetHan(String ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
        return this;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public MaGiamGia setTrangThai(String trangThai) {
        this.trangThai = trangThai;
        return this;
    }
}
