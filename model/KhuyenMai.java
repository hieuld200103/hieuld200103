package model;
import java.sql.Date;
public class KhuyenMai {
    private int id_KhuyenMai;
    private String tenChuongTrinh;
    private double phanTramKM;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    
    public KhuyenMai(int id_KhuyenMai, String tenChuongTrinh, double phanTramKM, Date ngayBatDau, Date ngayKetThuc) {
        this.id_KhuyenMai = id_KhuyenMai;
        this.tenChuongTrinh = tenChuongTrinh;
        this.phanTramKM = phanTramKM;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
    }
    public int getID_KhuyenMai() {
        return id_KhuyenMai;
    }
    public void setID_KhuyenMai(int id_KhuyenMai) {
        this.id_KhuyenMai = id_KhuyenMai;
    }
    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }
    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }
    public double getPhanTramKM() {
        return phanTramKM;
    }
    public void setPhanTram(double phanTramKM) {
        this.phanTramKM = phanTramKM;
    }
    public Date getNgayBatDau() {
        return ngayBatDau;
    }
    public void setNgayBatDau(Date ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }
    public Date getNgayKetThuc() {
        return ngayKetThuc;
    }
    public void setNgayKetThuc(Date ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }
}