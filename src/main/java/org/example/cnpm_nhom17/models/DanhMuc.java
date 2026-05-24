package org.example.cnpm_nhom17.models;

public class DanhMuc {
    private int maDanhMuc;
    private String tenDanhMuc;

    public DanhMuc(int maDanhMuc, String tenDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        this.tenDanhMuc = tenDanhMuc;
    }

    public int getMaDanhMuc() {
        return maDanhMuc;
    }

    public DanhMuc setMaDanhMuc(int maDanhMuc) {
        this.maDanhMuc = maDanhMuc;
        return this;
    }

    public String getTenDanhMuc() {
        return tenDanhMuc;
    }

    public DanhMuc setTenDanhMuc(String tenDanhMuc) {
        this.tenDanhMuc = tenDanhMuc;
        return this;
    }
}
