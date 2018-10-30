package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coffee on 1/10/2561.
 */
@Entity
@Table(name = "tbbrands")
public class Brands extends Model {
    @Id
    private String id;
    private String name;

    @OneToMany(mappedBy = "brands" ,cascade = CascadeType.ALL)
    private List<Products> productsList = new ArrayList<Products>();

    public Brands() {
    }

    public Brands(String id, String name, List<Products> productsList) {
        this.id = id;
        this.name = name;
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

    public List<Products> getProductsList() {
        return productsList;
    }

    public static Finder<String, Brands> finder = new Finder(String.class, Brands.class);

    public static List<Brands> brandList(){
        return finder.all();
    }
    public static void insert(Brands brand){
        brand.save();
    }
    public static void update (Brands brand){
        brand.update();
    }
    public  static void delete (Brands brand){
        brand.delete();
    }
}
