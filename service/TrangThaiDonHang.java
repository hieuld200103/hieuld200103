package service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import connection.DatabaseConnection;
import model.User;
import model.NhanVien;
import userinterface.DichVuKhachHang;
import userinterface.QuanLyDonHang;
public class TrangThaiDonHang {
        public static void kiemTraDonHangKH(Scanner scanner, User currentUser) {
            int idUser = currentUser.getID_User();
            String sql = "SELECT dh.id_DonHang,dh.id_User, dh.id_BanAn, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
            "ct.id_MonAn, ct.SoLuong, ct.dongia, ct.thanhtien, ct.kieudonhang AS kieuCT " +
            "FROM donhang dh " +
            "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
            "WHERE dh.Id_User = ? " +
            "ORDER BY dh.id_DonHang" ;
            try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idUser);
                ResultSet rs = stmt.executeQuery();
                boolean found = false;
                System.out.println("=============================================== Trạng thái đơn hàng của bạn =================================================");
                System.out.println("==============================================================================================================================");
                System.out.printf("%-10s | %-8s | %-9s | %-8s | %-8s | %-10s | %-12s | %-15s%n",
                        "ID_DonHang", "ID_BanAn", "ID_MonAn", "SoLuong", "DonGia", "ThanhTien", "KieuChiTiet","TrangThai");
                System.out.println("------------------------------------------------------------------------------------------------------------------------------");

            while (rs.next()) {
                found = true;
                System.out.printf("%-10d | %-8d | %-9d | %-8d | %-8d | %-10d | %-12s | %-15s%n",
                        rs.getInt("id_DonHang"),
                        rs.getInt("id_BanAn"),
                        rs.getInt("id_MonAn"),
                        rs.getInt("SoLuong"),
                        rs.getInt("dongia"),
                        rs.getInt("thanhtien"),
                        rs.getString("kieuCT"),
                        rs.getString("TrangThai"));
                        System.out.println("------------------------------------------------------------------------------------------------------------------------------");
                
            }
            DichVuKhachHang.dichVu(currentUser, scanner);
            if (!found) {
                System.out.println("Không tìm thấy đơn hàng hoặc không có chi tiết đơn hàng.");
            }

        }   catch (SQLException e) {
                System.out.println("Lỗi khi truy vấn: " + e.getMessage());
        }
    }
    public static void kiemTraDonHangNV(Scanner scanner, NhanVien currentNV, int idChiNhanh) {
        System.out.print("Nhập ID đơn hàng cần kiểm tra: ");
         int idDonHang = scanner.nextInt();
        String sql = "SELECT dh.id_DonHang, dh.id_User, dh.id_BanAn, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
         "ct.id_MonAn, ct.SoLuong, ct.dongia, ct.thanhtien, ct.kieudonhang AS kieuCT " +
         "FROM donhang dh " +
         "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
         "WHERE dh.id_DonHang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idDonHang);
            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            System.out.println("=================================================== Trạng thái đơn hàng ======================================================");
            System.out.println("==============================================================================================================================");
            System.out.printf("%-10s | %-8s | %-8s | %-15s | %-9s | %-8s | %-8s | %-10s | %-12s%n",
                    "ID_DonHang", "ID_User", "ID_BanAn", "TrangThai",
                    "ID_MonAn", "SoLuong", "DonGia", "ThanhTien", "KieuChiTiet");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------");

        while (rs.next()) {
            found = true;
            System.out.printf("%-10d | %-8d | %-8d | %-15s | %-9d | %-8d | %-8d | %-10d | %-12s%n",
                    rs.getInt("id_DonHang"),
                    rs.getInt("id_User"),
                    rs.getInt("id_BanAn"),
                    rs.getString("TrangThai"),
                    rs.getInt("id_MonAn"),
                    rs.getInt("SoLuong"),
                    rs.getInt("dongia"),
                    rs.getInt("thanhtien"),
                    rs.getString("kieuCT"));
                    System.out.println("------------------------------------------------------------------------------------------------------------------------------");
                    
        }
        QuanLyDonHang.quanLy(currentNV, idChiNhanh, scanner);
        if (!found) {
            System.out.println("Không tìm thấy đơn hàng hoặc không có chi tiết đơn hàng.");
        }

    }   catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn: " + e.getMessage());
    }
}
    public static void kiemTraDonHangNV2(Scanner scanner, NhanVien currentNV,int idChiNhanh){
          String sql =  "SELECT dh.id_DonHang, dh.id_User, dh.id_BanAn, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
          "ct.id_MonAn, ct.SoLuong, ct.dongia, ct.thanhtien, ct.kieudonhang AS kieuCT " +
          "FROM donhang dh " +
          "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
          "ORDER BY dh.id_DonHang";
        try ( Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql) )
         {
            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            System.out.println("=================================================== Trạng thái đơn hàng ======================================================");
            System.out.println("==============================================================================================================================");
            System.out.printf("%-10s | %-8s | %-8s | %-15s | %-9s | %-8s | %-8s | %-10s | %-12s%n",
                    "ID_DonHang", "ID_User", "ID_BanAn", "TrangThai",
                    "ID_MonAn", "SoLuong", "DonGia", "ThanhTien", "KieuChiTiet");
            System.out.println("==============================================================================================================================");
            while(rs.next()){
                found = true;
                System.out.printf("%-10d  | %-8d | %-8s | %-15s | %-9d | %-8d | %-8d | %-10d | %-12s%n",
                        rs.getInt("id_DonHang"),
                        rs.getInt("id_User"),
                        rs.getInt("id_BanAn"),
                        rs.getString("TrangThai"),
                        rs.getInt("id_MonAn"),
                        rs.getInt("SoLuong"),
                        rs.getInt("dongia"),
                        rs.getInt("thanhtien"),
                        rs.getString("kieuCT"));
                System.out.println("------------------------------------------------------------------------------------------------------------------------------");
                
            }
            QuanLyDonHang.quanLy(currentNV, idChiNhanh, scanner);
        
        if (!found) {
            System.out.println("Không tìm thấy đơn hàng hoặc không có chi tiết đơn hàng.");
        }
    }
            catch (SQLException e) {
                System.out.println("Lỗi khi truy vấn: " + e.getMessage());
        } 
    
    }
}

    

