package models;

import com.avaje.ebean.Expr;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coffee on 30/9/2561.
 */
@Entity
@Table(name = "tbuser")
public class Users extends Model{
    @Id
    private String username;
    private String password,name,sername,address,tel,status;

    @OneToMany(mappedBy = "users")
    private List<Orders> ordersList = new ArrayList<Orders>();
    public List<Orders> getOrdersList(){
        return ordersList;
    }

    public Users() {

    }

    public Users( String username, String password, String name, String sername, String address, String tel, String status) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.sername = sername;
        this.address = address;
        this.tel = tel;
        this.status = status;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSername() {
        return sername;
    }

    public void setSername(String sername) {
        this.sername = sername;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public static Finder<String, Users> finder = new Finder<String, Users>(String.class, Users.class);

    public static List<Users> usersList(){
        return finder.all();
    }

    public static void insert (Users users){
        users.save();
    }
    public  static void update (Users users){
        users.update();
    }
    public static  void delete (Users users){
        users.delete();
    }

    public  static Users userlogin (String usr, String pwd){
        return finder.where().and(Expr.eq("username",usr),Expr.eq("password",pwd)).findUnique();
    }
}


