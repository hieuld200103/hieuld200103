package service;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

import connection.DatabaseConnection;
import model.DatBan;
import model.KhachHang;
import model.User;
public class KhachHangServices {
    // Thêm khách hàng
    public static KhachHang khachHangNhanBan(Scanner scanner) {
        System.out.println("\n=== Xác nhận nhận bàn ===");
    
        System.out.print("Nhập số điện thoại đã dùng để đăng ký tài khoản và đặt bàn: ");
        String sdt = scanner.nextLine();
        if (!sdt.matches("\\d{10}")) {
            System.out.println("Lỗi: Số điện thoại không hợp lệ!");
            return null;
        }
    
        String sqlTimUser = "SELECT ID_User, TenUser  FROM user WHERE SDT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtUser = conn.prepareStatement(sqlTimUser)) {
            
            stmtUser.setString(1, sdt);
            ResultSet rsUser = stmtUser.executeQuery();
    
            if (rsUser.next()) {
                int idUser = rsUser.getInt("ID_User");    
                String tenKH = rsUser.getString("TenUser");
                String sqlCheckDatBan = "SELECT * FROM datban WHERE ID_User = ?";
                try (PreparedStatement stmtCheck = conn.prepareStatement(sqlCheckDatBan)) {
                    stmtCheck.setInt(1, idUser);
                    ResultSet rsDatBan = stmtCheck.executeQuery();
                    
                    if (rsDatBan.next()) {
                       
                        String sqlInsertKH = "INSERT INTO khachhang (ID_User, TenKH, SDT, TrangThai) VALUES (?, ?, ?, 'DA_NHAN_BAN')";
                        try (PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertKH, PreparedStatement.RETURN_GENERATED_KEYS)) {
                            stmtInsert.setInt(1, idUser);
                            stmtInsert.setString(2, tenKH);
                            stmtInsert.setString(3, sdt);
                            stmtInsert.executeUpdate();
    
                            ResultSet genKeys = stmtInsert.getGeneratedKeys();
                            if (genKeys.next()) {
                                int idKH = genKeys.getInt(1);
                                System.out.println("Xác nhận nhận bàn thành công!");
                                return new KhachHang(idKH, idUser, tenKH, sdt);
                            }
                        }
                    } else {
                        System.out.println("Số điện thoại này chưa có đặt bàn trước!");
                    }
                }
            } else {
                System.out.println("Không tìm thấy tài khoản với số điện thoại này!");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xác nhận nhận bàn!");
            e.printStackTrace();
        }
    
        return null;
    }
    
  
    //Xóa thông tin khách hàng
    public static void xoaKhachHang(Scanner scanner) {
        System.out.print("Nhập ID khách hàng cần xóa: ");
        if (!scanner.hasNextInt()) {
            System.out.println(" Lỗi: ID không hợp lệ!");
            scanner.next(); 
            return;
        }

        int idKH = scanner.nextInt();
        scanner.nextLine(); 

        String sql = "DELETE FROM khachhang WHERE ID_KhachHang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idKH);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Xóa khách hàng thành công!");
            } else {
                System.out.println("Không tìm thấy khách hàng có ID " + idKH);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa khách hàng!");
            e.printStackTrace();
        }
    }
   

    //Xem danh sách khách hàng
    public static List<KhachHang> xemDanhSachKhachHang() {
        List<KhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khachhang";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            System.out.println("==========================================");
            System.out.printf("| %-5s | %-20s | %-12s |\n", "ID", "Tên Khách Hàng", "Số Điện Thoại");
            System.out.println("==========================================");
    
            while (rs.next()) {
                int id = rs.getInt("ID_KhachHang");
                int idUser = rs.getInt("ID_User");
                String tenKH = rs.getString("TenKH");
                String sdt = rs.getString("SDT");
                danhSach.add(new KhachHang(id,idUser, tenKH, sdt));
    
                System.out.printf("| %-5d | %-20s | %-12s |\n", id, tenKH, sdt);
            }
    
            System.out.println("==========================================");
    
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách khách hàng!");
            e.printStackTrace();
        }
        return danhSach;
    }
    

    //Xem bàn đã đặt
    public static List<DatBan> xemDanhSachDatBan(User currentUser) {
        List<DatBan> danhSach = new ArrayList<>();
        int idUser = currentUser.getID_User();
        String sql = "SELECT * FROM datban WHERE ID_User = ? AND TrangThai = 'CHO_XAC_NHAN' OR TrangThai = 'DA_XAC_NHAN'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,idUser);
            ResultSet rs = stmt.executeQuery();
    
            System.out.println("\n========================================= DANH SÁCH BÀN ĐÃ ĐẶT ========================================");
            System.out.println("========================================================================================================");
            System.out.printf("| %-5s | %-7s | %-8s | %-7s | %-20s | %-20s | %-15s |\n", "ID", "ID_CN", "ID_User", "List bàn", "Ngày đặt","Ngày ăn","Trạng thái");
            System.out.println("========================================================================================================");
    
            while (rs.next()) {
                int id = rs.getInt("ID_DatBan");
                int idCN = rs.getInt("ID_ChiNhanh");
                String idBanAn = rs.getString("List_BanAn");
                LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                danhSach.add(datBan);
    
                System.out.printf("| %-5d | %-7d | %-8d | %-7s | %-20s | %-20s | %-15s |\n",
                        id, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
            }
    
            System.out.println("========================================================================================================");
            rs.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách đặt bàn!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
}
