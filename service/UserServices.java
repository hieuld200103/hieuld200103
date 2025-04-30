package service;
import java.util.Scanner;

import model.User;
import model.User.Role;
import userinterface.DichVuKhachHang;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connection.DatabaseConnection;


public class UserServices {
    //Mã hóa mật khẩu 
    private static String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = md.digest(password.getBytes());
                StringBuilder sb = new StringBuilder();
                for (byte b : hashedBytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Lỗi mã hóa mật khẩu!");
            }
        }

    // Hàm kiểm tra số điện thoại đã tồn tại chưa
    private static boolean kiemTraTonTaiSDT(String sdt) {
        String sql = "SELECT COUNT(*) FROM user WHERE SDT = ?";
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
    public static User dangKy(Scanner scanner) {
        while (true) {        
            System.out.println("\n=== Đăng Ký ===");
            System.out.print("Tên khách hàng: ");
            String tenUser = scanner.nextLine();
            if (tenUser.isEmpty()) {
                System.out.println("Lỗi: Tên khách hàng không được để trống!");
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
                System.out.print("Mật Khẩu: ");
                String matKhau = scanner.nextLine();
                String hashedMatKhau = hashPassword(matKhau);
                
                String sql = "INSERT INTO user (TenUser, SDT, Email, MatKhau, Role) VALUES (?, ?, ?, ?, 'SLIVER')";
                try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    
                    stmt.setString(1, tenUser);
                    stmt.setString(2, sdt);
                    stmt.setString(3, email);
                    stmt.setString(4, hashedMatKhau);
                    stmt.executeUpdate();

                    try(ResultSet rs = stmt.getGeneratedKeys()){
                        if (rs.next()) {
                            int idUser = rs.getInt(1);
                            System.out.println("Đăng ký thành công! ID của bạn là: " + idUser);
                            return new User(idUser, tenUser, sdt, email, hashedMatKhau, User.Role.SLIVER); 
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
    private static User currentUser = null;
    public static User dangNhap(Scanner scanner) {
        System.out.println("\n=== Đăng Nhập ===");
         // Kiểm tra số điện thoại
        String sdt;
        do {
            System.out.print("SĐT: ");
            sdt = scanner.nextLine().trim();
            if (sdt.isEmpty()) {
                System.out.println("Số điện thoại không được để trống!");
            }
        } while (sdt.isEmpty());

        // Kiểm tra mật khẩu
        String matKhau;
        do {
            System.out.print("Mật Khẩu: ");
            matKhau = scanner.nextLine().trim();
            if (matKhau.isEmpty()) {
                System.out.println("Mật khẩu không được để trống!");
            }
        } while (matKhau.isEmpty());

        String hashedPassword = hashPassword(matKhau);

        String sql = "SELECT * FROM user WHERE SDT = ? AND MatKhau = ? ";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sdt);
            stmt.setString(2, hashedPassword);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idUser = rs.getInt("ID_User");
                    String tenUser = rs.getString("TenUser"); 
                    String email = rs.getString("Email");
                    Role role = Role.valueOf(rs.getString("Role"));

                    System.out.println(" Đăng nhập thành công! Chào " + tenUser);
                    System.out.println(" Hạng của Quý khách: " + role);
                   

                    currentUser =  new User(idUser, tenUser, sdt, email, role); 
                    return currentUser;
                } else {
                    System.out.println("Sai số điện thoại hoặc mật khẩu!");
                    return null; 
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối database!");
            e.printStackTrace();
        }
        return null; 
        }

    //Đăng xuất
    public static void dangXuat(){
        if(currentUser !=null){
            System.out.println("Tạm biệt, " + currentUser.getTenUser() + "!");
            currentUser = null;
        }else {
            System.out.println("Bạn chưa đăng nhập!");
        }
    }


    //Sửa thông tin user
    public static void suaThongTinCaNhan(Scanner scanner, int idUser, User currentUser) {
        if (currentUser == null) {
            System.out.println("Lỗi: Không xác định được người dùng hiện tại!");
            return;
        }
        System.out.println("\n=== CẬP NHẬT THÔNG TIN CÁ NHÂN ===");
        
        String sql = "SELECT * FROM user WHERE ID_User = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtCheck = conn.prepareStatement(sql)) {
            stmtCheck.setInt(1, idUser);
            try(ResultSet rs = stmtCheck.executeQuery()){
                if (!rs.next()) {
                    System.out.println("Không tìm thấy tài khoản!");
                    return;
                }
        
                String tenUser = rs.getString("TenUser");
                String sdt = rs.getString("SDT");
                String email = rs.getString("Email");
                String matKhau = rs.getString("MatKhau");
        
                System.out.println("1. Tên: " + tenUser);
                System.out.println("2. Số điện thoại: " + sdt);
                System.out.println("3. Email: " + email);
                System.out.println("4. Đổi mật khẩu");
                System.out.println("0.Thoát");
                System.out.print("Chọn mục cần sửa: ");
                while (!scanner.hasNextInt()) {
                    System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                    scanner.next(); 
                    System.out.print("Chọn mục cần sửa: "); 
                }
                int choice = scanner.nextInt();
                scanner.nextLine();
                String field = "";
                
                if (choice == 4) { 
                    System.out.print("Nhập mật khẩu cũ: ");
                    String oldPassword = scanner.nextLine();
                    if (!hashPassword(oldPassword).equals(matKhau)) {
                        System.out.println("Mật khẩu cũ không đúng!");
                        return;
                    }
                    field = "MatKhau";
                } else {
                    switch (choice) {
                        case 1: field = "TenUser"; break;
                        case 2: field = "SDT"; break;
                        case 3: field = "Email"; break;
                        case 0: 
                        DichVuKhachHang.dichVu(currentUser,scanner);
                        break;
                        default:
                            System.out.println("Lựa chọn không hợp lệ!");
                            return;
                    }
                }
        
                System.out.print("Nhập mật khẩu mới: ");
                String newValue = scanner.nextLine();
                if (choice == 4) newValue = hashPassword(newValue); 
        
                String sqlUpdate = "UPDATE user SET " + field + " = ? WHERE ID_User = ?";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setString(1, newValue);
                    stmtUpdate.setInt(2, idUser);
                    stmtUpdate.executeUpdate();
                    System.out.println(" Cập nhật thành công!");
                }
            }
        } catch (SQLException e) {
            System.out.println(" Lỗi cập nhật!");
            e.printStackTrace();
        }
    }
}
