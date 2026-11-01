package org.example.cnpm_nhom17.database_helper;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class QuantitySpinner extends HBox {

    @FXML
    private Button minusButton;
    @FXML
    private TextField quantityTextField;
    @FXML
    private Button plusButton;

    private IntegerProperty quantity = new SimpleIntegerProperty(1);

    public QuantitySpinner() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("quantity-spinner.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        // Bind TextField to quantity property
        quantityTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantityTextField.setText(oldVal);
            } else {
                int newQuantity = Integer.parseInt(newVal.isEmpty() ? "0" : newVal);
                if (newQuantity < 1) {
                    quantity.set(1);
                    quantityTextField.setText("1");
                } else {
                    quantity.set(newQuantity);
                }
            }
        });

        // Initialize TextField with quantity
        quantityTextField.setText(String.valueOf(quantity.get()));

        minusButton.setOnAction(event -> decrement());
        plusButton.setOnAction(event -> increment());
    }

    public void increment() {
        quantity.set(quantity.get() + 1);
        quantityTextField.setText(String.valueOf(quantity.get()));
    }

    public void decrement() {
        if (quantity.get() > 1) {
            quantity.set(quantity.get() - 1);
            quantityTextField.setText(String.valueOf(quantity.get()));
        }
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
        this.quantityTextField.setText(String.valueOf(quantity));
    }
}
