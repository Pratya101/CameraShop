package models;

import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coffee on 30/9/2561.
 */
@Entity
@Table(name = "tbtypes")
public class Types extends Model{
    @Id
    private  String id;
    private String name;
    @OneToMany(mappedBy = "types",cascade = CascadeType.ALL)
    private List<Products> productsList = new ArrayList<Products>();

    public Types() {
    }

    public Types(String id, String name) {
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

    public void setProductsList(List<Products> productsList) {
        this.productsList = productsList;
    }

    public static Finder<String, Types> finder = new Finder(String.class, Types.class);

    public static List<Types> typesList(){
        return finder.all();
    }
    public static void insert(Types types){
        types.save();
    }
    public static void update (Types types){
        types.update();
    }
    public  static void delete (Types types){
        types.delete();
    }
}
