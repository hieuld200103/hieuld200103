package model;

import java.time.LocalDateTime;

public class DatBan {
    public enum TrangThai{
        CHO_XAC_NHAN, DA_XAC_NHAN,DA_HUY
    }
    private int id_DatBan;
    private int id_ChiNhanh;
    private int id_User;
    private String listID_BanAn;
    private LocalDateTime ngayDat;
    private LocalDateTime ngayAn;
    private TrangThai trangThai;

    public DatBan(int id_DatBan, int id_ChiNhanh, int id_User,String listID_BanAn, LocalDateTime ngayDat, LocalDateTime ngayAn, TrangThai trangThai){
        this.id_DatBan = id_DatBan;
        this.id_ChiNhanh = id_ChiNhanh;
        this.id_User = id_User;
        this.listID_BanAn  = listID_BanAn;
        this.ngayDat = ngayDat;
        this.ngayAn = ngayAn;
        this.trangThai = trangThai;
    }
   
    public int getID_DatBan(){return id_DatBan;}
    public void setID_DatBan(int id_DatBan){this.id_DatBan = id_DatBan;}

    public int getID_ChiNhanh(){return id_ChiNhanh;}
    public void setID_ChiNhanh(int id_ChiNhanh){this.id_ChiNhanh = id_ChiNhanh;}

    public int getID_User(){return id_User;}
    public void setID_User(int id_User){this.id_User = id_User;};

    public String getListID_BanAn(){return listID_BanAn;}
    public void setListID_BanAn(String listID_BanAn){this.listID_BanAn = listID_BanAn;}

    public LocalDateTime getNgayDat(){return ngayDat;}
    public void setNgayDat(LocalDateTime ngayDat){this.ngayDat = ngayDat;}

    public LocalDateTime getNgayAn(){return ngayAn;}
    public void setNgayAn(LocalDateTime ngayAn){this.ngayAn = ngayAn;}
    
    public TrangThai getTrangThai(){return trangThai;}
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;}
}
