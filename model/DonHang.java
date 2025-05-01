package model;

public class DonHang {
    public enum TrangThai{
        DANG_CHUAN_BI, DA_HOAN_THANH;
    }
    public enum KieuDonHang{
        TAI_NHA_HANG, MANG_VE 
    }
    private int id_DonHang;
    private int id_User;
    private Integer id_BanAn;
    private TrangThai trangThai;
    private KieuDonHang kieuDonHang;

    public DonHang(int id_DonHang, int id_User, Integer id_BanAn, TrangThai trangThai, KieuDonHang kieuDonHang){
        this.id_DonHang = id_DonHang;
        this.id_User = id_User;
        this.id_BanAn = id_BanAn;  
        this.trangThai = trangThai;
        this.kieuDonHang = kieuDonHang;
    }
    
    public DonHang(int id_DonHang, int id_User,TrangThai trangThai, KieuDonHang kieuDonHang){
        this.id_DonHang = id_DonHang;
        this.id_User = id_User;
        this.trangThai = trangThai;
        this.kieuDonHang = kieuDonHang;
    }

    public DonHang(int id_DonHang, int id_User, KieuDonHang kieuDonHang){
        this.id_DonHang = id_DonHang;
        this.id_User = id_User;
        this.kieuDonHang = kieuDonHang;
    }

    public int getID_DonHang(){return id_DonHang;}
    public void setID_DonHang(int id_DonHang){this.id_DonHang = id_DonHang;}

    public int getID_User(){return id_User;}
    public void setID_User(int id_User){this.id_User = id_User;}

    public Integer getID_BanAn(){return id_BanAn;}
    public void setID_BanAn(Integer id_BanAn){this.id_BanAn = id_BanAn;}

    public TrangThai getTrangThai(){return trangThai;}
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;}
    
    public KieuDonHang getKieuDonHang(){return kieuDonHang;};
    public void setKieuDonHang( KieuDonHang kieuDonHang){this.kieuDonHang = kieuDonHang;}
}
