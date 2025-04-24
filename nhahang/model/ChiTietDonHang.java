package model;

public class ChiTietDonHang {
    
    private int id_ChiTiet;
    private int id_DonHang;
    private int id_MonAn;
    private int soLuong;
    private int donGia;
    private int thanhTien;
    private DonHang.KieuDonHang kieuDonHang;

    public ChiTietDonHang(int id_ChiTiet, int id_DonHang, int id_MonAn, int soLuong,int donGia, int thanhTien){
        this.id_ChiTiet = id_ChiTiet;
        this.id_DonHang = id_DonHang;
        this.id_MonAn = id_MonAn;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public ChiTietDonHang(int id_DonHang, int id_MonAn, int soLuong,int donGia, int thanhTien, DonHang.KieuDonHang kieuDonHang){
        this.id_DonHang = id_DonHang;
        this.id_MonAn = id_MonAn;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
        this.kieuDonHang = kieuDonHang;
    }

    public int getID_ChiTiet(){return id_ChiTiet;};
    public void setID_ChiTiet(int id_ChiTiet){this.id_ChiTiet = id_ChiTiet;}

    public int getID_DonHang(){return id_DonHang;};
    public void setID_DonHang(int id_DonHang){this.id_DonHang = id_DonHang;}

    public int getID_MonAn(){return id_MonAn;};
    public void setID_MonAn(int id_MonAn){this.id_MonAn = id_MonAn;}

    public int getSoLuong(){return soLuong;};
    public void setSoLuong(int soLuong){this.soLuong = soLuong;}

    public int getGia(){return donGia;}
    public void setGia(int donGia){this.donGia = donGia;}

    public int getThanhTien(){return thanhTien;};
    public void setThanhTien(int thanhTien){this.thanhTien = thanhTien;}

    public DonHang.KieuDonHang getKieuDonHang(){return kieuDonHang;};
    public void setKieuDonHang( DonHang.KieuDonHang kieuDonHang){this.kieuDonHang = kieuDonHang;}

}
