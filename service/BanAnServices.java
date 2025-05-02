package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import model.BanAn;
import model.BanAn.TrangThai;
import model.NhanVien;
import connection.DatabaseConnection;


public class BanAnServices {
    //Thêm bàn ăn
    public static BanAn themBanAn(NhanVien currentNV,int idChiNhanh, Scanner scanner){
        System.out.println("\n==== Thêm bàn mới ====");
        scanner.nextLine();
        System.out.print("Số ghế: ");     
        int soGhe = scanner.nextInt();
        scanner.nextLine();       
        
        String sql = "INSERT INTO banan (ID_ChiNhanh, Soghe, TrangThai) VALUES (?,?,'TRONG')";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
                stmt.setInt(1, idChiNhanh);
                stmt.setInt(2,soGhe);
                stmt.executeUpdate();
                try(ResultSet rs = stmt.getGeneratedKeys()){
                    if (rs.next()) {
                        int idBA = rs.getInt(1);
                        System.out.println("Thêm bàn ăn thành công! Mã bàn: " + idBA);
                        return new BanAn(idBA, idChiNhanh , soGhe, BanAn.TrangThai.TRONG);
                    }
                }      
            }catch (SQLException e){
                System.out.println("Lỗi khi thêm bàn!");
                e.printStackTrace();
            }
            return null;
    }

    //Sửa thông tin bàn
    public static BanAn suaBanAn(NhanVien currentNV,int idChiNhanh, Scanner scanner){
        xemBan(idChiNhanh);
        System.out.println("Nhập ID bàn ăn cần sửa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ!");
            scanner.next();
            return null;
        }
        int idBA = scanner.nextInt();
        scanner.nextLine();

        String sql = "SELECT * FROM banan WHERE ID_BanAn = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idBA);
                try(ResultSet rs = stmt.executeQuery()){
                    if(!rs.next()){
                        System.out.println("Không có bàn có ID: " + idBA );
                        return null;
                    }
    
                    int soGhe = rs.getInt("Soghe");
                    String trangThai = rs.getString("TrangThai");
    
                    System.out.println("Các thông tin có thể sửa:");
                    System.out.println("1. Số ghế: " + soGhe);
                    System.out.println("2. Trạng thái: " + trangThai);
                    System.out.println("Chọn mục cần sửa: ");
                    while (!scanner.hasNextInt()) {
                        System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                        scanner.next(); 
                        System.out.print("Chọn mục cần sửa: "); 
                    }
        
                    int choice = scanner.nextInt();
                    scanner.nextLine();
    
                    String field = null;
                    switch (choice){
                        case 1:
                        field = "Soghe";
                        break;
                        case 2:
                        field = "TrangThai";
                        break;
                        default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
                    }
                    System.out.println("Nhập giá trị mới: ");
                    Object newValue;
    
                    if(choice == 1){
                        if (scanner.hasNextInt()){
                            newValue = scanner.nextInt();
                            scanner.nextLine();
                        }else{
                            System.out.println("Lỗi: Giá trị phải nhập số nguyên hợp lệ!");
                            scanner.next();
                            return null;
                        }
                    }else{
                        newValue = scanner.nextLine();
                    }
    
                    String sqlUpdate = "UPDATE banan SET " + field + " = ? WHERE ID_BanAn = ?";
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
                        stmtUpdate.setObject(1, newValue);
                        stmtUpdate.setInt(2, idBA);
                        stmtUpdate.executeUpdate();
    
                        switch(choice){
                            case 1:
                            soGhe = (int) newValue;
                            break;
                            case 2: 
                            while(true){
                                trangThai = (String) newValue;
                                if(trangThai.equals("TRONG")|| trangThai.equals("DA_DAT") ||trangThai.equals("DANG_SU_DUNG")){
                                    break;
                                }
                                System.out.println("Lỗi");
                                break;
                            }break;
                            default:
                            System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
                        }
                        System.out.println("Cập hật thành công!");
                    }
                    catch (SQLException e){
                        System.out.println("Lỗi cập nhật!");
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e){
                System.out.println("Lỗi kết nối cơ sở dữ liệu!");
                e.printStackTrace();
            }
            return null;
    }

    //Xóa bàn 
    public static void xoaBanAn(NhanVien currentNV,int idChiNhanh,Scanner scanner){
        xemBan(idChiNhanh);
        System.out.println("Nhập ID bàn ăn cần xóa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ");
            scanner.next();
            return;
        }

        int idBA = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM banan WHERE ID_BanAn = ? ";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idBA);
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected>0){
                    System.out.println("Xóa bàn thành công!");
                }else{
                    System.out.println("Không tìm thấy món ăn có ID " + idBA);
                }
            }catch (SQLException e){
                System.out.println("Lỗi khi xóa món ăn");
                e.printStackTrace();
            }
    }

    //Danh Sách Bàn ăn
    public static List<BanAn> xemDanhSachBan(int idChiNhanh, String tieuDe, String dieuKienWhere){
        List<BanAn> danhSach = new ArrayList<>();
        
        String sql = "SELECT * FROM banan WHERE ID_ChiNhanh = ?";

        if(dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()){
            sql += " AND "+ dieuKienWhere;
        }
        
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1,idChiNhanh);
            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("\n======== " +tieuDe.toUpperCase() +" ========");
                System.out.println("=================================");
                System.out.printf("| %-5s | %-7s | %-7s | %-10s | \n", "ID", "ID CN", "Số ghế", "Trạng thái");
                System.out.println("=================================");
                
                while(rs.next()){
                    int id = rs.getInt("ID_BanAn");
                    int idCN = rs.getInt("ID_ChiNhanh");
                    int soGhe = rs.getInt("Soghe");
                    TrangThai trangThai = TrangThai.valueOf(rs.getString("TrangThai"));
                    BanAn banAn = new BanAn(id, idCN,soGhe, trangThai);
                    danhSach.add(banAn);
                    System.out.printf("| %-5d | %-7s|  %-7s | %-10s | \n", id, idCN, soGhe,trangThai);
                }
                System.out.println("=================================");
            }
        }catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách món ăn!");
            e.printStackTrace();
        }
        return danhSach;
    }

    //Xem danh sách bàn
    public static List<BanAn> xemBan(int idChiNhanh){
        return xemDanhSachBan(idChiNhanh, "Danh sách bàn", null);
    }

    public static List<BanAn> xemBanTrong(int idChiNhanh){
        return xemDanhSachBan(idChiNhanh, "Danh sách bàn trống"," TrangThai = 'TRONG'");
    }
    
    //LocBanAn
    public static void locBanAn(NhanVien currentNV, int idChiNhanh, Scanner scanner) {    
        System.out.println("Bàn tại chi nhánh ID: " + idChiNhanh);
        while (true) {
            System.out.println("\n======CHECK======");
            System.out.println("1. Bàn trống");
            System.out.println("2. Bàn đã đặt");
            System.out.println("3. Bàn đang sử dụng");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");

            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                scanner.next(); 
                continue;
            }

            switch(choice){
                case 1: 
                xemDanhSachBan(idChiNhanh, "Danh sách bàn trống", " TrangThai = 'TRONG'");
                break;
                case 2:
                xemDanhSachBan(idChiNhanh, "Danh sách bàn đã đặt",  "TrangThai = 'DA_DAT'");
                break;
                case 3:
                xemDanhSachBan(idChiNhanh, "Bàn đang sử dụng","TrangThai = 'DANG_SU_DUNG'");
                break;
                case 0:
                NhanVienServices.checkBan(currentNV, idChiNhanh, scanner);
                return;
                default:
                System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại");
            }
        }     
    }
    
    //Chọn chi nhánh
    public static int chonChiNhanh(Scanner scanner) {
        while (true) {
            String sqlChiNhanh = "SELECT * FROM chinhanh";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlChiNhanh);
                ResultSet rs = stmt.executeQuery()) {

                Set<Integer> danhSachID = new HashSet<>();

                System.out.println("\n=========== CHỌN CHI NHÁNH ==========");
                System.out.println("=====================================");
                System.out.println("| ID    | Tên chi nhánh             |");
                System.out.println("=====================================");

                while (rs.next()) {
                    int idChiNhanh = rs.getInt("ID_ChiNhanh");
                    String tenChiNhanh = rs.getString("TenCN");
                    System.out.printf("| %-5d | %-25s |\n", idChiNhanh, tenChiNhanh);
                    danhSachID.add(idChiNhanh);
                }

                System.out.println("=====================================");
                System.out.print("Nhập ID chi nhánh bạn muốn chọn: (0 để thoát): ");

                if (scanner.hasNextInt()) {
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    if(id ==0){
                        return 0;
                    }
                    if (danhSachID.contains(id)) {
                        return id; 
                    } else {
                        System.out.println("ID chi nhánh không tồn tại. Vui lòng chọn lại.");
                    }
                } else {
                    System.out.println("Vui lòng nhập số hợp lệ.");
                    scanner.next(); 
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi lấy danh sách chi nhánh!");
                e.printStackTrace();
                return -1;
            }
        }
    }

}
