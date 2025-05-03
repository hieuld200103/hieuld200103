package service;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import connection.DatabaseConnection;
import model.User;
import model.NhanVien;
import userinterface.QuanLyDonHang;
public class TrangThaiDonHang {
        public static void kiemTraDonHangKH(Scanner scanner, User currentUser) {
            int idUser = currentUser.getID_User();
            String sql = "SELECT dh.id_DonHang, dh.id_User, dh.id_ChiNhanh, dh.id_BanAn, dh.thoigiantaodon, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
            "ct.id_MonAn, ct.SoLuong, ct.thanhtien, ct.kieudonhang AS kieuCT, " +
            "ma.TenMon, "+
            "cn.TenCN " +
            "FROM donhang dh " +
            "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
            "JOIN monan ma ON ct.id_MonAn = ma.id_MonAn " +
            "JOIN chinhanh cn ON dh.id_ChiNhanh = cn.id_ChiNhanh " +
            "WHERE dh.id_User = ? " +
            "ORDER BY dh.id_DonHang" ;
            try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idUser);
                ResultSet rs = stmt.executeQuery();
                boolean found = false;
                int stt=1;
                int preId = -1;
            while (rs.next()) {
                found = true;
                int curId = rs.getInt("id_DonHang");
                if (curId != preId) {
             if (preId != -1) {
                System.out.println();
        }
                stt = 1;
                System.out.println("\n=======================================================================================================");
                System.out.println("ĐƠN HÀNG " + rs.getInt("id_DonHang"));
                System.out.println(rs.getString("TenCN"));
                System.out.println("ID Khách hàng: "+rs.getInt("ID_User")+"                                                         " + "Bàn: "+rs.getInt("Id_BanAn"));
                System.out.println("Thời gian: " + rs.getTimestamp("thoigiantaodon").toLocalDateTime().toString().replace("T", " ")+"                                            "+"Kiểu đơn: "+ rs.getString("kieuCT"));
                System.out.println();
                System.out.printf(" %-5s | %-50s | %-8s | %-10s | %-20s%n", 
                   "STT","TenMon", "SoLuong", "ThanhTien","TrangThai");
                System.out.println("-------------------------------------------------------------------------------------------------------");
                preId = curId;
            }
                System.out.printf(" %-5d | %-50s | %-8d | %,10d | %-20s%n",
                    stt++,
                    rs.getString("TenMon"),
                    rs.getInt("SoLuong"),
                    rs.getInt("thanhtien"),
                    rs.getString("TrangThai"));
                    System.out.println("---------------------------------------------------------------------------------------------------");
                        if (!found) {
                System.out.println("Không tìm thấy đơn hàng hoặc không có chi tiết đơn hàng.");
                        }
            }
            
        }   catch (SQLException e) {
                System.out.println("Lỗi khi truy vấn: " + e.getMessage());
        }
    }
    public static void kiemTraDonHangNV(Scanner scanner, NhanVien currentNV, int idChiNhanh) {
        DonHangServices.xemDSDonHang();
        System.out.print("Nhập ID đơn hàng cần kiểm tra: ");
         int idDonHang = scanner.nextInt();
         String sql = "SELECT dh.id_DonHang, dh.id_User, dh.id_ChiNhanh, dh.id_BanAn, dh.thoigiantaodon, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
         "ct.id_MonAn, ct.SoLuong, ct.thanhtien, ct.dongia, ct.kieudonhang AS kieuCT, " +
         "ma.TenMon, "+
         "cn.TenCN " +
         "FROM donhang dh " +
         "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
         "JOIN monan ma ON ct.id_MonAn = ma.id_MonAn " +
         "JOIN chinhanh cn ON dh.id_ChiNhanh = cn.id_ChiNhanh " +
         "WHERE dh.id_DonHang = ? AND dh.id_ChiNhanh = ? " +
         "ORDER BY dh.id_DonHang" ;
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql))
        {
            stmt.setInt(1, idDonHang);
            stmt.setInt(2, idChiNhanh); 
            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            int stt=1;
            int preId = -1;
        while (rs.next()) {
            found = true;
            int curId = rs.getInt("id_DonHang");
                if (curId != preId) {
             if (preId != -1) {
                System.out.println();
        }   stt =1;
            System.out.println("\n=========================================================================================================================");
            System.out.println("ĐƠN HÀNG " + rs.getInt("id_DonHang"));
            System.out.println(rs.getString("TenCN"));
            System.out.println("ID Khách hàng: "+rs.getInt("ID_User")+"                                                         " + "Bàn: "+rs.getInt("Id_BanAn"));
            System.out.println("Thời gian: " + rs.getTimestamp("thoigiantaodon").toLocalDateTime().toString().replace("T", " ")+"                                            "+"Kiểu đơn: "+ rs.getString("kieuCT"));
            System.out.println();
            System.out.printf(" %-5s | %-10s | %-50s | %-10s | %-10s | %-10s | %-15s%n",
                    "STT", "ID_MonAn", "TenMon", "SoLuong", "DonGia", "ThanhTien", "TrangThai");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
            preId = curId;
        }
            System.out.printf(" %-5d | %-10d | %-50s | %-10d | %,10d | %,10d | %-15s%n",
                    stt++,
                    rs.getInt("id_MonAn"),
                    rs.getString("TenMon"),
                    rs.getInt("SoLuong"),
                    rs.getInt("dongia"),
                    rs.getInt("thanhtien"),
                    rs.getString("TrangThai"));
                    System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
                
        }
        if (!found) {
            System.out.println("Không tìm thấy đơn hàng hoặc không có chi tiết đơn hàng.");
        QuanLyDonHang.quanLy(currentNV, idChiNhanh, scanner);
    }
    }   catch (SQLException e) {
            System.out.println("Lỗi khi truy vấn: " + e.getMessage());
    }
}
    public static void kiemTraDonHangNV2(Scanner scanner, NhanVien currentNV,int idChiNhanh){
        String sql = "SELECT dh.id_DonHang, dh.id_User, dh.id_ChiNhanh, dh.id_BanAn, dh.thoigiantaodon, dh.TrangThai, dh.kieudonhang AS kieuDH, " +
        "ct.id_MonAn, ct.SoLuong, ct.thanhtien, ct.dongia, ct.kieudonhang AS kieuCT, " +
        "ma.TenMon, "+
        "cn.TenCN " +
        "FROM donhang dh " +
        "JOIN chitietdonhang ct ON dh.id_DonHang = ct.id_DonHang " +
        "JOIN monan ma ON ct.id_MonAn = ma.id_MonAn " +
        "JOIN chinhanh cn ON dh.id_ChiNhanh = cn.id_ChiNhanh " +
        "WHERE dh.id_ChiNhanh = ? " +
        "ORDER BY dh.id_DonHang" ;
        try ( Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql) )
         {
            stmt.setInt(1, idChiNhanh);
            ResultSet rs = stmt.executeQuery();
            boolean found = false;
            int stt=1;
            int preId = -1;
        while (rs.next()) {
            found = true;
            int curId = rs.getInt("id_DonHang");
                if (curId != preId) {
             if (preId != -1) {
                System.out.println();
        }   
            stt =1;
            System.out.println("\n=====================================================================================================================");
            System.out.println("ĐƠN HÀNG " + rs.getInt("id_DonHang"));
            System.out.println(rs.getString("TenCN"));
            System.out.println("ID Khách hàng: "+rs.getInt("ID_User")+"                                                         " + "Bàn: "+rs.getInt("Id_BanAn"));
            System.out.println("Thời gian: " + rs.getTimestamp("thoigiantaodon").toLocalDateTime().toString().replace("T", " ")+"                                            "+"Kiểu đơn: "+ rs.getString("kieuCT"));
            System.out.println();
            System.out.printf(" %-5s | %-10s | %-50s | %-10s | %-10s | %-10s | %-15s%n",
                    "STT", "ID_MonAn", "TenMon", "SoLuong", "DonGia", "ThanhTien", "TrangThai");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
            preId = curId;
        }
            System.out.printf(" %-5d | %-10d | %-50s | %-10d | %,10d | %,10d | %-15s%n",
                    stt++,
                    rs.getInt("id_MonAn"),
                    rs.getString("TenMon"),
                    rs.getInt("SoLuong"),
                    rs.getInt("dongia"),
                    rs.getInt("thanhtien"),
                    rs.getString("TrangThai"));
                    System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
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

    

