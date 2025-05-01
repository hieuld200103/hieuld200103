package service;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import connection.DatabaseConnection;
import model.KhuyenMai;
import model.NhanVien;
import model.User;
import userinterface.DichVuKhachHang;
import userinterface.QuanLyKhuyenMai;
import java.sql.Date;
public class KhuyenMaiServices {
    //Thêm khuyến mãi
    public static void themKhuyenMai(Scanner scanner){
        System.out.println("\n=== Thêm khuyến mãi mới ===");

        String tenChuongTrinh;
        while(true){
            System.out.print("Tên chương trình khuyến mãi: ");
            tenChuongTrinh = scanner.nextLine();
            if(!tenChuongTrinh.isEmpty()){
                break;
            }
            System.out.println("Lỗi: Tên chương trình khuyến mãi không được để trống! Vui lòng nhập lại");
        }
        double phanTramKM;
        while (true) {
            System.out.print("Phần trăm giảm giá: ");
            String input = scanner.next().trim();
        
            if (input.isEmpty()) {
                System.out.println("Lỗi: Không được để trống! Vui lòng nhập lại");
                continue;
            }
        
            try {
                phanTramKM = Double.parseDouble(input);
                if (phanTramKM > 0) {
                    break;
                } else {
                    System.out.println("Lỗi: Phần trăm giảm giá phải lớn hơn 0!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Lỗi: Vui lòng nhập một số hợp lệ!");
            }
        }
        scanner.nextLine();
        System.out.print("Ngày bắt đầu(YYYY-MM-DD): ");
        String ngayBatDau = scanner.nextLine();
        System.out.print("Ngày kết thúc(YYYY-MM-DD): ");
        String ngayKetThuc = scanner.nextLine();
        String sql = " INSERT INTO khuyenmai (TenChuongTrinhKM, PhanTramKM, NgayBatDau, NgayKetThuc) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, tenChuongTrinh);
            stmt.setDouble(2, phanTramKM);
            stmt.setDate(3, Date.valueOf(ngayBatDau));
            stmt.setDate(4, Date.valueOf(ngayKetThuc));
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                System.out.println("Thêm khuyến mãi thành công!" );
            }   
        } catch (Exception e) {
            System.out.println("Lỗi khi thêm khuyến mãi!" );
            e.printStackTrace();
        }
    }
    // Xóa khuyến mãi
    public static void xoaKhuyenMai(Scanner scanner){
        System.out.print("Nhập ID khuyến mãi cần xóa: ");
        if(!scanner.hasNextInt()){
            System.out.println( "Lỗi: ID không hợp lệ!");
            scanner.next();
            return;
        }
        int idKM = scanner.nextInt();
        scanner.nextLine();
        String sql = "DELETE FROM khuyenmai WHERE ID_KhuyenMai=?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql) ){
        stmt.setInt(1,idKM);
        int rows = stmt.executeUpdate();
        if(rows>0){
            System.out.println("Xóa khuyến mãi thành công!");
        }else{
            System.out.println("Không tìm thấy khuyến mãi có ID này!");
        }
       } 
       catch(SQLException e){
        System.out.println("Lỗi khi xóa khuyến mãi!");
        e.printStackTrace();
       }
    }
// Sửa thông tin khuyến mãi
    public static void suaKhuyenMai(Scanner scanner){
        System.out.print("Nhập ID khuyến mãi cần sửa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ");
            scanner.next();
        }
        int idKM = scanner.nextInt();
        scanner.nextLine();
        String sql = "SELECT * FROM khuyenmai WHERE ID_KhuyenMai=?";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){
        stmt.setInt(1,idKM);
        ResultSet rs = stmt.executeQuery();
        if ((!rs.next())) {
            System.out.println("Không có món ăn có ID" + idKM);
        }
        String tenChuongTrinhKM = rs.getString("TenChuongTrinhKM");
        double phanTramKM = rs.getDouble("PhanTramKM");
        Date ngayBatDau = rs.getDate("NgayBatDau");
        Date ngayKetThuc = rs.getDate("NgayKetThuc");
        System.out.println("===========Các thông tin có thể sửa============");
        System.out.println("1. Tên chương trình khuyến mãi: "+tenChuongTrinhKM);
        System.out.println("2. Phần trăm giảm giá: "+ phanTramKM);
        System.out.println("3. Ngày bắt đầu: "+ ngayBatDau);
        System.out.println("4. Ngày kết thúc: "+ ngayKetThuc);
        System.out.print("Chọn mục cần sửa: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        String field = null;
        switch (choice){
            case 1: 
            field = "TenChuongTrinhKM";
            break;
            case 2:
            field = "PhanTramKM";
            break;
            case 3:
            field = "NgayBatDau";
            break;
            case 4:
            field ="NgayKetThuc";
            break;
            default:
            System.out.println("Lựa chọn không hợp lệ vui lòng nhập lại!");
        }
        System.out.print("Nhập giá trị mới: ");
        Object newValue=null;
        switch (choice) {
            case 1: 
                newValue = scanner.nextLine();
                tenChuongTrinhKM = (String) newValue;
                break;
            case 2: 
                newValue = scanner.nextDouble();
                phanTramKM = (double) newValue;
                break;
            case 3: 
                String newvalue = scanner.nextLine();
                newValue= Date.valueOf(newvalue); 
            break;
            case 4:
                String newsubvalue = scanner.nextLine();
                newValue = Date.valueOf(newsubvalue);
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ!");
                return;
        }
        String sqlUpdate = "UPDATE khuyenmai SET " + field + " = ? WHERE ID_KhuyenMai = ?";
        try(PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
            stmtUpdate.setObject(1, newValue);
            stmtUpdate.setInt(2,idKM);
            stmtUpdate.executeUpdate();
        System.out.println("Cập nhật thành công");
        }
        catch(SQLException e){
            System.out.println("Lỗi khi sửa khuyến mãi");
            e.printStackTrace();
        }
    }
        catch(SQLException e){
            System.out.println("Lỗi kết nối");
            e.printStackTrace();
     }
    }
    // Xem khuyến mãi
    public static List<KhuyenMai> xemKhuyenMai(NhanVien currentNV,int idChiNhanh){
        List<KhuyenMai> danhSach = new ArrayList<>();
        String sql = " SELECT * FROM khuyenmai";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            System.out.println("\n============================================ Khuyến mãi ======================================");
            System.out.println("============================================================================================");
            System.out.printf("| %-5s | %-30s | %-10s | %-15s | %-15s |\n", "ID", "Tên CT Khuyến Mãi", "Giảm (%)", "Ngày Bắt Đầu", "Ngày Kết Thúc");
            System.out.println("============================================================================================");
            while(rs.next()){
                int id = rs.getInt("ID_KhuyenMai");
                String tenCT = rs.getString("TenChuongTrinhKM");
                double giamGia = rs.getDouble("PhanTramKM");
                Date ngayBDau = rs.getDate("NgayBatDau");
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd"); 
                String ngayBDStr = date.format(ngayBDau);
                Date ngayKThuc = rs.getDate("NgayKetThuc");
            
                String ngayKTStr = date.format(ngayKThuc);
            KhuyenMai km = new KhuyenMai(id, tenCT, giamGia, ngayBDau ,ngayKThuc);
            danhSach.add(km);
            System.out.printf("| %-5d | %-30s | %-10.2f | %-15s | %-15s |\n", id, tenCT,giamGia,ngayBDStr,ngayKTStr);
            System.out.println(" -------------------------------------------------------------------------------------------");
            QuanLyKhuyenMai.quanLy(currentNV, idChiNhanh);
            }
            
        }
        catch(SQLException e){
            System.out.println("lỗi khi lấy danh sách khuyến mãi");
            e.printStackTrace();
        }
        return danhSach;
        
    }
    public static List<KhuyenMai> xemKhuyenMai2(User currentUser,Scanner scanner){
        List<KhuyenMai> danhSach = new ArrayList<>();
        String sql = " SELECT * FROM khuyenmai";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery()){
            System.out.println("\n============================================ Khuyến mãi ======================================");
            System.out.println("============================================================================================");
            System.out.printf("| %-5s | %-30s | %-10s | %-15s | %-15s |\n", "ID", "Tên CT Khuyến Mãi", "Giảm (%)", "Ngày Bắt Đầu", "Ngày Kết Thúc");
            System.out.println("============================================================================================");
            while(rs.next()){
                int id = rs.getInt("ID_KhuyenMai");
                String tenCT = rs.getString("TenChuongTrinhKM");
                double giamGia = rs.getDouble("PhanTramKM");
                Date ngayBDau = rs.getDate("NgayBatDau");
                SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd"); 
                String ngayBDStr = date.format(ngayBDau);
                Date ngayKThuc = rs.getDate("NgayKetThuc");
            
                String ngayKTStr = date.format(ngayKThuc);
            KhuyenMai km = new KhuyenMai(id, tenCT, giamGia, ngayBDau ,ngayKThuc);
            danhSach.add(km);
            System.out.printf("| %-5d | %-30s | %-10.2f | %-15s | %-15s |\n", id, tenCT,giamGia,ngayBDStr,ngayKTStr);
            System.out.println(" -------------------------------------------------------------------------------------------");
            DichVuKhachHang.dichVu(currentUser, scanner);
            }
            
        }
        catch(SQLException e){
            System.out.println("lỗi khi lấy danh sách khuyến mãi");
            e.printStackTrace();
        }
        return danhSach;
        
    }
}

