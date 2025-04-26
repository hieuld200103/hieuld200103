package model;

public class NhanVien {
    private int id_NhanVien;
    private int id_ChiNhanh;
    private String tenNV;
    private String chucVu;
    private String sdt;
    private String email;
    private String matkhau;

    public NhanVien(int id_NhanVien, int id_ChiNhanh, String tenNV, String chucVu, String sdt,String email, String matkhau ){
        this.id_NhanVien = id_NhanVien;
        this.id_ChiNhanh = id_ChiNhanh;
        this.tenNV = tenNV;
        this.chucVu = chucVu;
        this.sdt = sdt;
        this.email = email;
        this.matkhau = matkhau;
    }

    public void setID_NhanVien(int id_NhanVIen){this.id_NhanVien = id_NhanVIen;};
    public int getID_NhanVien(){return id_NhanVien;};

    public void setID_ChiNhanh(int id_ChiNhanh){this.id_ChiNhanh = id_ChiNhanh;};
    public int getID_ChiNhanh(){return id_ChiNhanh;};

    public void setTenNV(String tenNV){this.tenNV = tenNV;};
    public String getTenNV(){return tenNV;};

    public void setChucVu(String chucVu){this.chucVu = chucVu;};
    public String getChucVu(){return chucVu;};

    public void setSDT(String sdt){this.sdt = sdt;};
    public String getSDT(){return sdt;};

    public void setEmail(String email){this.email = email;};
    public String getEmail(){return email;};

    public void setMatKhau(String matkhau){this.matkhau = matkhau;};
    public String getMatKhau(){return matkhau;};

}
