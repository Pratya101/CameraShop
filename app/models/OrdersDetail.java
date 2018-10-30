package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.avaje.ebean.Expr.eq;

/**
 * Created by Coffee on 20/10/2561.
 */
@Entity
@Table(name = "tbOrdersDetail")
public class OrdersDetail extends Model {
    @Id
    private String id;
    @ManyToOne
    private Orders orders;
    @ManyToOne
    private Products products;
    private int amount;

    public OrdersDetail() {
        setId();
    }

    public OrdersDetail( Orders orders, Products products, int amount) {
        setId();
        this.orders = orders;
        this.products = products;
        this.amount = amount;

    }

    public String getId() {
        return id;
    }

    public void setId() {
        int i ;
        Random random = new Random();
        i = random.nextInt(100000)+1;
        id = "CS-" + Integer.toString(i);
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
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

    public static Finder<String,OrdersDetail> finder = new Finder<String, OrdersDetail>(String.class,OrdersDetail.class);
    public static List<OrdersDetail> ordersDetailList (){
        return finder.all();
    }
    public static void insert (OrdersDetail ordersDetail){
        ordersDetail.save();
    }
    public static void update (OrdersDetail ordersDetail){
        ordersDetail.update();
    }
    public static void  delete (OrdersDetail ordersDetail){
        ordersDetail.delete();
    }


}
