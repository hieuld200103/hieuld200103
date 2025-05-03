package service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connection.DatabaseConnection;
import model.NhanVien;
import model.NhanVien.Role;
import model.User;
import model.User.Roles;

import userinterface.QuanLyBanAn;

public class NhanVienServices {
    //Mã hóa mật khẩu
    private static String hashPassword(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes){
                sb.append(String.format("%02x",b));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException e ){
            throw new RuntimeException("Lỗi mã hóa mật khẩu!");
        }
    }

    // Hàm kiểm tra số điện thoại đã tồn tại chưa
    private static boolean kiemTraTonTaiSDT(String sdt) {
        String sql = "SELECT COUNT(*) FROM nhanvien WHERE SDT = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sdt);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra số điện thoại!");
            e.printStackTrace();
        }
        return false;
    }  
    
    //Tác vụ khách hàng
    //Đăng ký
    public static NhanVien dangKy(int idChiNhanh, Scanner scanner) {
        while (true) {
            System.out.println("\n=== Đăng Ký ===");
            System.out.print("Tên nhân viên: ");
            String tenNV = scanner.nextLine();
            if (tenNV.isEmpty()) {
                System.out.println("Lỗi: Tên nhân viên không được để trống!");
                return null;
            }
    
            System.out.print("Số điện thoại: ");
            String sdt = scanner.nextLine();
            if (!sdt.matches("\\d{10}")) {
                System.out.println("Lỗi: Số điện thoại không hợp lệ!");
                return null;
            }
    
            if (kiemTraTonTaiSDT(sdt)) {
                System.out.println("Số điện thoại đã tồn tại!");
                System.out.println("1. Nhập số điện thoại khác");
                System.out.println("2. Quên mật khẩu");
                System.out.print("Chọn: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                if (choice == 2) {
                    System.out.println("Chức năng đang phát triển...");
                    return null;
                }
            } else {
                System.out.print("Email: ");
                String email = scanner.nextLine();
    
                // Nhập mật khẩu và xác nhận lại
                String matKhau;
                while (true) {
                    System.out.print("Mật khẩu: ");
                    matKhau = scanner.nextLine();
                    System.out.print("Nhập lại mật khẩu: ");
                    String nhapLai = scanner.nextLine();
    
                    if (!matKhau.equals(nhapLai)) {
                        System.out.println("Mật khẩu không khớp. Vui lòng nhập lại!");
                    } else if (matKhau.isEmpty()) {
                        System.out.println("Mật khẩu không được để trống!");
                    } else {
                        break;
                    }
                }
    
                String hashedMatKhau = hashPassword(matKhau);
    
                String sql = "INSERT INTO nhanvien (ID_ChiNhanh,TenNV ,ChucVu ,SDT , Email, MatKhau) VALUES (?, ?, 'NHAN_VIEN', ?, ?, ?)";
                try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1,idChiNhanh);
                    stmt.setString(2, tenNV);
                    stmt.setString(3, sdt);
                    stmt.setString(4, email);
                    stmt.setString(5, hashedMatKhau);
                    stmt.executeUpdate();
    
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            int idNV = rs.getInt(1);
                            System.out.println("Đăng ký thành công! ID của bạn là: " + idNV);
                            currentNV =new NhanVien(idNV, idChiNhanh, tenNV, NhanVien.Role.NHAN_VIEN, sdt, email, hashedMatKhau);
                            return currentNV;
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi khi Đăng Ký!");
                    e.printStackTrace();
                }
            }
        }
    }
    

    //Đăng nhập
    private static NhanVien currentNV = null;
    public static NhanVien dangNhap(Scanner scanner) {
        System.out.println("\n=== Đăng Nhập ===");
        String sdt;
        do {
            System.out.print("SĐT: ");
            sdt = scanner.nextLine().trim();
            if (sdt.isEmpty()) {
                System.out.println("Số điện thoại không được để trống!");
            }
        } while (sdt.isEmpty());
        String matKhau;
        do {
            System.out.print("Mật Khẩu: ");
            matKhau = scanner.nextLine().trim();
            if (matKhau.isEmpty()) {
                System.out.println("Mật khẩu không được để trống!");
            }
        } while (matKhau.isEmpty());
        String hashedPassword = hashPassword(matKhau);

        String sql = "SELECT * FROM nhanvien WHERE SDT = ? AND MatKhau = ? ";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sdt);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idNV = rs.getInt("ID_NhanVien");
                    int idChiNhanh = rs.getInt("ID_ChiNhanh");
                    String tenNV = rs.getString("TenNV"); 
                    String email = rs.getString("Email");
                    Role chucVu = Role.valueOf(rs.getString("ChucVu"));
                    System.out.println(" Đăng nhập thành công! Chào " + tenNV);
                    System.out.println(" Nhân viên ở chi nhánh " + idChiNhanh);
                    
                    currentNV = new NhanVien(idNV, idChiNhanh, tenNV, chucVu, sdt, email, hashedPassword); 
                    return currentNV;
                } else {
                    System.out.println("Sai số điện thoại hoặc mật khẩu!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
        return null; 
        }

    public static NhanVien getCurrentNV() {
        return currentNV;
    }

    //Đăng xuất
    public static void dangXuat(){
        if(currentNV !=null){
            System.out.println("Tạm biệt, " + currentNV.getTenNV() + "!");
            currentNV = null;
        }else {
            System.out.println("Bạn chưa đăng nhập!");
        }

    }

    //Tìm món
    public static void timMon(Scanner scanner){
        while (true) {            
            System.out.println("\n=== TÌM MÓN ĂN ===");
            System.out.println("1. Xem menu");
            System.out.println("2. Tìm món ăn");
            System.out.println("0. THOÁT");
            System.out.print("Chọn chức năng: ");

            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    MonAnServices.menuXemMonAn(scanner);
                    break;
                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    break;
               
                case 0:                            
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  


    //Check bàn
     public static void checkBan(NhanVien currentNV, int idChiNhanh, Scanner scanner){
        while (true) {
            System.out.println("\n=== CHECK BÀN ===");
            System.out.println("1. Lọc danh sách bàn");
            System.out.println("2. Danh sách bàn");
            System.out.println("0. Thoát");
            System.out.print("Chọn chức năng: ");

            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    BanAnServices.locBanAn(currentNV,idChiNhanh, scanner);
                    break;
                case 2:
                    BanAnServices.xemBan(idChiNhanh);
                    break;
                case 0:
                    QuanLyBanAn.quanLy(currentNV, idChiNhanh, scanner);            
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  

    //Danh sách NhanVien
    public static List<NhanVien> xemDanhSachNV(int idChiNhanh){
        List<NhanVien> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE ID_ChiNhanh = ? AND ChucVu = 'NHAN_VIEN'";
       
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1,idChiNhanh);
                try(ResultSet rs = stmt.executeQuery()){
                    int stt = 1;
                    System.out.println("=============================== DANH SÁCH NHÂN VIÊN ================================");
                    System.out.println("=====================================================================================");
                    System.out.printf("| %-3s | %-5s | %-5s | %-20s | %-12s | %-20s | %-10s |\n",
                                      "STT","ID", "IDCN", "Tên", "SDT", "Email", "Mật khẩu");
                    while (rs.next()) {
                        int id = rs.getInt("ID_NhanVien");
                        String tenNV = rs.getString("TenNV");
                        String sdt = rs.getString("SDT");
                        String email = rs.getString("Email");
        
                        danhSach.add(new NhanVien(id, idChiNhanh, tenNV, Role.NHAN_VIEN, sdt, email, "******"));
                        System.out.println("-------------------------------------------------------------------------------------");
                        System.out.printf("| %-3d | %-5d | %-5d | %-20s | %-12s | %-20s | %-10s |\n",
                                          stt++, id, idChiNhanh, tenNV, sdt, email, "******");
                    }
            
                    System.out.println("=====================================================================================");
                }
          
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách nhân viên!");
            e.printStackTrace();
        }
        return danhSach;
    }
    
    
    //Danh sách User
    public static List<User> xemDanhSachUser(){
        List<User> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM user";
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            
            int stt = 1;
            System.out.println("=================================================================================== DANH SÁCH USER ==========================================================================================");
            System.out.println("============================================================================================================================================================================================");

            System.out.printf("| %-3s | %-5s | %-20s | %-12s | %-25s | %-10s ||| %-3s | %-5s | %-20s | %-12s | %-25s | %-10s |\n",
                "STT","ID", "Tên", "SDT", "Email", "Hạng",
                "STT","ID", "Tên", "SDT", "Email", "Hạng");
    
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
    
            List<String> rowBuffer = new ArrayList<>();
    
            while (rs.next()) {
                int id = rs.getInt("ID_User");
                String tenUser = rs.getString("TenUser");
                String sdt = rs.getString("SDT");
                String email = rs.getString("Email");
                Roles role = Roles.valueOf(rs.getString("Role"));
    
                danhSach.add(new User(id, tenUser, sdt, email, "", role));
    
                String userStr = String.format("| %-3d | %-5d | %-20s | %-12s | %-25s | %-10s |",
                        stt++, id, tenUser.length() > 20 ? tenUser.substring(0, 17)+"...":tenUser, sdt, email, role);
                rowBuffer.add(userStr);
    
                if (rowBuffer.size() == 2) {
                    System.out.println(rowBuffer.get(0) + "|" + rowBuffer.get(1));
                    rowBuffer.clear();
                }
            }
    
            // Nếu còn 1 bản ghi lẻ, in riêng
            if (!rowBuffer.isEmpty()) {
                System.out.println(rowBuffer.get(0));
            }
    
            System.out.println("============================================================================================================================================================================================");
    
        } catch(SQLException e){
            System.out.println("Lỗi khi lấy danh sách khách hàng!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
    


    // //Kiểm tra Admin
    public static boolean ktAdmin(int idNV){
        String sql = "SELECT * FROM nhanvien WHERE ID_NhanVien = ? AND ChucVu = 'QUAN_LY'";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idNV);
                try(ResultSet rs = stmt.executeQuery()){
                    return rs.next();
                }
                
            }catch (SQLException e) {
                System.out.println("Lỗi kiểm tra Admin!");
                e.printStackTrace();
            }
            return false;
    }
}
