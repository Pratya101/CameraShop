package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coffee on 30/9/2561.
 */
@Entity
@Table(name = "tbproducts")
public class Products extends Model {
    @Id
    private String id;
    private String name, detail, generation,pic;
    private double price;
    private int amount;

    @ManyToOne
    private Brands brands;

    @ManyToOne
    private Types types;

    @OneToMany(mappedBy = "products")
    private List<OrdersDetail> ordersDetailList = new ArrayList<OrdersDetail>();
    public List<OrdersDetail> getOrdersDetailList(){
        return ordersDetailList;
    }
    public Products() {
    }

    public Products(String id, String name, String detail, String generation, String pic, double price, int amount, Brands brands, Types types) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.generation = generation;
        this.pic = pic;
        this.price = price;
        this.amount = amount;
        this.brands = brands;
        this.types = types;

    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getGeneration() {
        return generation;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Brands getBrands() {
        return brands;
    }

    public void setBrands(Brands brands) {
        this.brands = brands;
    }

    public Types getTypes() {
        return types;
    }

    public void setTypes(Types types) {
        this.types = types;
    }

    public static Finder<String, Products> finder = new Finder(String.class, Products.class);

    public static List<Products> productsList(){
        return finder.all();
    }
    public static void insert(Products products){
        products.save();
    }
    public static void update (Products products){
        products.update();
    }
    public  static void delete (Products products){
        products.delete();
    }

    public static List<Products> findByBrand (String brand){
        return finder.where().eq("brands.id",brand).findList();
    }


    public static List<Products> cannons(){
        return finder.where().eq("brands.id","B001").findList();
    }
    public static List<Products> nikons(){
        return finder.where().eq("brands.id","B002").findList();
    }
    public static List<Products> sonys(){
        return finder.where().eq("brands.id","B003").findList();
    }
    public static List<Products> olympus(){
        return finder.where().eq("brands.id","B004").findList();
    }
    public static List<Products> fujiflim(){
        return finder.where().eq("brands.id","B005").findList();
    }
}

