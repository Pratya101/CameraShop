package models;

/**
 * Created by Coffee on 20/10/2561.
 */
public class Basket {
    private Products products;
    private int amount;

    public Basket() {
    }

    public Basket(Products products, int amount) {
        this.products = products;
        this.amount = amount;
    }

    public Products getProducts() {
        return products;
    }

    public void setProducts(Products products) {
        this.products = products;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
