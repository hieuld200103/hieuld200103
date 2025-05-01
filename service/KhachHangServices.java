package service;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DatabaseConnection;
import model.DatBan;
import model.KhachHang;
import model.NhanVien;
import model.User;
import userinterface.DichVuKhachHang;
public class KhachHangServices {
    // Thêm khách hàng
    public static KhachHang khachHangNhanBan(Scanner scanner, NhanVien currentNV) {
        while (true) {
            System.out.println("\n=== XÁC NHẬN NHẬN BÀN ===");    
            System.out.print("Nhập số điện thoại đã dùng để đặt bàn (0 để thoát): ");
            String sdt = scanner.nextLine();

            if (sdt.equals("0")) return null;
            if (!sdt.matches("\\d{10}")) {
                System.out.println("Lỗi: Số điện thoại không hợp lệ!");
                continue;
            }

            User user = timUser(sdt);
            if (user == null) {
                System.out.println("Không tìm thấy tài khoản với số điện thoại này!");
                continue;
            }

            DatBan datBan = DichVuKhachHang.daXacNhanDatBan(user.getID_User());
            if (datBan == null) {
                System.out.println("Số điện thoại này chưa có đặt bàn trước!");
                continue;
            }

            KhachHang kh = themKH(user.getID_User(), user.getTenUser(), sdt);
            if (kh != null) {
                return kh;
            } else {
                System.out.println("Lỗi khi thêm khách hàng!");
            }
                         
        }
    }

    //Tìm User 
    public static User timUser(String sdt) {
        String sql = "SELECT ID_User, TenUser FROM user WHERE SDT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, sdt);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("ID_User");
                    String ten = rs.getString("TenUser");
                    return new User(id, ten);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tìm User!");
            e.printStackTrace();
        }
        return null;
    }

    
    //Thêm vào bảng khách hàng
    public static KhachHang themKH(int idUser, String tenKH, String sdt){
        String sql = "INSERT INTO khachhang (ID_User, TenKH, SDT, TrangThai) VALUES (?, ?, ?, 'DA_NHAN_BAN')";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
                stmt.setInt(1,idUser);
                stmt.setString(2,tenKH);
                stmt.setString(3,sdt);
                stmt.executeUpdate();
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if(rs.next()){
                        int idKH = rs.getInt(1);
                        System.out.println("Xác nhận nhận bàn thành công!");
                        return new KhachHang(idKH, idUser, tenKH, sdt, KhachHang.TrangThai.DA_NHAN_BAN);
                    }
                }
            }catch(SQLException e){
                System.out.println("Lỗi khi check đặt bàn!");
                e.printStackTrace();
            } 
            return null;
    }

    //Xóa thông tin khách hàng
    public static void xoaKhachHang(Scanner scanner) {
        xemDanhSachKhachHang();
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
            System.out.println("========================DANH SÁCH KHÁCH HÀNG=======================");
            System.out.printf("| %-5s | %-20s | %-15s | %-15s |\n", "ID", "Tên Khách Hàng", "Số Điện Thoại", "Trạng thái");
            System.out.println("===================================================================");    
            while (rs.next()) {
                int id = rs.getInt("ID_KhachHang");
                int idUser = rs.getInt("ID_User");
                String tenKH = rs.getString("TenKH");
                String sdt = rs.getString("SDT");
                KhachHang.TrangThai trangThai = KhachHang.TrangThai.valueOf(rs.getString("TrangThai"));
                danhSach.add(new KhachHang(id,idUser, tenKH, sdt,trangThai));
    
                System.out.printf("| %-5d | %-20s | %-15s | %-15s |\n", id, tenKH, sdt,trangThai);
            }
    
            System.out.println("===================================================================");
        rs.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách khách hàng!");
            e.printStackTrace();
        }
        return danhSach;
    }

   
}
