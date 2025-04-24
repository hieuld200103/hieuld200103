package model;

public class KhachHang {
    private int id_KhachHang;
    private int id_User;
    private String tenKH;
    private String sdt;

    public KhachHang(int id_KhachHang,int id_User, String tenKH, String sdt) {
        this.id_KhachHang = id_KhachHang;
        this.id_User = id_User;
        this.tenKH = tenKH ;
        this.sdt = sdt;
    }

    public int getID_KhachHang() { return id_KhachHang; }
    public void setID_KhachHang(int id_KhachHang) { this.id_KhachHang = id_KhachHang; }

    public int getID_User() { return id_User; }
    public void setID_User(int id_User) { this.id_User = id_User; }
    
    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSDT() { return sdt; }
    public void setSDT(String sdt) { this.sdt = sdt;}
}
