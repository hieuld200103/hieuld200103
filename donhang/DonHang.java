package donhang;

public class DonHang {
    public enum TrangThai{
        DANG_CHUAN_BI, DA_HOAN_THANH;
    }
    private int id_DonHang;
    private int id_User;
    private int id_BanAn;
    private TrangThai trangThai;

    public DonHang(int id_DonHang, int id_User, int id_BanAn, TrangThai trangThai){
        this.id_DonHang = id_DonHang;
        this.id_User = id_User;
        this.id_BanAn = id_BanAn;  
        this.trangThai = trangThai;
    }

    public int getID_DonHang(){return id_DonHang;}
    public void setID_DonHang(int id_DonHang){this.id_DonHang = id_DonHang;}

    public int getID_User(){return id_User;}
    public void setID_User(int id_User){this.id_User = id_User;}

    public int getID_BanAn(){return id_BanAn;}
    public void setID_BanAn(int id_BanAn){this.id_BanAn = id_BanAn;}

    public TrangThai getTrangThai(){return trangThai;}
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;}

}
