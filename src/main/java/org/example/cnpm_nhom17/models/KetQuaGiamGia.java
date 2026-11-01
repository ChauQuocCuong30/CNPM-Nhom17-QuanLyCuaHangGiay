package org.example.cnpm_nhom17.models;

public class KetQuaGiamGia {
    private boolean thanhCong;
    private double soTienGiam;    // Số tiền cụ thể được trừ vào hóa đơn
    private String thongBao;

    public KetQuaGiamGia(boolean hopLe, double soTienGiam, String thongBaoLoi) {
        this.thanhCong = hopLe;
        this.soTienGiam = soTienGiam;
        this.thongBao = thongBaoLoi;
    }

    public boolean isThanhCong() {
        return thanhCong;
    }

    public double getSoTienGiam() {
        return soTienGiam;
    }

    public String getThongBao() {
        return thongBao;
    }

    public KetQuaGiamGia setThanhCong(boolean thanhCong) {
        this.thanhCong = thanhCong;
        return this;
    }

    public KetQuaGiamGia setSoTienGiam(double soTienGiam) {
        this.soTienGiam = soTienGiam;
        return this;
    }

    public KetQuaGiamGia setThongBao(String thongBao) {
        this.thongBao = thongBao;
        return this;
    }
}
