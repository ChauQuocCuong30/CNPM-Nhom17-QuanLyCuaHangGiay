from pathlib import Path
import difflib

readme = """# CNPM Nhóm 17 – Phần mềm quản lý cửa hàng giày

Đồ án môn **Công nghệ phần mềm** – Trường Đại học Nha Trang. Thực hiện theo nhóm **3 người**.
Ứng dụng desktop hỗ trợ quản lý bán hàng và kho cho cửa hàng giày: bán hàng tại quầy, quản lý tồn kho, khách hàng, nhân viên, mã giảm giá và thống kê.

## Công nghệ sử dụng

- **Ngôn ngữ:** Java (JDK 25 theo cấu hình `pom.xml`)
- **Giao diện:** JavaFX 21 (FXML), ControlsFX, BootstrapFX
- **Cơ sở dữ liệu:** SQLite (JDBC)
- **Xuất báo cáo:** Apache POI (Excel)
- **Build:** Maven (có sẵn Maven Wrapper)

## Chức năng chính

- Bán hàng và thanh toán: quét mã vạch, giỏ hàng, áp dụng mã giảm giá, in/lưu hóa đơn
- Quản lý kho: sản phẩm theo size, màu sắc, mã vạch; cảnh báo tồn kho thấp; phiếu nhập hàng, nhà cung cấp
- Quản lý hóa đơn và lịch sử bán hàng
- Quản lý khách hàng, tích lũy điểm
- Quản lý mã giảm giá
- Quản lý nhân viên
- Thống kê doanh thu

## Cơ sở dữ liệu

Cơ sở dữ liệu SQLite được tạo tự động khi chạy ứng dụng lần đầu, gồm 11 bảng:
`DanhMuc`, `SanPham`, `ChiTietSanPham`, `NhaCungCap`, `PhieuNhap`, `ChiTietPhieuNhap`, `KhachHang`, `NhanVien`, `MaGiamGia`, `HoaDon`, `ChiTietHoaDon`.

## Cách chạy

**Yêu cầu:** JDK phù hợp với `pom.xml`, Maven (hoặc dùng `mvnw` có sẵn), IntelliJ IDEA (khuyến nghị).

```bash
git clone <đường dẫn repo này>
cd CNPM
./mvnw clean javafx:run
```

Hoặc mở project bằng IntelliJ IDEA và chạy lớp `Launcher` (hàm `main` tạo database rồi mở giao diện).

## Cấu trúc mã nguồn

```
src/main/java/org/example/cnpm_nhom17/
├── Controller/        # Xử lý giao diện: bán hàng, kho, hóa đơn, khách hàng, mã giảm giá...
├── models/            # Các lớp dữ liệu (SanPham, HoaDon, KhachHang...)
├── database_helper/   # Truy vấn SQLite cho từng chức năng
├── Launcher.java      # Điểm khởi chạy
└── MainApplication.java
src/main/resources/    # Giao diện FXML
```

## Thành viên nhóm

| Thành viên | Phần phụ trách |
|---|---|
| Võ Đức Song Toàn | Giao diện + Thanh Toán |
| Châu Quốc Cường | Giao diện + Cơ sở dữ liệu |
| Hà Văn Tuấn | Giao diện |
"""
old = """git clone <đường dẫn repo này>
cd CNPM
./mvnw clean javafx:run"""
new = """git clone https://github.com/ChauQuocCuong30/CNPM-Nhom17-QuanLyCuaHangGiay.git
cd CNPM-Nhom17-QuanLyCuaHangGiay
./mvnw clean javafx:run"""
assert readme.count(old)==1
updated = readme.replace(old, new)
updated=updated.replace(
"Hoặc mở project bằng IntelliJ IDEA và chạy lớp `Launcher` (hàm `main` tạo database rồi mở giao diện).",
"Trên Windows, có thể chạy lệnh `mvnw.cmd clean javafx:run` thay cho `./mvnw clean javafx:run`.\n\nHoặc mở project bằng IntelliJ IDEA và chạy lớp `Launcher` (hàm `main` tạo database rồi mở giao diện)."
)
out = Path("/mnt/data")
readme_path=out/"README_CNPM_Nhom17_chinh_nhe.md"
patch_path=out/"README_CNPM_Nhom17.patch"
readme_path.write_text(updated,encoding="utf-8")
patch="".join(difflib.unified_diff(readme.splitlines(keepends=True),updated.splitlines(keepends=True),fromfile="a/README.md",tofile="b/README.md"))
patch_path.write_text(patch,encoding="utf-8")
print(patch)
print("Đã tạo:",readme_path.name, patch_path.name)
