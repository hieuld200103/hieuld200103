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

import Main.Main;
import connection.DatabaseConnection;
import model.NhanVien;
import model.User;
import model.User.Role;
import userinterface.CongViecNhanVien;
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

                    System.out.println(" Đăng nhập thành công! Chào " + tenNV);
                    System.out.println(" Nhân viên ở chi nhánh " + idChiNhanh);
                    
                    currentNV = new NhanVien(idNV, idChiNhanh, tenNV, "NHAN_VIEN", sdt, email, hashedPassword); 
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
    public static void timMon(NhanVien currentNV, int idChiNhanh,Scanner scanner){
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
                    MonAnServices.xemMenu();
                    break;
                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    break;
               
                case 0: 
                    CongViecNhanVien.congViec(currentNV, idChiNhanh,scanner);                    
                    System.out.println("Bạn chưa đăng nhập!");
                    Main.main(new String [] {});                              
                    scanner.close();
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  


    //Check bàn
     public static void checkBan(NhanVien currentNV, Scanner scanner){
        int idChiNhanh = currentNV.getID_ChiNhanh();
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
                    BanAnServices.locBanAn(currentNV, scanner);
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
    
    //Danh sách User
    public static List<User> xemDanhSachUser(){
        List<User> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM user";
       
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
                System.out.println("========================= DANH SÁCH USER =========================");
                System.out.println("=====================================================================================");
                System.out.printf("| %-5s | %-20s | %-12s | %-25s | %-10s |\n",
                                  "ID", "Tên", "SDT", "Email", "Hạng");
                System.out.println("=====================================================================================");
                
                while (rs.next()) {
                    int id = rs.getInt("ID_User");
                    String tenUser = rs.getString("TenUser");
                    String sdt = rs.getString("SDT");
                    String email = rs.getString("Email");
                    Role role = Role.valueOf(rs.getString("Role"));
        
                    danhSach.add(new User(id, tenUser, sdt, email, "", role));
        
                    System.out.printf("| %-5d | %-20s | %-12s | %-25s | %-10s |\n",id, tenUser, sdt, email, role);
                }               
                System.out.println("=====================================================================================");
            }catch(SQLException e){
                System.out.println("Lỗi khi lấy danh sách khách hàng!");
                e.printStackTrace();
            }
            return danhSach;
    }

     //Xoa user
    // public static void xoaUser(Scanner scanner){
    //     System.out.println("Nhập ID user cần xóa: ");
    //     if (!scanner.hasNextInt()){
    //         System.out.println("Lỗi ID không hợp lệ!");
    //         scanner.next();
    //         return;
    //     }

    //     int idUser = scanner.nextInt();
    //     scanner.nextLine();

    //     String sql = "DELETE FROM user WHERE ID_User = ?";
    //     try (Connection conn = DatabaseConnection.getConnection();
    //         PreparedStatement stmt = conn.prepareStatement(sql)){
    //             stmt.setInt(1,idUser);
    //             int rowsAffected = stmt.executeUpdate();
    //             if(rowsAffected >0){
    //                 System.out.println("Xóa User thành công!");
    //             }else{
    //                 System.out.println("Không tìm thấy User có ID "+idUser);
    //             }
    //         }catch(SQLException e){
    //             System.out.println("Lỗi khi xóa User!");
    //             e.printStackTrace();
    //         }
    // }
    
    // //Sửa hạng User
    // public static void suaUser(Scanner scanner) {
    //     System.out.println("Nhập ID User cần sửa: ");
    //     if (!scanner.hasNextInt()) {
    //         System.out.println("Lỗi: ID không hợp lệ!");
    //         scanner.next(); 
    //         return;
    //     }
    //     int idUser = scanner.nextInt();
    //     scanner.nextLine();
    
    //     String sql = "SELECT * FROM user WHERE ID_User = ?";
    //     try (Connection conn = DatabaseConnection.getConnection();
    //          PreparedStatement stmtCheck = conn.prepareStatement(sql)) {
    //         stmtCheck.setInt(1, idUser);
    //         ResultSet rs = stmtCheck.executeQuery();
    
    //         if (!rs.next()) {
    //             System.out.println("Không tìm thấy User có ID: " + idUser);
    //             return;
    //         }

    //         String tenUser = rs.getString("TenUser");
    //         String role = rs.getString("ROLE");
    //         System.out.println("Tên User: "+ tenUser + " Hạng hiện tại: "+ role);
            
    //         System.out.println("\n === Hạng Thành Viên === ");
    //         System.out.println("1. ==== SLIVER === ");
    //         System.out.println("2. ===== GOLD ==== ");
    //         System.out.println("3. === DIAMOND === ");
    //         System.out.println("Khách hàng đã đạt hạng:  ");
            
           
    //         String newValue = null;
    //         int choice = scanner.nextInt();
    //         scanner.nextLine();

    //         if (!scanner.hasNextInt()) {
    //             System.out.println("Lỗi: ID không hợp lệ!");
    //             scanner.next(); 
    //             return;
    //         }
    //         switch(choice){
    //             case 1:
    //                 newValue = "SLIVER";
    //                 break;
    //             case 2:
    //                 newValue = "GOLD";
    //                 break;
    //             case 3:
    //                 newValue = "DIAMOND";
    //                 break;
    //             default:
    //             System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
    //         }
    //         String sqlUpdate = "UPDATE user SET Role = ? WHERE ID_User = ?";
    //         try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
    //             stmtUpdate.setString(1, newValue);
    //             stmtUpdate.setInt(2, idUser);
    //             stmtUpdate.executeUpdate();
    //             System.out.println("Cập nhật thành công!");
    //         }
    //         rs.close();
    
    //     } catch (SQLException e) {
    //         System.out.println("Lỗi cập nhật!");
    //         e.printStackTrace();
    //     }
    // }
}
