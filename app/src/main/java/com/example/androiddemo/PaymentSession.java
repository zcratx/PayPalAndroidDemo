package com.example.androiddemo;

public class PaymentSession {

    String nonce;
    String deviceData;
    String amount;
    String orderId;
    public PaymentSession(String nonce, String deviceData, String amount, String orderId) {

        this.nonce = nonce;
        this.deviceData = deviceData;
        this.amount = amount;
        this.orderId = orderId;
    }

    public String getNonce() {
        return nonce;
    }

    public String getDeviceData() {
        return deviceData;
    }

    public String getAmount() {
        return amount;
    }

    public String getOrderId() {
        return orderId;
    }
}
