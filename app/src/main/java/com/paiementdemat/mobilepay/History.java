package com.paiementdemat.mobilepay;

import java.util.Date;

public class History {
    public String shop;
    public String amount;
    public String createdAt;

    History(String shop, String amount, String createdAt){
        this.shop = shop;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
