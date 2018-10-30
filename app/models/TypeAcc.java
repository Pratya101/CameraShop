package models;

import play.db.ebean.Model;
import scala.util.parsing.combinator.testing.Str;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coffee on 4/10/2561.
 */
@Entity
@Table(name = "tbTypeAcc")
public class TypeAcc extends Model {
    @Id
    private String id;
    private String name;

    @OneToMany(mappedBy = "typeAcc" , cascade = CascadeType.ALL)
    private List<Accessory> accessoryList = new ArrayList<>();

    public TypeAcc() {
    }

    public TypeAcc(String id, String name) {
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

    public List<Accessory> getAccessoryList() {
        return accessoryList;
    }

    public static Finder<String,TypeAcc> finder = new Finder(String.class,TypeAcc.class);
    public static List<TypeAcc> typeAccList(){
        return finder.all();
    }
    public static void insert (TypeAcc typeAcc){
        typeAcc.save();
    }
    public static void update (TypeAcc typeAcc){
        typeAcc.update();
    }
    public static  void delete (TypeAcc typeAcc){
        typeAcc.delete();
    }
}
