package service;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connection.DatabaseConnection;
import Main.Main;
import model.MonAn.DanhMucMonAn;
import model.MonAn.LoaiMonAn;
import model.MonAn;
import model.User;
import userinterface.DichVuKhachHang;


public class MonAnServices {
    //Thêm món ăn
    public static MonAn themMonAn(Scanner scanner){
        System.out.println("\n=== Thêm món ăn mới ===");
        System.out.print("Tên món ăn: ");
        String tenMon = scanner.nextLine();
        if (tenMon.isEmpty()) {
            System.out.println("Lỗi: Tên khách hàng không được để trống!");
            return null;
        }
        System.out.print("Giá: ");
        int gia = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Mô tả: ");
        String moTa = scanner.nextLine();

        LoaiMonAn loaiMonAn = null;
        while (true) {
            System.out.print("Loại món ăn (DO_AN / DO_UONG): ");
            String loaiMonAnInput = scanner.nextLine();
            if (loaiMonAnInput.equals("DO_AN")) {
                loaiMonAn = LoaiMonAn.DO_AN;
                break;
            } else if (loaiMonAnInput.equals("DO_UONG")) {
                loaiMonAn = LoaiMonAn.DO_UONG;
                break;
            } else {
                System.out.println("Lỗi: Chỉ được nhập 'DO_AN' hoặc 'DO_UONG'. Hãy nhập lại!");
            }
        }
    
        DanhMucMonAn danhMuc = null;
        while (true) {
            if (loaiMonAn == LoaiMonAn.DO_AN) {
                System.out.print("Danh mục (DO_KHO / DO_NUOC): ");
            } else {
                System.out.print("Danh mục (CO_CON / KHONG_CON): ");
            }
            String danhMucInput = scanner.nextLine();
            
            if (loaiMonAn == LoaiMonAn.DO_AN) {
                if (danhMucInput.equals("DO_KHO") || danhMucInput.equals("DO_NUOC")) {
                    danhMuc = DanhMucMonAn.valueOf(danhMucInput);
                    break;
                }
            } else if (loaiMonAn == LoaiMonAn.DO_UONG) {
                if (danhMucInput.equals("CO_CON") || danhMucInput.equals("KHONG_CON")) {
                    danhMuc = DanhMucMonAn.valueOf(danhMucInput);
                    break;
                }
            }
            
            System.out.println("Lỗi: Danh mục không hợp lệ. Hãy nhập lại!");
        }
    

        String sql = "INSERT INTO monan (TenMon, Gia, Mota, LoaiMonAn, DanhMuc) VALUES (?, ?, ?, ?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
                stmt.setString(1, tenMon);
                stmt.setInt(2, gia);
                stmt.setString(3,moTa);
                stmt.setString(4, loaiMonAn.name());
                stmt.setString(5, danhMuc.name());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()){
                    int idMA = generatedKeys.getInt(1);
                    System.out.println("Thêm món ăn thành công!");
                    return new MonAn(idMA, tenMon, gia, moTa, loaiMonAn, danhMuc);
                }

            }catch (SQLException e) {
                System.out.println("Lỗi khi thêm món ăn!");
                e.printStackTrace();
                }
                return null;        
            }
    
    // Xóa thông tin món ăn
    public static void xoaMonAn(Scanner scanner){
        System.out.println("Nhập ID món ăn cần xóa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ");
            scanner.next();
            return;
        }

        int idMA = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM monan WHERE ID_MonAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idMA);
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected >0){
                    System.out.println("Xóa món ăn thành công");
                }else{
                    System.out.println("Không tìm thấy món ăn có ID "+ idMA);
                }
            }catch (SQLException e){
                System.out.println("Lỗi khi xóa món ăn");
                e.printStackTrace();
            }
    }

    //Sửa món ăn
    public static MonAn suaMonAn(Scanner scanner){
        System.out.print("Nhập ID món ăn cần sửa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ!");
            scanner.next();
            return null;
        }

        int idMA = scanner.nextInt();
        scanner.nextLine();
        
        String sql = "SELECT * FROM monan WHERE ID_MonAn = ?";
        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idMA);
                ResultSet rs = stmt.executeQuery();

                if(!rs.next()){
                    System.out.println("Không có món ăn có ID: "+ idMA);
                    return null;
                }

                String tenMon = rs.getString("TenMon");
                int gia = rs.getInt("Gia");
                String moTa = rs.getString("Mota");
                String loaiMonAn = rs.getString("LoaiMonAn");
                String danhMuc= rs.getString("DanhMuc");
               
                System.out.println("Các thông tin có thể sửa ");
                System.out.println("1. Tên món: "+ tenMon);
                System.out.println("2. Giá: "+ gia);
                System.out.println("3. Mô tả: " + moTa); 
                System.out.println("4. Loại món ăn: " + loaiMonAn);
                System.out.println("5. Loại: " + danhMuc);
                
                System.out.print("Chọn mục cần sửa:");
                
                int choice = scanner.nextInt();
                scanner.nextLine();
                
                String field = null;
                switch (choice){
                    case 1:
                    field = "TenMon"; 
                    break;
                    case 2:
                    field = "Gia"; 
                    break;
                    case 3:
                    field = "Mota";
                    break; 
                    case 4:
                    field = "LoaiMonAn";
                    break; 
                    case 5:
                    field = "DanhMuc"; 
                    break;
                    default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
                }

                System.out.print("Nhập giá trị mới: ");
                Object newValue;

                if (choice == 2) {                     
                    if (scanner.hasNextInt()) {
                        newValue = scanner.nextInt();
                        scanner.nextLine(); 
                    } else {
                        System.out.println("Lỗi: Giá phải là số nguyên hợp lệ!");
                        scanner.next(); 
                        return null;
                    }
                } else { 
                    newValue = scanner.nextLine();
                }
            
                String sqlUpdate = "UPDATE monan SET " + field +" = ? WHERE ID_MonAn = ?";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
                    stmtUpdate.setObject(1,newValue);
                    stmtUpdate.setInt(2, idMA);
                    stmtUpdate.executeUpdate();

                    switch(choice){
                        case 1:
                            tenMon = (String) newValue;
                            break;
                        case 2:
                            gia = (int) newValue;
                            break;
                        case 3:
                            moTa = (String) newValue;
                            break;
                        case 4:
                        while(true){
                            loaiMonAn = (String) newValue;
                            if (loaiMonAn.equals("DoAn") || loaiMonAn.equals("DoUong")) {
                                break;
                            }
                            System.out.println("Lỗi: Chỉ được nhập 'DoAn' hoặc 'DoUong'. Hãy nhập lại!");
                            break;
                        }break;
                        case 5:
                        while (true){
                            if (loaiMonAn.equals("DoAn")) {
                                System.out.print("Danh mục (Do_Kho / Do_Nuoc): ");
                            } else {
                                System.out.print("Danh mục (Co_Con / Khong_Con): ");
                            }
                            danhMuc = (String) newValue;
                            if ((loaiMonAn.equals("DoAn") && (danhMuc.equals("Do_Kho") || danhMuc.equals("Do_Nuoc"))) ||
                            (loaiMonAn.equals("DoUong") && (danhMuc.equals("Co_Con") || danhMuc.equals("Khong_Con")))) {
                            break;
                            }
                            System.out.println("Lỗi: Danh mục không hợp lệ. Hãy nhập lại!");
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
            catch (SQLException e) {
                System.out.println("Lỗi kết nối cơ sở dữ liệu!");
                e.printStackTrace();
            }
            return null;
    }

    //Menu
    public static List<MonAn> xemMenu(){        
        List<MonAn> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM monan";        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
                System.out.println("\n================================================ MENU ==================================================");
                System.out.println("==========================================================================================================");
                System.out.printf("| %-5s | %-20s | %-12s | %-25s | %-12s | %-10s |\n", "ID", "Tên Món", "Giá", "Mô tả","Loại món", "Loại");
                System.out.println("==========================================================================================================");
             
                while (rs.next()){
                    int id = rs.getInt("ID_MonAn");
                    String tenMon = rs.getString("TenMon");
                    int gia = rs.getInt("Gia");
                    String moTa = rs.getString("Mota");
                    LoaiMonAn loaiMonAn = LoaiMonAn.valueOf(rs.getString("LoaiMonAn"));
                    DanhMucMonAn danhMuc = DanhMucMonAn.valueOf(rs.getString("DanhMuc"));
                    MonAn monAn = new MonAn(id, tenMon, gia, moTa, loaiMonAn, danhMuc);
                    danhSach.add(monAn);
                    System.out.printf("| %-5d | %-20s | %-12s | %25s | %-12s | %-10s |\n", id, tenMon, gia, moTa, loaiMonAn,danhMuc);
                }
                System.out.println("==========================================================================================================");
                
            }catch (SQLException e) {
                System.out.println("Lỗi khi lấy danh sách món ăn!");
                e.printStackTrace();
            }
            return danhSach;
    }

    //Tìm kiếm món ăn
    public static List<MonAn> timKiemMonAn(Scanner scanner) {
        List<MonAn> danhSach = new ArrayList<>();
        while(true){
            System.out.println("\n======= TÌM KIẾM =======");
            System.out.println("1. Tìm theo loại món ăn");
            System.out.println("2. Tìm theo danh mục món ăn");
            System.out.print("Chọn: ");
            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }
            int luaChon = scanner.nextInt();
            scanner.nextLine();
            String sql = "";
            String filterValue = "";
        
            if (luaChon == 1) { 
                System.out.println("\n======= TÌM MÓN ĂN =======");
                System.out.println("1. Đồ ăn");
                System.out.println("2. Đồ uống");
                System.out.print("Chọn (0 để thoát): ");
                int choice = scanner.nextInt();
                scanner.nextLine();
        
                switch (choice) {
                    case 1:
                        filterValue = "DO_AN";
                        break;
                    case 2:
                        filterValue = "DO_UONG";
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Lỗi! Vui lòng nhập lại.");
                        return danhSach;
                    
                }    
                sql = "SELECT * FROM monan WHERE LoaiMonAn = ?";
        
            } else if (luaChon == 2) { 
                System.out.println("\n======= TÌM THEO DANH MỤC =======");
                System.out.println("1. Đồ khô");
                System.out.println("2. Đồ nước");
                System.out.println("3. Đồ uống có cồn");
                System.out.println("4. Đồ uống không cồn");
                System.out.print("Chọn (0 để thoát): ");
                int subchoice = scanner.nextInt();
                scanner.nextLine();
        
                switch (subchoice) {
                    case 1:
                        filterValue = "DO_KHO";
                        break;
                    case 2:
                        filterValue = "DO_NUOC";
                        break;
                    case 3:
                        filterValue = "CO_CON";
                        break;
                    case 4:
                        filterValue = "KHONG_CON";
                        break;
                    case 0:
                        break;
                    default:
                        System.out.println("Lỗi! Vui lòng nhập lại.");
                        return danhSach;
                }
        
                sql = "SELECT * FROM monan WHERE DanhMuc = ?";
            } else {
                System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập lại.");
                return danhSach;
            }
        
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
        
                stmt.setString(1, filterValue);
        
                try (ResultSet rs = stmt.executeQuery()) {
                    System.out.println("\n=============================== DANH SÁCH MÓN ĂN ===============================");
                    System.out.printf("| %-5s | %-20s | %-10s | %-25s |\n", "ID", "Tên Món", "Giá", "Mô tả");
                    System.out.println("================================================================================");
        
                    while (rs.next()) {
                        int id = rs.getInt("ID_MonAn");
                        String tenMon = rs.getString("TenMon");
                        int gia = rs.getInt("Gia");
                        String moTa = rs.getString("Mota");
        
                        System.out.printf("| %-5d | %-20s | %-10d | %-25s |\n", id, tenMon, gia, moTa);
                    }
                    System.out.println("================================================================================");
        
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi lấy danh sách món ăn!");
                e.printStackTrace();
            }    
            return danhSach;       
        }    
    }

    public static void timMon(User currentUser) {
        Scanner scanner = new Scanner(System.in);
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
                    DichVuKhachHang.dichVu(currentUser);                    
                    System.out.println("Bạn chưa đăng nhập!");
                    Main.main(new String [] {});                              
                    scanner.close();
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  

    //Kiểm tra món có tồn tại k
    public static boolean kiemTraMonAnTonTai(int idMonAn) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM monan WHERE ID_MonAn = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idMonAn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //Lấy giá
    public static int layGiaMonAn(int idMonAn) {
        String sql = "SELECT Gia FROM monan WHERE ID_MonAn = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idMonAn);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt("Gia");
            } else {
                System.out.println("Không tìm thấy món ăn với ID: " + idMonAn);
            }
    
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy giá món ăn từ cơ sở dữ liệu:");
            e.printStackTrace();
        }
    
        return 0; 
    }
}

     
    
    