package org.example.cnpm_nhom17.Controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

// Import các lớp model
import org.example.cnpm_nhom17.database_helper.XuLyBanHang;
import org.example.cnpm_nhom17.models.MaGiamGia;
import org.example.cnpm_nhom17.models.NhanVien;
import org.example.cnpm_nhom17.models.ScannedProductInfo;
import org.example.cnpm_nhom17.models.KhachHang;

public class ThanhToanController implements Initializable {

    @FXML
    private TableView<ProductItem> productTableView;
    @FXML
    private TableColumn<ProductItem, String> productNameCol;
    @FXML
    private TableColumn<ProductItem, Integer> quantityCol;
    @FXML
    private TableColumn<ProductItem, Double> unitPriceCol;
    @FXML
    private TableColumn<ProductItem, Double> totalPriceCol;
    @FXML
    private TableColumn<ProductItem, Void> actionCol;

    @FXML
    private ComboBox<NhanVien> employeeComboBox;
    @FXML
    private TextField barcodeTextField;

    @FXML
    private TextField voucherDisplayTextField;
    @FXML
    private ComboBox<MaGiamGia> allVouchersComboBox;
    @FXML
    private TextField customerPhoneTextField;
    @FXML
    private Label customerPointsLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label discountAmountLabel;
    @FXML
    private Label finalAmountLabel;

    @FXML
    private TextField customerMoneyTextField; // Thêm vào
    @FXML
    private Label changeAmountLabel; // Thêm vào
    @FXML
    private Button checkoutButton; // Thêm nút thanh toán để chuyển focus đến

    private ObservableList<ProductItem> cartItems = FXCollections.observableArrayList();
    private MaGiamGia appliedVoucher = null;
    private int currentCustomerPoints = 0; // Điểm tích lũy thực tế của KH từ DB
    private String currentCustomerName = ""; // NEW: Tên khách hàng hiện tại
    // Điểm tiềm năng = (currentCustomerPoints + điểm quy đổi từ giỏ hàng hiện tại)
    // Sẽ dùng để lọc voucher.
    private int currentPotentialPoints = 0;
    private double currentFinalAmount = 0.0; // Biến để lưu tổng tiền cuối cùng

    // Định dạng tiền tệ
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0đ");
    // Giả định định dạng ngày tháng trong DB là "yyyy-MM-dd"
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize TableView
        productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        // Định dạng đơn giá
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        unitPriceCol.setCellFactory(tc -> new TableCell<ProductItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(CURRENCY_FORMAT.format(price));
                }
            }
        });

        // Định dạng tổng tiền từng mặt hàng
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setCellFactory(tc -> new TableCell<ProductItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(CURRENCY_FORMAT.format(price));
                }
            }
        });

        // Custom CellFactory cho cột số lượng
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(new Callback<TableColumn<ProductItem, Integer>, TableCell<ProductItem, Integer>>() {
            @Override
            public TableCell<ProductItem, Integer> call(TableColumn<ProductItem, Integer> param) {
                return new TableCell<ProductItem, Integer>() {
                    private final HBox hbox = new HBox(5);
                    private final Button minusBtn = new Button("-");
                    private final TextField quantityField = new TextField();
                    private final Button plusBtn = new Button("+");
                    private boolean isUpdating = false;

                    {
                        // Cấu hình TextField
                        quantityField.setPrefWidth(50);
                        quantityField.setStyle("-fx-text-alignment: center; -fx-font-size: 12px;");

                        // Cấu hình nút Minus
                        minusBtn.setPrefWidth(40);
                        minusBtn.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                        minusBtn.setOnAction(e -> decreaseQuantity());

                        // Cấu hình nút Plus
                        plusBtn.setPrefWidth(40);
                        plusBtn.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                        plusBtn.setOnAction(e -> increaseQuantity());

                        // Lắng nghe thay đổi TextField
                        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
                            if (!isUpdating && getTableRow() != null && getTableRow().getItem() != null) {
                                try {
                                    int newQuantity = Integer.parseInt(newVal);
                                    ProductItem item = getTableRow().getItem();
                                    int soLuongTon = item.getSoLuongTon();

                                    if (newQuantity >= 1 && newQuantity <= soLuongTon) {
                                        item.setQuantity(newQuantity);
                                        calculateTotals();
                                        updateVoucherComboBoxAndDisplay(true);
                                    } else if (newQuantity > soLuongTon) {
                                        showAlert(Alert.AlertType.WARNING, "Không đủ hàng!",
                                                "Sản phẩm này chỉ còn " + soLuongTon + " cái trong kho.");
                                        isUpdating = true;
                                        quantityField.setText(String.valueOf(item.getQuantity()));
                                        isUpdating = false;
                                    } else if (newQuantity < 1) {
                                        showAlert(Alert.AlertType.WARNING, "Số lượng không hợp lệ!",
                                                "Số lượng phải >= 1.");
                                        isUpdating = true;
                                        quantityField.setText(String.valueOf(getTableRow().getItem().getQuantity()));
                                        isUpdating = false;
                                    }
                                } catch (NumberFormatException ex) {
                                    // Bỏ qua nếu không phải số
                                }
                            }
                        });

                        // Sắp xếp layout: [-] [TextField] [+]
                        hbox.setAlignment(Pos.CENTER);
                        hbox.getChildren().addAll(minusBtn, quantityField, plusBtn);
                    }

                    private void increaseQuantity() {
                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            ProductItem item = getTableRow().getItem();
                            int soLuongTon = item.getSoLuongTon();
                            int currentQty = item.getQuantity();

                            if (currentQty < soLuongTon) {
                                item.setQuantity(currentQty + 1);
                                updateQuantityField();
                                calculateTotals();
                                updateVoucherComboBoxAndDisplay(true);
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Không đủ hàng!",
                                        "Sản phẩm này chỉ còn " + soLuongTon + " cái trong kho.");
                            }
                        }
                    }

                    private void decreaseQuantity() {
                        if (getTableRow() != null && getTableRow().getItem() != null) {
                            ProductItem item = getTableRow().getItem();
                            int currentQty = item.getQuantity();

                            if (currentQty > 1) {
                                item.setQuantity(currentQty - 1);
                                updateQuantityField();
                                calculateTotals();
                                updateVoucherComboBoxAndDisplay(true);
                            } else {
                                showAlert(Alert.AlertType.WARNING, "Số lượng tối thiểu!",
                                        "Số lượng không thể < 1. Hãy xóa sản phẩm nếu không cần.");
                            }
                        }
                    }

                    private void updateQuantityField() {
                        isUpdating = true;
                        quantityField.setText(String.valueOf(getTableRow().getItem().getQuantity()));
                        isUpdating = false;
                    }

                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                            setGraphic(null);
                        } else {
                            isUpdating = true;
                            quantityField.setText(String.valueOf(item));
                            isUpdating = false;
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });


        // Cột hành động (nút Xóa)
        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        actionCol.setCellFactory(new Callback<TableColumn<ProductItem, Void>, TableCell<ProductItem, Void>>() {
            @Override
            public TableCell<ProductItem, Void> call(final TableColumn<ProductItem, Void> param) {
                final TableCell<ProductItem, Void> cell = new TableCell<ProductItem, Void>() {
                    private final Button btn = new Button("Xóa");
                    {
                        btn.setOnAction((event) -> {
                            ProductItem data = getTableView().getItems().get(getIndex());
                            cartItems.remove(data);
                            calculateTotals();
                            updateVoucherComboBoxAndDisplay(true);
                        });
                        btn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });

        productTableView.setItems(cartItems);

        // Populate Employee ComboBox
        List<NhanVien> nhanVienList = XuLyBanHang.nhanvienThanhToan();
        employeeComboBox.setItems(FXCollections.observableArrayList(nhanVienList));
        employeeComboBox.getSelectionModel().selectFirst();
        employeeComboBox.setCellFactory(lv -> new ListCell<NhanVien>() {
            @Override
            protected void updateItem(NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getHoTen());
            }
        });
        employeeComboBox.setButtonCell(new ListCell<NhanVien>() {
            @Override
            protected void updateItem(NhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getHoTen());
            }
        });

        // Customer Phone TextField listener
        customerPhoneTextField.setOnAction(event -> {
            updateCustomerInfoAndVouchers(true); // Ưu tiên chọn mã tốt nhất khi nhập SĐT
            Platform.runLater(() -> checkoutButton.requestFocus()); // Chuyển focus đến nút Thanh Toán
        });
        customerPhoneTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!oldVal.equals(newVal) && newVal.isEmpty()) {
                currentCustomerPoints = 0;
                currentCustomerName = ""; // Reset tên khách hàng
                customerPointsLabel.setText("Điểm tích lũy: 0 điểm");
                updateVoucherComboBoxAndDisplay(true);
            }
        });
        // Xử lý khi customerPhoneTextField mất focus (click ra ngoài hoặc Tab)
        customerPhoneTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && !customerPhoneTextField.getText().trim().isEmpty()) { // Nếu mất focus và không trống
                updateCustomerInfoAndVouchers(false); // Không ưu tiên chọn mã tốt nhất khi chỉ mất focus
            } else if (!newVal && customerPhoneTextField.getText().trim().isEmpty()) { // Nếu mất focus và trống
                currentCustomerPoints = 0;
                currentCustomerName = ""; // Reset tên khách hàng
                customerPointsLabel.setText("Điểm tích lũy: 0 điểm");
                updateVoucherComboBoxAndDisplay(true);
            }
        });


        // Voucher Display TextField (không cho phép sửa trực tiếp)
        voucherDisplayTextField.setEditable(false);
        voucherDisplayTextField.setOnMouseClicked(event -> allVouchersComboBox.show());

        // Đảm bảo setCellFactory cho ComboBox để hiển thị đúng
        allVouchersComboBox.setCellFactory(lv -> new ListCell<MaGiamGia>() {
            @Override
            protected void updateItem(MaGiamGia voucher, boolean empty) {
                super.updateItem(voucher, empty);
                if (empty || voucher == null) {
                    setText(null);
                } else {
                    String info = voucher.getCodeVoucher() + " - ";

                    if (voucher.getLoaiGiamGia().equals("PhanTram")) {
                        // Hiển thị % rõ ràng
                        info += "Giảm " + (int)(voucher.getGiaTriGiam() * 100) + "%";
                    } else if (voucher.getLoaiGiamGia().equals("TienMat")) {
                        // Hiển thị tiền mặt rõ ràng
                        info += "Giảm " + CURRENCY_FORMAT.format(voucher.getGiaTriGiam());
                    }
                    setText(info);
                }
            }
        });

        // Listener cho ComboBox chọn mã giảm giá
        allVouchersComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            appliedVoucher = newVal; // Gán voucher được chọn
            if (appliedVoucher != null) {
                voucherDisplayTextField.setText(appliedVoucher.getCodeVoucher());
            } else {
                voucherDisplayTextField.setText("Chọn mã giảm giá");
            }
            calculateTotals(); // Tính lại tổng tiền sau khi áp dụng voucher mới
        });

        // Thay vì addEventFilter cho KEY_PRESSED, dùng setOnAction cho TextField
        barcodeTextField.setOnAction(event -> {
            handleAddProduct();
        });

        // Lắng nghe sự thay đổi của customerMoneyTextField để tính tiền thối
        customerMoneyTextField.setOnAction(event -> {
            calculateChangeAmount();
            Platform.runLater(() -> checkoutButton.requestFocus());
        });

        customerMoneyTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            calculateChangeAmount();
            // ✅ Khi tiền thay đổi → tự động cập nhật & áp dụng voucher tốt nhất
            updateVoucherComboBoxAndDisplay(true);
        });

        customerMoneyTextField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                calculateChangeAmount();
                // ✅ Khi mất focus → cập nhật voucher
                updateVoucherComboBoxAndDisplay(true);
            }
        });



        // Khởi tạo các giá trị ban đầu và cập nhật voucher
        calculateTotals();
        updateVoucherComboBoxAndDisplay(true); // Lần đầu tải không cần ưu tiên chọn mã nào
    }

    // Hàm này sẽ cập nhật thông tin khách hàng và tự động cập nhật danh sách voucher
    private void updateCustomerInfoAndVouchers(boolean selectBestVoucher) {
        String customerPhone = customerPhoneTextField.getText().trim();
        if (!customerPhone.isEmpty()) {
            KhachHang khachHang = XuLyBanHang.traCuuDiemKhachHang(customerPhone);
            if (khachHang != null) {
                currentCustomerPoints = khachHang.getDiemTichLuy();
                currentCustomerName = khachHang.getHoTen();
                customerPointsLabel.setText(currentCustomerName + ": " + currentCustomerPoints + " điểm");
            } else {
                currentCustomerPoints = 0;
                currentCustomerName = "";
                customerPointsLabel.setText("Điểm tích lũy: 0 điểm (Khách hàng mới)");
            }
        } else {
            currentCustomerPoints = 0;
            currentCustomerName = "";
            customerPointsLabel.setText("Điểm tích lũy: 0 điểm");
        }
        updateVoucherComboBoxAndDisplay(selectBestVoucher); // Cập nhật voucher và ưu tiên chọn mã tốt nhất
        calculateTotals(); // Tính lại tổng tiền
    }

    @FXML
    private void handleAddProduct() {
        String barcode = barcodeTextField.getText().trim();

        if (barcode.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng nhập mã vạch!");
            return;
        }

        ScannedProductInfo scannedProduct = XuLyBanHang.traCuuSanPhamTheoBarcode(barcode);

        if (scannedProduct == null) {
            showAlert(Alert.AlertType.ERROR, "❌ Mã vạch không tồn tại",
                    "Sản phẩm với mã '" + barcode + "' không có trong hệ thống.\n\n" +
                            "Vui lòng:\n" +
                            "• Kiểm tra lại mã vạch\n" +
                            "• Hoặc thêm sản phẩm mới vào kho");

            barcodeTextField.clear();
            return;
        }

        int quantityToAdd = 1;

        Optional<ProductItem> existingItem = cartItems.stream()
                .filter(item -> item.getMaChiTietSanPham() == scannedProduct.getMaChiTietSanPham())
                .findFirst();

        if (existingItem.isPresent()) {
            ProductItem item = existingItem.get();
            if (item.getQuantity() + quantityToAdd <= scannedProduct.getSoLuongTon()) {
                item.setQuantity(item.getQuantity() + quantityToAdd);
                productTableView.refresh();
            } else {
                showAlert(Alert.AlertType.WARNING, "Không đủ hàng!", "Sản phẩm " + item.getProductName() + " chỉ còn " + scannedProduct.getSoLuongTon() + " sản phẩm trong kho. Không thể thêm.");
            }
        } else {
            if (quantityToAdd <= scannedProduct.getSoLuongTon()) {
                ProductItem newItem = new ProductItem(scannedProduct, quantityToAdd);
                cartItems.add(newItem);
            } else {
                showAlert(Alert.AlertType.WARNING, "Không đủ hàng!", "Sản phẩm " + scannedProduct.getTenSanPham() + " chỉ còn " + scannedProduct.getSoLuongTon() + " sản phẩm trong kho. Không thể thêm.");
            }
        }

        calculateTotals();
        barcodeTextField.clear();
        barcodeTextField.requestFocus();
        updateVoucherComboBoxAndDisplay(true);
    }

    // Thêm tham số `selectBestVoucher` để điều khiển việc tự động chọn voucher
    private void updateVoucherComboBoxAndDisplay(boolean selectBestVoucher) {
        double totalBeforeDiscount = cartItems.stream().mapToDouble(ProductItem::getTotalPrice).sum();

        int soDiemDeLocVoucher;
        String customerPhone = customerPhoneTextField.getText().trim();

        if (customerPhone.isEmpty()) {
            soDiemDeLocVoucher = (int) (totalBeforeDiscount);
        } else {
            soDiemDeLocVoucher = currentCustomerPoints * 1000 + (int) (totalBeforeDiscount);
        }
        currentPotentialPoints = soDiemDeLocVoucher;

        // Lấy tất cả voucher thỏa điều kiện
        List<MaGiamGia> Vouchers = XuLyBanHang.danhsachMaGiamGia(soDiemDeLocVoucher);

        // Sắp xếp các voucher: giảm nhiều nhất -> giảm ít nhất
        Vouchers.sort(Comparator
                .<MaGiamGia, Double>comparing(v -> calculateDiscountValue(v, totalBeforeDiscount), Comparator.reverseOrder())
                .thenComparing(MaGiamGia::getCodeVoucher));

        // Cập nhật ComboBox
        allVouchersComboBox.setItems(FXCollections.observableArrayList(Vouchers));

        // LUÔN LUÔN tự động chọn voucher tốt nhất (không cần điều kiện selectBestVoucher)
        if (!Vouchers.isEmpty()) {
            MaGiamGia bestVoucher = Vouchers.get(0); // Mã giảm giá tốt nhất (đầu tiên)
            appliedVoucher = bestVoucher;
            voucherDisplayTextField.setText(bestVoucher.getCodeVoucher()); // Hiển thị mã vào textfield
            allVouchersComboBox.getSelectionModel().select(bestVoucher); // Chọn trong ComboBox
        } else {
            // Không có voucher nào hợp lệ
            appliedVoucher = null;
            voucherDisplayTextField.setText("Không có mã giảm giá phù hợp");
            allVouchersComboBox.getSelectionModel().clearSelection();
        }

        calculateTotals();
    }


    // Hàm tính giá trị giảm của voucher
    // Đã điều chỉnh để xử lý % đúng cách
    private double calculateDiscountValue(MaGiamGia voucher, double totalAmount) {
        if (voucher == null) return 0.0;
        if (voucher.getLoaiGiamGia() == null) return 0.0;

        if (voucher.getLoaiGiamGia().equals("PhanTram")) {
            return totalAmount * voucher.getGiaTriGiam()/100;
        } else if (voucher.getLoaiGiamGia().equals("TienMat") || voucher.getLoaiGiamGia().equals("Diem")) {
            return voucher.getGiaTriGiam();
        }
        return 0.0;
    }

    private void calculateTotals() {
        double totalBeforeDiscount = cartItems.stream().mapToDouble(ProductItem::getTotalPrice).sum();
        totalAmountLabel.setText(CURRENCY_FORMAT.format(totalBeforeDiscount));

        double currentVoucherDiscount = calculateDiscountValue(appliedVoucher, totalBeforeDiscount);

        discountAmountLabel.setText(formatDiscountText(currentVoucherDiscount));

        double finalAmount = totalBeforeDiscount - currentVoucherDiscount;
        if (finalAmount < 0) finalAmount = 0;
        finalAmountLabel.setText(CURRENCY_FORMAT.format(finalAmount));
        currentFinalAmount = finalAmount; // Cập nhật biến tổng tiền cuối cùng

        calculateChangeAmount(); // Gọi hàm tính tiền thối sau khi tổng tiền cuối cùng thay đổi
    }

    private void calculateChangeAmount() {
        try {
            // Chuyển đổi chuỗi từ TextField thành số double. Loại bỏ ký tự không phải số nếu có.
            String customerMoneyText = customerMoneyTextField.getText().trim().replace(",", "").replace("đ", "");
            double customerMoney = Double.parseDouble(customerMoneyText);
            double change = customerMoney - currentFinalAmount;
            changeAmountLabel.setText(CURRENCY_FORMAT.format(change));
            if (change < 0) {
                changeAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: red;");
            } else {
                changeAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: blue;");
            }
        } catch (NumberFormatException e) {
            changeAmountLabel.setText("0đ"); // Nếu nhập không hợp lệ hoặc trống thì reset tiền thối
            changeAmountLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: blue;");
        }
    }

    private String formatDiscountText(double voucherDiscount) {
        if (voucherDiscount > 0) {
            return "-" + CURRENCY_FORMAT.format(voucherDiscount);
        } else {
            return "-0đ"; // Mặc định hiển thị -0đ khi không có giảm giá
        }
    }

    @FXML
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "❌ Giỏ hàng trống",
                    "Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán!");
            return;
        }

        NhanVien selectedEmployee = employeeComboBox.getSelectionModel().getSelectedItem();
        if (selectedEmployee == null) {
            showAlert(Alert.AlertType.WARNING, "⚠️ Chọn nhân viên",
                    "Vui lòng chọn nhân viên phụ trách giao dịch này!");
            return;
        }
        int maNhanVien = selectedEmployee.getMaNhanVien();

        double soTienTruocKhiGiam = cartItems.stream().mapToDouble(ProductItem::getTotalPrice).sum();
        double soTienGiam = calculateDiscountValue(appliedVoucher, soTienTruocKhiGiam);
        double thanhTienPhaiTra = soTienTruocKhiGiam - soTienGiam;
        if (thanhTienPhaiTra < 0) thanhTienPhaiTra = 0;

        // Kiểm tra tiền khách đưa
        double customerMoney = thanhTienPhaiTra;
        String customerMoneyText = customerMoneyTextField.getText().trim().replace(",", "").replace("đ", "");
        if (!customerMoneyText.isEmpty()) {
            // Nếu nhập tiền → kiểm tra đủ không
            try {
                customerMoney = Double.parseDouble(customerMoneyText);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi nhập liệu",
                        "Vui lòng nhập số tiền khách đưa hợp lệ.");
                return;
            }

            if (customerMoney < thanhTienPhaiTra) {
                showAlert(Alert.AlertType.WARNING, "❌ Không đủ tiền",
                        "Tiền khách đưa không đủ!\n\n" +
                                "Cần thanh toán: " + CURRENCY_FORMAT.format(thanhTienPhaiTra) + "\n" +
                                "Khách đưa: " + CURRENCY_FORMAT.format(customerMoney) + "\n" +
                                "Thiếu: " + CURRENCY_FORMAT.format(customerMoney - thanhTienPhaiTra));

                return;
            }
        }


        int maGiamGiaId = (appliedVoucher != null) ? appliedVoucher.getMaGiamGia() : 0;

        // Tính toán điểm khách hàng thực sự dùng
        int diemSuDungThucTe = 0; // Biến mới để lưu số điểm khách dùng để bù đắp
        if (appliedVoucher != null) {
            // Logic của bạn: "chỉ trừ khách 1000000 để số tiền đạt 15000000 để được giảm giá và giữ nguyên 1000000 điểm còn lại cho khách"
            // Điều này có nghĩa là chúng ta cần biết voucher đó có yêu cầu tổng tiền tối thiểu hay không.
            // Hiện tại MaGiamGia của bạn chưa có trường tongTienToiThieu.
            // Giả định: Nếu voucher là loại Diem, chúng ta dùng DiemCanDoi.
            // Nếu không phải Diem, và bạn muốn dùng điểm để bù đắp tổng tiền,
            // bạn cần thêm trường tongTienToiThieu vào MaGiamGia và logic để tính diemSuDungThucTe dựa trên đó.

            // Hiện tại, chỉ xử lý trường hợp voucher loại Diem:
            if (appliedVoucher.getLoaiGiamGia().equals("Diem")) {
                if (currentCustomerPoints < appliedVoucher.getDiemCanDoi()) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi Thanh Toán", "Không đủ điểm tích lũy để áp dụng mã giảm giá này!");
                    return;
                }
                diemSuDungThucTe = appliedVoucher.getDiemCanDoi();
            }
            // Logic bổ sung để xử lý "bù đắp" tổng tiền sẽ cần thông tin về ngưỡng tổng tiền tối thiểu của voucher
            // và cơ chế quy đổi điểm thành tiền. Tạm thời giữ nguyên logic điểm cho voucher loại Diem.
        }


        // Tạo danh sách các sản phẩm để thanh toán theo cấu trúc XuLyBanHang.Product
        List<XuLyBanHang.Product> productsToCheckout = cartItems.stream()
                .map(item -> new XuLyBanHang.Product(item.getBarcode(), item.getQuantity()))
                .collect(Collectors.toList());

        // Gọi hàm xuLyThanhToan (Bạn sẽ phải thêm tham số diemSuDungThucTe vào hàm này trong XuLyBanHang.java)
        int result = XuLyBanHang.xuLyThanhToan(
                productsToCheckout,
                maGiamGiaId,
                customerPhoneTextField.getText().trim().isEmpty() ? null : customerPhoneTextField.getText().trim(),
                maNhanVien,
                soTienTruocKhiGiam,
                thanhTienPhaiTra,
                diemSuDungThucTe
        );

        if (result != -1) {
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Thanh toán thành công! Mã hóa đơn: " + result + "\nThành tiền: " + CURRENCY_FORMAT.format(currentFinalAmount));
            clearAllFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Thanh toán thất bại. Vui lòng kiểm tra lại thông tin và thử lại.");
        }
    }

    @FXML
    private void handleRemoveProduct() {
        ProductItem selectedItem = productTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            cartItems.remove(selectedItem);
            calculateTotals();
            updateVoucherComboBoxAndDisplay(true); // Cập nhật voucher khi xóa sản phẩm
        } else {
            showAlert(Alert.AlertType.WARNING, "Lỗi", "Vui lòng chọn sản phẩm muốn xóa khỏi giỏ hàng.");
        }
    }
    @FXML
    private void quayVeTrangChu(ActionEvent event) {
        try {
            Scene currentScene = ((Node) event.getSource()).getScene();
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/cnpm_nhom17/main-view.fxml"));
            currentScene.setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearAllFields() {
        cartItems.clear();
        barcodeTextField.clear();
        voucherDisplayTextField.setText("Chọn mã giảm giá");
        allVouchersComboBox.setItems(FXCollections.emptyObservableList());
        allVouchersComboBox.getSelectionModel().clearSelection();
        customerPhoneTextField.clear();
        customerPointsLabel.setText("Điểm tích lũy: 0 điểm");
        customerMoneyTextField.clear(); // Xóa tiền khách đưa
        changeAmountLabel.setText("0đ"); // Reset tiền thối
        appliedVoucher = null;
        currentCustomerPoints = 0;
        currentCustomerName = ""; // Reset tên khách hàng
        currentPotentialPoints = 0; // Reset điểm tiềm năng
        calculateTotals();
        employeeComboBox.getSelectionModel().selectFirst();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class ProductItem {
        // ... (Không thay đổi) ...
        private final SimpleStringProperty barcode;
        private final SimpleStringProperty productName;
        private final SimpleDoubleProperty unitPrice;
        private final SimpleIntegerProperty quantity;
        private final SimpleDoubleProperty totalPrice;
        private final int maChiTietSanPham;
        private final double giaVonHienTai;
        private final int soLuongTon;

        public ProductItem(ScannedProductInfo scannedProduct, int quantity) {
            this.barcode = new SimpleStringProperty(scannedProduct.getBarcode());
            this.productName = new SimpleStringProperty(
                    scannedProduct.getTenSanPham() + " (" + scannedProduct.getMauSac() + " - " + scannedProduct.getSize() + ")"
            );
            this.unitPrice = new SimpleDoubleProperty(scannedProduct.getGiaBanHienTai());
            this.quantity = new SimpleIntegerProperty(quantity);
            this.totalPrice = new SimpleDoubleProperty(scannedProduct.getGiaBanHienTai() * quantity);
            this.maChiTietSanPham = scannedProduct.getMaChiTietSanPham();
            this.giaVonHienTai = scannedProduct.getGiaVonHienTai();
            this.soLuongTon = scannedProduct.getSoLuongTon();

            this.quantity.addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    this.totalPrice.set(this.unitPrice.get() * newVal.intValue());
                } else {
                    this.totalPrice.set(0.0);
                }
            });
        }

        public String getBarcode() { return barcode.get(); }
        public SimpleStringProperty barcodeProperty() { return barcode; }
        public String getProductName() { return productName.get(); }
        public SimpleStringProperty productNameProperty() { return productName; }
        public double getUnitPrice() { return unitPrice.get(); }
        public SimpleDoubleProperty unitPriceProperty() { return unitPrice; }
        public int getQuantity() { return quantity.get(); }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        public void setQuantity(int quantity) { this.quantity.set(quantity); }
        public double getTotalPrice() { return totalPrice.get(); }
        public SimpleDoubleProperty totalPriceProperty() { return totalPrice; }

        public int getMaChiTietSanPham() { return maChiTietSanPham; }
        public double getGiaVonHienTai() { return giaVonHienTai; }
        public int getSoLuongTon() { return soLuongTon; }
    }
}