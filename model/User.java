package model;

public class User {
    public enum Roles{
        SILVER, GOLD, DIAMOND;
    }
    private int id_User;
    private String tenUser;
    private String sdt;
    private String email;
    private String matKhau;
    private Roles role;

   
    public User(int id_User, String tenUser, String sdt, String email, String matKhau, Roles role) {
        this.id_User = id_User;
        this.tenUser = tenUser;
        this.sdt = sdt;
        this.email = email;
        this.matKhau = matKhau;
        this.role = role;
    }

    public User(int id_User, String tenUser, String sdt, String email, Roles role) {
        this.id_User = id_User;
        this.tenUser = tenUser;
        this.sdt = sdt;
        this.email = email;
        this.role = role;
    }
    public User(int id_User, String tenUser){
        this.id_User = id_User;
        this.tenUser = tenUser;
    }

    public int getID_User() { return id_User; }
    public void setID_User(int id_User) { this.id_User = id_User; }

    public String getTenUser() { return tenUser; }
    public void setTenUser(String tenUser) { this.tenUser = tenUser; }

    public String getSDT() { return sdt; }
    public void setSDT(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public Roles getRole() { return role; }
    public void setRole(Roles role) { this.role = role; }

}
