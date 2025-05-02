package model;

public class KhachHang {
    public enum TrangThai {
        DA_NHAN_BAN, DA_THANH_TOAN
    }
    private int id_KhachHang;
    private int id_User;
    private int id_ChiNhanh;
    private String tenKH;
    private String sdt;
    private TrangThai trangThai;

    public KhachHang(int id_KhachHang,int id_User,int id_ChiNhanh, String tenKH, String sdt, TrangThai trangThai) {
        this.id_KhachHang = id_KhachHang;
        this.id_User = id_User;
        this.id_ChiNhanh = id_ChiNhanh;
        this.tenKH = tenKH ;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public int getID_KhachHang() { return id_KhachHang; }
    public void setID_KhachHang(int id_KhachHang) { this.id_KhachHang = id_KhachHang; }

    public int getID_User() { return id_User; }
    public void setID_User(int id_User) { this.id_User = id_User; }
    
    public int getID_ChiNhanh() { return id_ChiNhanh; }
    public void setID_ChiNhanh(int id_ChiNhanh) { this.id_ChiNhanh = id_ChiNhanh;}

    public String getTenKH() { return tenKH; }
    public void setTenKH(String tenKH) { this.tenKH = tenKH; }

    public String getSDT() { return sdt; }
    public void setSDT(String sdt) { this.sdt = sdt;}

    public TrangThai getTrangThai(){return trangThai;}
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;}

}
