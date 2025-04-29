package userinterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

import Main.Main;
import background.DatBanChecker;
import connection.DatabaseConnection;
import model.DatBan;
import service.DatBanServices;
import model.DonHang;
import service.DonHangServices;
import service.GoiMonServices;
import service.MonAnServices;
import model.User;
import service.UserServices;

public class DichVuKhachHang {
    public static void dichVu(User currentUser) {
    if (currentUser == null) {
        System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
        return;
    }

    Scanner scanner = new Scanner(System.in);

    while (true) {
        System.out.println("\n=== DỊCH VỤ KHÁCH HÀNG ===");
        System.out.println("1. Gọi món tại nhà hàng");
        System.out.println("2. Đặt món mang về");
        System.out.println("3. Đặt bàn");
        System.out.println("4. Xem thực đơn");
        System.out.println("5. Sửa thông tin cá nhân");
        System.out.println("0. Đăng xuất");
        System.out.print("Chọn chức năng: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
            scanner.next();
            continue;
        }

        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1: 
                DatBan currentDatBan = daXacNhanDatBan(currentUser.getID_User());
                if (currentDatBan != null ) { 
                    if(DatBanChecker.daNhanBan(currentUser.getID_User())){
                        DonHang donHang = layDonHangHienTai(currentUser.getID_User());
                        if (donHang == null) {
                            donHang = DonHangServices.themDonHang(currentUser,DonHang.KieuDonHang.TAI_QUAN, scanner );
                        }
                        if (donHang != null) {
                            donHang.setKieuDonHang(DonHang.KieuDonHang.TAI_QUAN);
                            GoiMonServices.goiMon(currentUser, donHang, scanner);
                        } else {
                            System.out.println("Không thể tạo hoặc lấy đơn hàng.");
                        }
                    }else {
                        System.out.println("Bạn cần NHẬN BÀN  trước khi gọi món tại nhà hàng.");
                    }   
                
                } else {
                    System.out.println("Bạn cần ĐẶT BÀN trước khi gọi món tại quán.");
                }                  
             
                break;

            case 2: 
                // DonHang donMangVe = DonHangServices.taoDonHangMangVe(currentUser); 
                // if (donMangVe != null) {
                //     donMangVe.setKieuDonHang(ChiTietDonHang.KieuDonHang.MANG_VE);
                //     DonHangServices.goiMon(donMangVe, currentUser);
                // }
                // break;
            case 3: 
                DatBan datBan = daDatBan(currentUser.getID_User());
                if (datBan != null){                  
                    System.out.println("Bạn đã đặt bàn rồi!");
                }else{
                    DatBanServices.datBan(currentUser);
                }
                break;
            case 4:
                timMon(currentUser);
                break;
            case 5:
                UserServices.suaThongTinCaNhan(scanner, currentUser.getID_User(), currentUser);
                break;
            case 0:
                UserServices.dangXuat();
                TaiKhoanKhachHang.taiKhoanKhachHang();
                return;

            default:
                System.out.println("Lựa chọn không hợp lệ!");
            }
        }
    }

    //Lấy bàn đã đặt
    public static DatBan layDatBan(int idUser, String dieuKienWhere) {
        String tenUser = layTenUser(idUser);
        String sql = "SELECT * FROM datban WHERE ID_User = ? ";
        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()){
            sql += " AND " + dieuKienWhere;
        }
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    int id_DatBan = rs.getInt("ID_DatBan");
                    int id_ChiNhanh = rs.getInt("ID_ChiNhanh");
                    String id_BanAn = rs.getString("List_BanAn");
                    LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                    LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
    
                    String trangThaiStr = rs.getString("TrangThai");
                    DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(trangThaiStr);
                    hienThiThongTin(id_DatBan,idUser,tenUser, id_BanAn, ngayDat, ngayAn, trangThai);
                    return new DatBan(id_DatBan, id_ChiNhanh, idUser, id_BanAn, ngayDat, ngayAn , trangThai);
                }
            }            

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
    private static void hienThiThongTin(int idDatBan,int idUser,String tenUser, String idBanAn, LocalDateTime ngayDat, LocalDateTime ngayAn, DatBan.TrangThai trangThai) {
        System.out.println("ID Đặt bàn: "+idDatBan+ " | ID: " + idUser +" | Tên KH: "+tenUser+" | Bàn: " + idBanAn + " | Ngày đặt: " +ngayDat+" | Ngày ăn: "+ ngayAn + " | Trạng thái: " + trangThai);
    }

    public static DatBan daXacNhanDatBan(int idUser){
        return layDatBan(idUser, "TrangThai = 'DA_XAC_NHAN'");
    }

    public static DatBan daDatBan(int idUser){
        return layDatBan(idUser, "TrangThai IN ('DA_XAC_NHAN', 'CHO_XAC_NHAN')");
    }

    //Lấy tên
    public static String layTenUser(int idUser) {
        String sql = "SELECT TenUser FROM user WHERE ID_User = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TenUser");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy tên người dùng!");
            e.printStackTrace();
        }
        return null;
    }

    //Lấy đơn hiện tại
    public static DonHang layDonHangHienTai(int idUser) {
        String sql = "SELECT * FROM donhang WHERE ID_User = ? AND TrangThai = 'DANG_CHUAN_BI'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idUser);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    int idDonHang = rs.getInt("ID_DonHang");
                    return new DonHang(idDonHang, idUser, DonHang.TrangThai.DANG_CHUAN_BI);
                }
            }    
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Tìm món
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
}
