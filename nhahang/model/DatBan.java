package model;

import java.time.LocalDateTime;

public class DatBan {
    public enum TrangThai{
        CHO_XAC_NHAN, DA_XAC_NHAN,DA_HUY
    }
    private int id_DatBan;
    private int id_ChiNhanh;
    private int id_User;
    private int id_BanAn;
    private LocalDateTime ngayDat;
    private TrangThai trangThai;

    public DatBan(int id_DatBan, int id_ChiNhanh, int id_User,int id_BanAn, LocalDateTime ngayDat, TrangThai trangThai){
        this.id_DatBan = id_DatBan;
        this.id_ChiNhanh = id_ChiNhanh;
        this.id_User = id_User;
        this.id_BanAn  = id_BanAn;
        this.ngayDat = ngayDat;
        this.trangThai = trangThai;
    }
   


    public DatBan(int id_DatBan, int id_User){
        this.id_DatBan = id_DatBan;      
        this.id_User = id_User;

    }
    public int getID_DatBan(){return id_DatBan;}
    public void setID_DatBan(int id_DatBan){this.id_DatBan = id_DatBan;}

    public int getID_ChiNhanh(){return id_ChiNhanh;}
    public void setID_ChiNhanh(int id_ChiNhanh){this.id_ChiNhanh = id_ChiNhanh;}

    public int getID_User(){return id_User;}
    public void setID_User(int id_User){this.id_User = id_User;};

    public int getID_BanAn(){return id_BanAn;}
    public void setID_BanAn(int id_BanAn){this.id_BanAn = id_BanAn;}

    public LocalDateTime getNgayDat(){return ngayDat;}
    public void setNgayDat(LocalDateTime ngayDat){this.ngayDat = ngayDat;}
    
    public TrangThai getTrangThai(){return trangThai;}
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;}
}
