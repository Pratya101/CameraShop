package controllers;

import models.*;
import net.sf.ehcache.search.expression.Or;
import org.h2.engine.Session;
import play.Play;
import play.api.templates.Html;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.*;
import play.cache.Cache;
import views.html.*;

import javax.persistence.criteria.From;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Application extends Controller {
    public static List<Brands> brandss = new ArrayList<Brands>();

    public static Result showmain(Html content) {
        brandList = Brands.brandList();
        typeAccList = TypeAcc.typeAccList();
        return ok(main.render(content, brandList, typeAccList));
    }

    public static Result showmainadmin(Html content) {
        return ok(mainAdmin.render(content));
    }

    public static Result indexAdmin(){
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            flash("NotAddminError", "คุณไม่มีสิทธิ์เข้าใช้งานในส่วนนี้ครับ !");
            return showmain(NoAdmin.render());
        }else{
            return showmainadmin(indexAdmin.render());
        }
    }
    public static Result index() {
        productsList = Products.productsList();
        return showmain(index.render(productsList));
    }

    public static Result register() {
        return showmain(register.render());
    }

    public static Result editSender() {
        Users datauesr = Users.finder.byId(session("usr"));
        usersForm = Form.form(Users.class).fill(datauesr);
        return showmain(registerHelper.render(usersForm));
    }

    public static Result updateSender() {
        Form<Users> newuser = usersForm.bindFromRequest();
        if (newuser.hasErrors()) {
            flash("Error", "ข้อมูลไม่สมบูรณ์");
            return showmain(registerHelper.render(newuser));
        } else {
            users = newuser.get();
            users.setUsername(session("usr"));
            users.setPassword(session("password"));
            users.setStatus("user");
            Users.update(users);
            Users datausers = Users.finder.byId(session("usr"));
            List<Basket> basketList = new ArrayList<Basket>();
            if (Cache.get("basketList") != null) {
                basketList = (List<Basket>) Cache.get("basketList");
            }
            return showmain(checkBill.render(basketList, datausers));
        }
    }

    public static Result postRegister() {
        DynamicForm newuser = Form.form().bindFromRequest();
        String username = newuser.get("username");
        String password = newuser.get("password");
        String Conpassword = newuser.get("password_confirmation");
        String name = newuser.get("name");
        String sername = newuser.get("sername");
        String address = newuser.get("address");
        String tel = newuser.get("tel");
        String status = "user";
        Users newdatauser = new Users(username, password, name, sername, address, tel, status);

        if (!password.equals(Conpassword)) {
            flash("msgError", "Password ไม่ตรงกัน กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return register();
        } else {
            Users check;
            check = Users.finder.byId(newdatauser.getUsername());
            if (check != null) {
                flash("msgError", "Username ซ้ำ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return register();
            } else {
                Users.insert(newdatauser);
                return showmain(RegisterOK.render());
            }
        }
    }

    public static Result loginForm() {
        return showmain(login.render());
    }

    public static Result loginFormforBuy() {
        flash("NotUser", "กรุณาเข้าสู้ระบบก่อนเพื่อทำการเลือกซื้อสินค้า");
        return showmain(login.render());
    }

    public static Result submitLogin() {
        String usr = Form.form().bindFromRequest().get("usr");
        String pwd = Form.form().bindFromRequest().get("pwd");
        Users logged = Users.userlogin(usr, pwd);
        if (logged != null) {
            users = logged;
            String status1 = logged.getStatus();
            session("usr", logged.getUsername());
            session("name", logged.getName());
            session("password", logged.getPassword());
            session("sername", logged.getSername());
            session("status", logged.getStatus());
            if (status1.equals("admin")) {
                return showmainadmin(indexAdmin.render());
            } else {
                productsList = Products.productsList();
                return showmain(index.render(productsList));
            }
        } else {
            flash("err", "Username หรือ Password ไม่ถูกต้อง กรุณาตรวจสอยหใหม่");
            return loginForm();
        }
    }

    public static Result logOut() {
        session().clear();
        return loginForm();
    }

    //Products------------------------------------------------------------------------
    public static Form<Products> productsForm = Form.form(Products.class);
    public static List<Products> productsList = new ArrayList<Products>();
    public static Products products;
    public static List<Types> typesList = new ArrayList<Types>();
    public static List<Brands> brandList = new ArrayList<Brands>();
    public static String camPicPath = Play.application().configuration().getString("cam_pic_path");

    public static Result listProducts() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            flash("NotAddminError", "คุณไม่มีสิทธิ์เข้าใช้งานในส่วนนี้ครับ !");
            return showmain(NoAdmin.render());
        }
        productsList = Products.productsList();
        return showmainadmin(listProductAll.render(productsList));
    }


    public static Result fromAddProducts() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typesList = Types.typesList();
        brandList = Brands.brandList();
        productsForm = Form.form(Products.class);
        return showmainadmin(addproduct.render(brandList, typesList, productsForm));
    }

    public static Result insertProduct() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("pic");
        String fileName, contentType;
        typesList = Types.typesList();
        brandList = Brands.brandList();
        Form<Products> newProduct = productsForm.bindFromRequest();
        if (newProduct.hasErrors()) {
            flash("msgError", "คุณป้อมข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(addproduct.render(brandList, typesList, newProduct));
        } else {
            products = newProduct.get();
            Products check;
            check = Products.finder.byId(products.getId());
            if (check != null) {
                flash("msgError", "รหัสสิ้นค้าซ้ำกับของเดิมที่มีอยู่ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmainadmin(addproduct.render(brandList, typesList, newProduct));
            } else {
                if (picture != null) {
                    contentType = picture.getContentType();
                    File file = picture.getFile();
                    fileName = picture.getFilename();
                    if (!contentType.startsWith("image")) {
                        flash("msgError", "นามสกุลภาพไม่ตรงตามชนิด กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                        return showmainadmin(addproduct.render(brandList, typesList, productsForm));
                    }
                    fileName = products.getId() + fileName.substring(fileName.lastIndexOf("."));
                    file.renameTo(new File(camPicPath, fileName));
                    products.setPic(fileName);
                    Products.insert(products);
                }
                return listProducts();
            }
        }
    }

    public static Result editProducts(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        products = Products.finder.byId(id);
        if (products == null) {
            flash("msgError", "อย่าเเสดงมาเเก้ URL!");
            return listProducts();
        } else {
            typesList = Types.typesList();
            brandList = Brands.brandList();
            productsForm = Form.form(Products.class).fill(products);
            return showmainadmin(views.html.editProducts.render(productsForm, typesList, brandList));
        }
    }

    public static Result updateProduct() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart myfile = body.getFile("pic");
        typesList = Types.typesList();
        brandList = Brands.brandList();
        Form<Products> dataupdate = productsForm.bindFromRequest();
        if (dataupdate.hasErrors()) {
            flash("msgError", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(views.html.editProducts.render(productsForm, typesList, brandList));
        } else {
            products = dataupdate.get();
            if (myfile != null) {
                String fileName = myfile.getFilename();
                String extension = fileName.substring(fileName.indexOf("."));
                String realName = products.getId() + extension;
                File file = myfile.getFile();
                File temp = new File("public/images/cscam/" + realName);
                if (temp.exists()) {
                    temp.delete();
                }
                file.renameTo(new File("public/images/cscam/" + realName));
                products.setPic(realName);
            }
            Products.update(products);
            return listProducts();
        }
    }

    public static Result deleteProducts(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        products = Products.finder.byId(id);
        if (products != null) {
            File temp = new File("public/images/cscam/" + products.getPic());
            temp.delete();
            Products.delete(products);
        }
        return listProducts();
    }

    public static Result detailProduct(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        int i;
        for (i = 0; i < productsList.size(); i++) {
            if (productsList.get(i).getId().equals(id)) {
                break;
            }
        }
        return showmainadmin(detailAdmin.render(productsList.get(i)));
    }

    public static Result detailProductuser(String id) {
        int i;
        for (i = 0; i < productsList.size(); i++) {
            if (productsList.get(i).getId().equals(id)) {
                break;
            }
        }
        return showmain(detailUser.render(productsList.get(i)));
    }

    //Types------------------------------------------------------------------------
    public static Form<Types> typesForm = Form.form(Types.class);
    public static Types types;

    public static Result listTypes() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typesList = Types.typesList();
        return showmainadmin(listTypes.render(typesList));
    }

    public static Result formAddTypes() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typesForm = Form.form(Types.class);
        return showmainadmin(addtypes.render(typesForm));
    }

    public static Result insertTypes() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<Types> newTypes = typesForm.bindFromRequest();
        if (newTypes.hasErrors()) {
            flash("msgError", "คุณป้อมข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(addtypes.render(typesForm));
        } else {
            types = newTypes.get();
            Types check;
            check = Types.finder.byId(types.getId());
            if (check != null) {
                flash("msgError", "รหัสสิ้นค้าซ้ำกับของเดิมที่มีอยู่ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmainadmin(addtypes.render(typesForm));
            } else {
                Types.insert(types);
                return listTypes();
            }
        }
    }

    public static Result editTypes(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        types = Types.finder.byId(id);
        if (types == null) {
            flash("msgError", "อย่าเเสดงมาแก้ URL!");
            return listTypes();
        } else {
            typesForm = Form.form(Types.class).fill(types);
            return showmainadmin(editTypes.render(typesForm));
        }
    }

    public static Result updateType() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<Types> dataform = typesForm.bindFromRequest();
        if (dataform.hasErrors()) {
            flash("msgError", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(editTypes.render(dataform));
        } else {
            types = dataform.get();
            Types.update(types);
            return listTypes();
        }
    }

    public static Result deleteTypes(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        types = Types.finder.byId(id);
        if (types != null) {
            Types.delete(types);
        }
        return listTypes();
    }

    //Brands------------------------------------------------------------------------
    public static Form<Brands> brandsForm = Form.form(Brands.class);
    public static Brands brands;

    public static Result listBrands() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        brandList = Brands.brandList();
        return showmainadmin(listBrands.render(brandList));
    }

    public static Result formAddBrands() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        brandsForm = Form.form(Brands.class);
        return showmainadmin(addbrand.render(brandsForm));
    }

    public static Result insertBrands() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<Brands> newBrands = brandsForm.bindFromRequest();
        if (newBrands.hasErrors()) {
            flash("msgError", "คุณป้อมข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(addbrand.render(newBrands));
        } else {
            brands = newBrands.get();
            Brands check;
            check = Brands.finder.byId(brands.getId());
            if (check != null) {
                flash("msgError", "รหัสสิ้นค้าซ้ำกับของเดิมที่มีอยู่ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmainadmin(addbrand.render(newBrands));
            } else {
                Brands.insert(brands);
                return listBrands();
            }
        }
    }

    public static Result editBrands(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        brands = Brands.finder.byId(id);
        if (brands == null) {
            flash("msgError", "อย่าเเสดงมาแก้ URL!");
            return listBrands();
        } else {
            brandsForm = Form.form(Brands.class).fill(brands);
            return showmainadmin(editbrand.render(brandsForm));
        }
    }

    public static Result updateBrands() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<Brands> dataform = brandsForm.bindFromRequest();
        if (dataform.hasErrors()) {
            flash("msgError", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(editbrand.render(dataform));
        } else {
            brands = dataform.get();
            Brands.update(brands);
            return listBrands();
        }
    }

    public static Result deleteBrands(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        brands = Brands.finder.byId(id);
        if (brands != null) {
            Brands.delete(brands);
        }
        return listBrands();
    }

    //Select Where Types-------------------------------------------------------------
    //Cannon
    public static Result brandPage(String id) {
        brands = Brands.finder.byId(id);
        if (brands != null) {
            List<Products> databrand = brands.getProductsList();
            return showmain(index.render(databrand));
        } else {
            return ok();
        }
    }

    //TypeAcc------------------------------------------------------------------
    public static List<TypeAcc> typeAccList = new ArrayList<TypeAcc>();
    public static Form<TypeAcc> typeAccForm = Form.form(TypeAcc.class);
    public static TypeAcc typeAcc;

    public static Result listTypeAcc() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typeAccList = TypeAcc.typeAccList();
        return showmainadmin(listTypeAcc.render(typeAccList));
    }

    public static Result formAddTypeAcc() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typeAccForm = Form.form(TypeAcc.class);
        return showmainadmin(addTypeAcc.render(typeAccForm));
    }

    public static Result insertTypeAcc() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<TypeAcc> newTypeAcc = typeAccForm.bindFromRequest();
        if (newTypeAcc.hasErrors()) {
            flash("msgError", "คุณป้อมข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(addTypeAcc.render(newTypeAcc));
        } else {
            typeAcc = newTypeAcc.get();
            TypeAcc check;
            check = TypeAcc.finder.byId(typeAcc.getId());
            if (check != null) {
                flash("msgError", "รหัสสิ้นค้าซ้ำกับของเดิมที่มีอยู่ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmainadmin(addTypeAcc.render(newTypeAcc));
            } else {
                TypeAcc.insert(typeAcc);
                return listTypeAcc();
            }
        }
    }

    public static Result editTypeAcc(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typeAcc = TypeAcc.finder.byId(id);
        if (typeAcc == null) {
            flash("msgError", "อย่าเเสดงมาแก้ URL!");
            return listTypeAcc();
        } else {
            typeAccForm = Form.form(TypeAcc.class).fill(typeAcc);
            return showmainadmin(editTypeAcc.render(typeAccForm));
        }
    }

    public static Result updateTypeAcc() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Form<TypeAcc> dataform = typeAccForm.bindFromRequest();
        if (dataform.hasErrors()) {
            flash("msgError", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(editTypeAcc.render(dataform));
        } else {
            typeAcc = dataform.get();
            TypeAcc.update(typeAcc);
            return listTypeAcc();
        }
    }

    public static Result deleteTypeAcc(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        typeAcc = TypeAcc.finder.byId(id);
        if (typeAcc != null) {
            TypeAcc.delete(typeAcc);
        }
        return listTypeAcc();
    }

    //TypeAccSideBar
    public static Result typeAccPage(String id) {
        typeAcc = TypeAcc.finder.byId(id);
        if (typeAcc != null) {
            List<Accessory> datatypeacc = typeAcc.getAccessoryList();
            return showmain(accessoryPage.render(datatypeacc));
        } else {
            return ok();
        }
    }


    //Accessory------------------------------------------------------------------
    public static List<Accessory> accessoryList = new ArrayList<Accessory>();
    public static Form<Accessory> accessoryForm = Form.form(Accessory.class);
    public static Accessory accessory;
    public static String AccPicPath = Play.application().configuration().getString("acc_pic_path");

    public static Result listAccessory() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        accessoryList = Accessory.accessoryList();
        return showmainadmin(listAccessory.render(accessoryList));
    }

    public static Result addAccessory() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        List<TypeAcc> typeAccList1 = TypeAcc.typeAccList();
        accessoryForm = Form.form(Accessory.class);
        return showmainadmin(addAccessory.render(accessoryForm, typeAccList1));
    }

    public static Result insertAccessory() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("pic");
        String fileName, contentType;
        List<TypeAcc> typeAccList1 = TypeAcc.typeAccList();
        Form<Accessory> newAcc = accessoryForm.bindFromRequest();
        if (newAcc.hasErrors()) {
            flash("msgError", "คุณป้อมข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(addAccessory.render(newAcc, typeAccList1));
        } else {
            accessory = newAcc.get();
            Accessory check;
            check = Accessory.finder.byId(accessory.getId());
            if (check != null) {
                flash("msgError", "รหัสสิ้นค้าซ้ำกับของเดิมที่มีอยู่ กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmainadmin(addAccessory.render(newAcc, typeAccList1));
            } else {
                if (picture != null) {
                    contentType = picture.getContentType();
                    File file = picture.getFile();
                    fileName = picture.getFilename();
                    if (!contentType.startsWith("image")) {
                        flash("msgError", "นามสกุลภาพไม่ตรงตามชนิด กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                        return showmainadmin(addAccessory.render(newAcc, typeAccList1));
                    }
                    fileName = accessory.getId() + fileName.substring(fileName.lastIndexOf("."));
                    file.renameTo(new File(AccPicPath, fileName));
                    accessory.setPic(fileName);
                    Accessory.insert(accessory);
                }
                return listAccessory();
            }
        }
    }

    public static Result editAccessory(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        accessory = Accessory.finder.byId(id);
        if (accessory == null) {
            flash("msgError", "อย่าเเสดงมาเเก้ URL!");
            return listAccessory();
        } else {
            typeAccList = TypeAcc.typeAccList();
            accessoryForm = Form.form(Accessory.class).fill(accessory);
            return showmainadmin(editAccessory.render(accessoryForm, typeAccList));
        }
    }

    public static Result updateAccessory() {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart myfile = body.getFile("pic");
        accessoryList = Accessory.accessoryList();
        Form<Accessory> dataupdate = accessoryForm.bindFromRequest();
        if (dataupdate.hasErrors()) {
            flash("msgError", "ข้อมูลไม่ถูกต้อง กรุณาตรวจสอบใหม่ด่วยค่ะ!");
            return showmainadmin(editAccessory.render(accessoryForm, typeAccList));
        } else {
            accessory = dataupdate.get();
            if (myfile != null) {
                String fileName = myfile.getFilename();
                String extension = fileName.substring(fileName.indexOf("."));
                String realName = accessory.getId() + extension;
                File file = myfile.getFile();
                File temp = new File("public/images/csacc/" + realName);
                if (temp.exists()) {
                    temp.delete();
                }
                file.renameTo(new File("public/images/csacc/" + realName));
                accessory.setPic(realName);
            }
            Accessory.update(accessory);
            return listAccessory();
        }
    }

    public static Result deleteAccessory(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        accessory = Accessory.finder.byId(id);
        if (accessory != null) {
            Accessory.delete(accessory);
            File temp = new File("public/images/csacc/" + products.getPic());
            temp.delete();
        }
        return listAccessory();
    }

    public static Result detailAccessory(String id) {
        if (session().get("status") == null) {
            return loginForm();
        } else if (!session().get("status").equals("admin")) {
            return showmain(NoAdmin.render());
        }
        int i;
        for (i = 0; i < accessoryList.size(); i++) {
            if (accessoryList.get(i).getId().equals(id)) {
                break;
            }
        }
        return showmainadmin(detailAccessory.render(accessoryList.get(i)));
    }

    //#sell----------------------------------------------------


    //Users------------------------------------------------------------
    public static Users users;
    public static List<Users> usersList = new ArrayList<Users>();
    public static Form<Users> usersForm = Form.form(Users.class);

    public static Result listuser() {
        usersList = Users.usersList();
        return showmainadmin(listUsers.render(usersList));
    }


    //Basket-------------------------------------------------------------

    public  List<OrdersDetail> basketList = new ArrayList<OrdersDetail>();

    public static Result basketSell() {
        String   myBasket = "basketList" + session("usr");
        if (session().get("status") == null) {
            flash("NotUser", "กรุณาเข้าสู้ระบบก่อนเพื่อดูสินค้าในตระกร้า");
            return loginForm();
        } else if (!session().get("status").equals("user")) {
            flash("NotAddminError", "คุณไม่มีสิทธิ์เข้าใช้งานในส่วนนี้ครับ !");
            return showmain(NoAdmin.render());
        } else {
            List<Basket> basketList = (List<Basket>) Cache.get(myBasket);
            return showmain(views.html.basketList.render(basketList));
        }
    }


    public static Result removeItem(String id) {
        String   myBasket = "basketList" + session("usr");
        List<Basket> basketList = new ArrayList<Basket>();
        if (Cache.get(myBasket) != null) {
            basketList.addAll((List<Basket>) Cache.get(myBasket));
            for (int i = 0; i < basketList.size(); i++) {
                if (basketList.get(i).getProducts().getId().equals(id)) {
                    basketList.remove(i);
                    break;
                }
            }
        }
        Cache.set(myBasket, basketList);
        return redirect("/basketSell");
    }

    public static Result addOrder(String id) {
        String   myBasket = "basketList" + session("usr");
        List<Basket> basketList = new ArrayList<Basket>();
        boolean found = false;
        if (Cache.get(myBasket) != null) {
            basketList.addAll((List<Basket>) Cache.get(myBasket));
            for (int i = 0; i < basketList.size(); i++) {
                if (basketList.get(i).getProducts().getId().equals(id)) {
                    int amount = basketList.get(i).getAmount();
                    basketList.get(i).setAmount(amount + 1);
                    found = true;
                    break;
                }
            }
        }
        if (found == false) {
            products = Products.finder.byId(id);
            basketList.add(new Basket(products, 1));
        }
        Cache.set(myBasket, basketList);
        return redirect("/basketSell");
        //return ok(showInfo.render(basketList));
    }

    public static Result checkBill() {
        String   myBasket = "basketList" + session("usr");
        users = Users.finder.byId(session("usr"));
        List<Basket> basketList = new ArrayList<Basket>();
        if (Cache.get(myBasket) != null) {
            basketList = (List<Basket>) Cache.get(myBasket);
        }
        return showmain(checkBill.render(basketList, users));
    }

    public static Result saveBill() {
        String   myBasket = "basketList" + session("usr");
        List<Basket> basketList = new ArrayList<Basket>();
        if (Cache.get(myBasket) != null) {
            Orders orders = new Orders();
            Users users = Users.finder.byId(session().get("usr"));
            orders.setDate(new Date());
            orders.setUsers(users);
            orders.setStatus("");
            Orders.insert(orders);
            basketList = (List<Basket>) Cache.get(myBasket);
            for (int i = 0; i < basketList.size(); i++) {
                OrdersDetail ordersDetail = new OrdersDetail();
                ordersDetail.setOrders(orders);
                ordersDetail.setProducts(basketList.get(i).getProducts());
                ordersDetail.setAmount(basketList.get(i).getAmount());
                OrdersDetail.insert(ordersDetail);
            }
        }
        flash("saveBill", "ทำการสั่งสินค้าเรียบร้อยเเล้วครับ");
        Cache.remove(myBasket);
        List<Orders> NoPay = Orders.NoPayList(session("usr"));
        return showmain(listPay.render(NoPay));
    }

    public static Result statusProduct() {
        if (session().get("status") == null) {
            flash("NotUser", "กรุณาเข้าสู้ระบบก่อนเพื่อดูสถานะการสั่งซื้อ");
            return loginForm();
        } else if (!session().get("status").equals("user")) {
            flash("NotAddminError", "คุณไม่มีสิทธิ์เข้าใช้งานในส่วนนี้ครับ !");
            return showmain(NoAdmin.render());
        } else {
            List<Orders> NoPay = Orders.NoPayList(session("usr"));
            return showmain(listPay.render(NoPay));
        }
    }

    public static List<Orders> ordersList = new ArrayList<Orders>();
    public static Form<Orders> ordersForm = Form.form(Orders.class);
    private static Orders orders;

    public static Result payment(String id) {
        orders = Orders.finder.byId(id);
        if (orders != null) {
            session("Oid", orders.getId());
            ordersForm = Form.form(Orders.class).fill(orders);
            return showmain(payment.render(orders, ordersForm));
        } else {
            List<Orders> NoPay = Orders.NoPayList(session("usr"));
            return showmain(listPay.render(NoPay));
        }
    }

    public static String BillPicPath = Play.application().configuration().getString("bill_pic_path");

    public static Result insertpayment() {
        Form<Orders> bill = ordersForm.bindFromRequest();
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("status");
        String fileName, contentType;
        Orders orderss = bill.get();
        if (picture != null) {
            contentType = picture.getContentType();
            File file = picture.getFile();
            fileName = picture.getFilename();
            if (!contentType.startsWith("image")) {
                flash("msgError", "นามสกุลภาพไม่ตรงตามชนิด กรุณาตรวจสอบใหม่ด่วยค่ะ!");
                return showmain(payment.render(orders, bill));
            }
            fileName = orderss.getId() + fileName.substring(fileName.lastIndexOf("."));
            file.renameTo(new File(BillPicPath, fileName));
            String status = fileName;
            Date dataDate = new Date();
            Orders data = new Orders(session("Oid"), dataDate, users, status);
            Orders.update(data);
        }
        flash("billok", "สินค้าจะถูกส่งภายใน 3-7 วัน");
        List<Orders> NoPay = Orders.NoPayList(session("usr"));
        return showmain(listPay.render(NoPay));
    }
    public static Result listPayment (){
        ordersList = Orders.payList();
        return showmainadmin(listPayment.render(ordersList));
    }
}