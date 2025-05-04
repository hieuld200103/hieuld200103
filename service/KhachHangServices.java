package service;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import background.DatBanChecker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connection.DatabaseConnection;
import model.DatBan;
import model.KhachHang;
import model.NhanVien;
import model.User;
public class KhachHangServices {
    // Thêm khách hàng
    public static void khachHangNhanBan(Scanner scanner, NhanVien currentNV, int idChiNhanh) {
        while (true) {
            System.out.println("\n=== XÁC NHẬN NHẬN BÀN ===");    
            System.out.print("Nhập số điện thoại đã dùng để đặt bàn (0 để thoát): ");
            String sdt = scanner.nextLine();
    
            if (sdt.equals("0")) break;
            if (!sdt.matches("\\d{10}")) {
                System.out.println("Lỗi: Số điện thoại không hợp lệ!");
                continue;
            }
    
            User user = timUser(sdt);
            if (user == null) {
                System.out.println("Không tìm thấy tài khoản với số điện thoại này!");
                continue;
            }

            DatBan dadatBan = datBanHopLe(currentNV, user.getID_User(), idChiNhanh);
            if (dadatBan == null) {
                System.out.println("Khách hàng không có đặt bàn hợp lệ tại chi nhánh này hoặc đã nhận bàn!");
                continue;
            }
    
            KhachHang kh = themKH(user.getID_User(), user.getTenUser(), sdt);
            if (kh != null) {
                System.out.println("✅ Đã xác nhận khách hàng: " + kh.getTenKH());
            } else {
                System.out.println("❌ Lỗi khi thêm khách hàng!");
            }
        }
    }
    
    //Kiểm tra lịch đặt của khách có chưa, đúng chi nhánh chưa
    public static DatBan datBanHopLe(NhanVien currentNV, int idUser, int idChiNhanh) {
        for (DatBan db : DatBanServices.xemDSDaXacNhan(currentNV, idChiNhanh)) {
            if (db.getID_User() == idUser 
                && db.getID_ChiNhanh() == idChiNhanh 
                && !DatBanChecker.daNhanBan(idUser)) {
                return db;
            }
        }
        return null;
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
    public static KhachHang themKH(int idUser, String tenKH, String sdt) {
        int idChiNhanh = layIDCN(idUser);
        String sql = "INSERT INTO khachhang (ID_User, ID_ChiNhanh, TenKH, SDT, TrangThai) VALUES (?, ?, ?, ?, ?)";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, idUser);
            stmt.setInt(2, idChiNhanh);
            stmt.setString(3, tenKH);
            stmt.setString(4, sdt);
            stmt.setString(5, "DA_NHAN_BAN");
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int idKH = rs.getInt(1);
                    System.out.println("Xác nhận nhận bàn thành công!");
                    return new KhachHang(idKH, idUser, idChiNhanh, tenKH, sdt, KhachHang.TrangThai.DA_NHAN_BAN);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi check đặt bàn!");
            e.printStackTrace();
        }
        return null;
    }
    
    //Lấy chi nhánh từ user
    public static int layIDCN(int idUser){
        String sql = "SELECT ID_ChiNhanh FROM datban WHERE ID_User = ? AND TrangThai = 'DA_XAC_NHAN'";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1, idUser);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        return rs.getInt("ID_ChiNhanh");
                    }
                }
            }catch (SQLException e) {
                System.out.println("Lỗi khi lấy IDCN từ cơ sở dữ liệu");
                e.printStackTrace();
            }
        return 0;
    }

    //Xem danh sách khách hàng
    public static List<KhachHang> xemDanhSachKhachHang(int idCN) {
        List<KhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khachhang";    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) { 
                int stt = 1;   
            System.out.println("================================DANH SÁCH KHÁCH HÀNG================================");
            System.out.println("====================================================================================");
            System.out.printf("| %-3s | %-5s | %-20s | %-5s | %-15s | %-15s |\n", "STT","ID", "Tên Khách Hàng", "IDCN","Số Điện Thoại", "Trạng thái");
                
            while (rs.next()) {
                int id = rs.getInt("ID_KhachHang");
                int idUser = rs.getInt("ID_User");
                String tenKH = rs.getString("TenKH");
                String sdt = rs.getString("SDT");
                KhachHang.TrangThai trangThai = KhachHang.TrangThai.valueOf(rs.getString("TrangThai"));
                danhSach.add(new KhachHang(id,idUser,idCN, tenKH, sdt,trangThai));
                System.out.println("-------------------------------------------------------------------------------------");
                System.out.printf("| %-3d | %-5d | %-20s | %-5d | %-15s | %-15s |\n", stt++, id, tenKH, idCN, sdt,trangThai);
            }
    
            System.out.println("=====================================================================================");
        rs.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách khách hàng!");
            e.printStackTrace();
        }
        return danhSach;
    }

   
}
