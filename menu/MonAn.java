package menu;

public class MonAn {
    public enum LoaiMonAn {
        DO_AN, DO_UONG;
    }
    public enum DanhMucMonAn {
        DO_KHO, DO_NUOC, CO_CON, KHONG_CON;
    }
    
    private int id_MonAn;
    private String tenMon;
    private int gia;
    private String mota;
    private LoaiMonAn loaiMonAn;
    private DanhMucMonAn danhMuc;

    public MonAn(int id_MonAn, String tenMon, int gia, String mota, LoaiMonAn loaiMonAn, DanhMucMonAn danhMuc){
        this.id_MonAn = id_MonAn;
        this.tenMon = tenMon;
        this.gia = gia;
        this.mota = mota;
        this.loaiMonAn = loaiMonAn;
        this.danhMuc = danhMuc;
    }

    public int getID_MonAn() { return id_MonAn; }
    public void setID_MonAn(int id_MonAn) { this.id_MonAn = id_MonAn; }

    public String getTenMon() { return tenMon; }
    public void setTenMon(String tenMon) { this.tenMon = tenMon; }
   
    public int getGia() { return gia; }
    public void setGia(int gia) { this.gia = gia; }
   
    public String getMoTa() { return mota; }
    public void setMoTa(String mota) { this.mota = mota; }
    
    public LoaiMonAn getLoaiMonAn() { return loaiMonAn; }
    public void setLoaiMonAn(LoaiMonAn loaiMonAn) { this.loaiMonAn = loaiMonAn; }
    
    public DanhMucMonAn getDanhMuc() { return danhMuc; }
    public void setDanhMuc(DanhMucMonAn danhMuc) { this.danhMuc = danhMuc; }
}