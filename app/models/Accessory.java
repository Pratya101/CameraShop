package models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;

/**
 * Created by Coffee on 4/10/2561.
 */
@Entity
@Table(name = "tbAccessory")
public class Accessory extends Model {
    @Id
    private String id;
    private String name,detail,pic;
    double price;
    int amount;

    @ManyToOne
    private TypeAcc typeAcc;

    public Accessory() {
    }

    public Accessory(String id, String name, String detail,String pic, double price, int amount, TypeAcc typeAcc) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.pic = pic;
        this.price = price;
        this.amount = amount;
        this.typeAcc = typeAcc;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public TypeAcc getTypeAcc() {
        return typeAcc;
    }

    public void setTypeAcc(TypeAcc typeAcc) {
        this.typeAcc = typeAcc;
    }

    public static Finder<String,Accessory> finder = new Finder(String.class,Accessory.class);
    public static List<Accessory> accessoryList (){
        return  finder.all();
    }
    public static void insert(Accessory accessory){
        accessory.save();
    }
    public static void update(Accessory accessory){
        accessory.update();
    }
    public static void delete (Accessory accessory){
        accessory.delete();
    }
}
