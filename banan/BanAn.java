package banan;

public class BanAn {
    public enum TrangThai{
        TRONG, DA_DAT, DANG_SU_DUNG;
    }

    private int id_BanAn;
    private int soGhe;
    private int id_ChiNhanh;
    private TrangThai trangThai;

    public BanAn(int id_BanAn, int id_ChiNhanh, int soGhe, TrangThai trangThai){
        this.id_BanAn = id_BanAn;
        this.id_ChiNhanh = id_ChiNhanh;
        this.soGhe = soGhe;
        this.trangThai = trangThai;
    }

    public BanAn(int ID_BanAn, int soGhe){
        this.id_BanAn = ID_BanAn;      
        this.soGhe = soGhe;      
    }

    public int getID_BanAn(){return id_BanAn;};
    public void setID_BanAn(int id_BanAn){this.id_BanAn = id_BanAn;};

    public int getID_ChiNhanh(){return id_ChiNhanh;};
    public void setID_ChiNhanh(int id_ChiNhanh){this.id_ChiNhanh = id_ChiNhanh;};
    
    public int getSoGhe(){return soGhe;};
    public void setSoGhe(int soGhe){this.soGhe = soGhe;};

    public TrangThai getTrangThai(){return trangThai;};
    public void setTrangThai(TrangThai trangThai){this.trangThai = trangThai;};

    
}
