package model;

import java.time.LocalDateTime;

public class HoaDon {
    private int id_HoaDon;
    private int id_DonHang;
    private int tongTien;
    private int phaiTra;        
    private String ptThanhToan;  
    private String trangThai;    
    private int id_KhuyenMai;
    private LocalDateTime ngayThanhToan;
    private LocalDateTime ngayTaoHD;
    // Constructors
    public HoaDon() {
    }

    public HoaDon(int id_HoaDon, int id_DonHang, int tongTien, int phaiTra, String ptThanhToan, String trangThai, int id_KhuyenMai, LocalDateTime ngayTaoHD,LocalDateTime ngayThanhToan) {
        this.id_HoaDon = id_HoaDon;
        this.id_DonHang = id_DonHang;
        this.tongTien = tongTien;
        this.phaiTra = phaiTra;
        this.ptThanhToan = ptThanhToan;
        this.trangThai = trangThai;
        this.id_KhuyenMai = id_KhuyenMai;
        this.ngayTaoHD = ngayTaoHD;
        this.ngayThanhToan = ngayThanhToan;
    }

    // Getters, Setters
    public int getIdHoaDon() {
        return id_HoaDon;
    }

    public void setIdHoaDon(int idHoaDon) {
        this.id_HoaDon = idHoaDon;
    }

    public int getIdDonHang() {
        return id_DonHang;
    }

    public void setIdDonHang(int idDonHang) {
        this.id_DonHang = idDonHang;
    }

    public int getTongTien() {
        return tongTien;
    }

    public void setTongTien(int tongTien) {
        this.tongTien = tongTien;
    }

    public int getPhaiTra() {
        return phaiTra;
    }

    public void setPhaiTra(int phaiTra) {
        this.phaiTra = phaiTra;
    }

    public String getPtThanhToan() {
        return ptThanhToan;
    }

    public void setPtThanhToan(String ptThanhToan) {
        this.ptThanhToan = ptThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getIdKhuyenMai() {
        return id_KhuyenMai;
    }

    public void setIdKhuyenMai(int idKhuyenMai) {
        this.id_KhuyenMai = idKhuyenMai;
    }

    public LocalDateTime getNgayThanhToan() {
        return ngayThanhToan;
    }

    public void setNgayThanhToan(LocalDateTime ngayThanhToan) {
        this.ngayThanhToan = ngayThanhToan;
    }

    public LocalDateTime getNgayTaoHD() {
        return ngayTaoHD;
    }

    public void setNgayTaoHD(LocalDateTime ngayTaoHD) {
        this.ngayTaoHD = ngayTaoHD;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================= HÓA ĐƠN ====================== \n");
        sb.append("ID Hóa đơn         : ").append(id_HoaDon).append("\n");
        sb.append("ID Đơn hàng        : ").append(id_DonHang).append("\n");
        sb.append("PT Thanh toán      : ").append(ptThanhToan).append("\n");
        sb.append("Trạng thái         : ").append(trangThai).append("\n");
        sb.append("ID Khuyến mãi      : ").append(id_KhuyenMai == 0 ? "Không áp dụng" : id_KhuyenMai).append("\n");
        sb.append("Ngày tạo hóa đơn   : ").append(ngayTaoHD != null ? ngayTaoHD : "N/A").append("\n");
        sb.append("Ngày thanh toán    : ").append(ngayThanhToan != null ? ngayThanhToan : "N/A").append("\n");
        return sb.toString();
    }

}
