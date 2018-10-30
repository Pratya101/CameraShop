package models;

import com.avaje.ebean.Expr;
import net.sf.ehcache.search.expression.Or;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by Coffee on 20/10/2561.
 */
@Entity
@Table(name = "tbOrders")
public class Orders extends Model {
    @Id
    private String id;
    private Date date;
    @ManyToOne
    private Users  users;
    private String status;

    @OneToMany(mappedBy = "orders")
    private List<OrdersDetail> ordersDetailList = new ArrayList<OrdersDetail>();
    public  List<OrdersDetail> getOrdersDetailList(){
        return ordersDetailList;
    }

    public Orders() {
        setId();
    }

    public Orders(String id, Date date, Users users, String status) {
        if (id != null){
            reciveid(id);
        }else{
            setId();
        }
        this.date = date;
        this.users = users;
        this.status = status;

    }

    public String getId() {
        return id;
    }

    public void setId() {
        int i ;
        Random random = new Random();
        i = random.nextInt(100000)+1;
        id = "CS-"+Integer.toString(i);
    }

    public void reciveid (String id){
        this.id=id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Finder<String, Orders> finder = new Finder<String, Orders>(String.class,Orders.class);
    public static List<Orders> ordersList (){
        return finder.all();
    }
    public static void insert (Orders orders){
        orders.save();
    }
    public static void update (Orders orders){
        orders.update();
    }
    public static void delete (Orders orders){
        orders.delete();
    }

    public static List<Orders> NoPayList (String users){
        return finder.where().eq("users.username",users).findList();
    }

    public static List<Orders> payList (){
        return finder.where().ne("status","").findList();
    }

}
