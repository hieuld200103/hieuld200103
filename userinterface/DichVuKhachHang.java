package userinterface;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

import Main.Main;
import background.DatBanChecker;
import connection.DatabaseConnection;
import model.DatBan;
import service.BanAnServices;
import service.DatBanServices;
import model.DonHang;
import service.DonHangServices;
import service.GoiMonServices;
import service.KhuyenMaiServices;
import service.MonAnServices;
import service.TrangThaiDonHang;
import model.User;
import service.UserServices;


public class DichVuKhachHang {
    public static void dichVu(User currentUser, Scanner scanner) {
    if (currentUser == null) {
        System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
        return;
    }

    while (true) {
        String [] menu = {
            "DỊCH VỤ KHÁCH HÀNG",
            "1. Gọi món tại nhà hàng",
            "2. Đặt món mang về",
            "3. Đặt bàn",
            "4. Xem thực đơn",
            "5. Sửa thông tin cá nhân",
            "6. Kiểm tra trạng thái đơn hàng",
            "7. Xem chương trình khuyến mãi",
            "8. Thanh toán hóa đơn",
            "0. Đăng xuất",
        };
        System.out.println("\n=== " + menu[0] + " ===");
            for (int i = 1; i < menu.length; i++) {
                System.out.println(menu[i]);
            }
        System.out.println("Chọn chức năng: ");
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
                if (currentDatBan != null) { 
                    if (DatBanChecker.daNhanBan(currentUser.getID_User())) {
                        int idChiNhanh = currentDatBan.getID_ChiNhanh();
                        DonHang donHang = layDHTaiNhaHang(currentUser.getID_User());
                        if (donHang == null) {
                            donHang = DonHangServices.themDonHangTaiNhaHang(currentUser, idChiNhanh, DonHang.KieuDonHang.TAI_NHA_HANG, scanner);
                        }
                        if (donHang != null) {
                            donHang.setKieuDonHang(DonHang.KieuDonHang.TAI_NHA_HANG);
                            boolean daGoiMon = GoiMonServices.goiMon(currentUser,idChiNhanh, donHang, scanner);
                            if (!daGoiMon) {
                                xoaDH(donHang.getID_DonHang());
                            }
                        } else {
                            System.out.println("Không thể tạo hoặc lấy đơn hàng.");
                        }
                    } else {
                        System.out.println("Bạn cần NHẬN BÀN trước khi gọi món tại nhà hàng.");
                    }
                } else {
                    System.out.println("Bạn cần ĐẶT BÀN trước khi gọi món tại quán.");
                }
                break;

            case 2:
                int idChiNhanh = BanAnServices.chonChiNhanh(scanner);
                if (idChiNhanh == 0) {
                    break; 
                }
                DonHang donHang = DonHangServices.themDonHangMangVe(currentUser, idChiNhanh, DonHang.KieuDonHang.MANG_VE, scanner);
                if (donHang != null) {
                    boolean daGoiMon = GoiMonServices.goiMon(currentUser, idChiNhanh, donHang, scanner);
                    if (!daGoiMon) {
                        xoaDH(donHang.getID_DonHang());
                    }
                } else {
                    System.out.println("Không thể tạo đơn hàng mang về.");
                }
                DichVuKhachHang.dichVu(currentUser, scanner);
                break;
            case 3: 
                DatBan datBan = daDatBan(currentUser.getID_User());
                if (datBan != null){                  
                    System.out.println("\nBạn đã đặt bàn rồi!");
                    qlHuyDatBan(scanner, currentUser);
                }else{
                    DatBanServices.datBan(currentUser, scanner);
                }
                break;
            case 4:
                timMon(currentUser, scanner);
                break;
            case 5:
                UserServices.suaThongTinCaNhan(scanner, currentUser.getID_User(), currentUser);
                break;
            case 6: 
                TrangThaiDonHang.kiemTraDonHangKH(scanner,currentUser);
                break;
            case 7:
                KhuyenMaiServices.xemKhuyenMai2(currentUser, scanner);
                break;
            case 8:
                QuanLyThanhToanHoaDon.thanhToan(currentUser);
                break;
            case 0:
                UserServices.dangXuat();
                TaiKhoanKhachHang.taiKhoanKhachHang(scanner);
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
        System.out.println("\nID Đặt bàn: "+idDatBan+ " | ID: " + idUser +" | Tên KH: "+tenUser+" | Bàn: " + idBanAn + " | Ngày đặt: " +ngayDat+" | Ngày ăn: "+ ngayAn + " | Trạng thái: " + trangThai);
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

    //Lấy đơn ăn tại nhà hàng
    public static DonHang layDHTaiNhaHang(int idUser) {
        String sql = "SELECT * FROM donhang WHERE ID_User = ? AND TrangThai = 'DANG_CHUAN_BI' AND kieudonhang = 'TAI_NHA_HANG' ";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idUser);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    int idDonHang = rs.getInt("ID_DonHang");
                    return new DonHang(idDonHang, idUser, DonHang.KieuDonHang.TAI_NHA_HANG);
                }
            }    
        
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Xóa đơn mang về nếu k gọi món
    public static void xoaDH(int idDH) {
        String sql = "SELECT COUNT(*) FROM chitietdonhang WHERE ID_DonHang = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDH);
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next() && rs.getInt(1) == 0) {
                    String deleteSQL = "DELETE FROM donhang WHERE ID_DonHang = ?";
                    try (PreparedStatement deletePs = conn.prepareStatement(deleteSQL)){
                        deletePs.setInt(1, idDH);
                        deletePs.executeUpdate();
                        System.out.println("Đã xóa đơn khi không gọi món!");
                    }                    
                }
            }    
        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa đơn!");
            e.printStackTrace();
        }
    }
    
    //Tìm món
    public static void timMon(User currentUser, Scanner scanner) {
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
                    DichVuKhachHang.dichVu(currentUser, scanner);                    
                    System.out.println("Bạn chưa đăng nhập!");
                    Main.main(new String [] {});                              
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    } 
    

    //Thao tác hủy đặt bàn
    public static void qlHuyDatBan(Scanner scanner, User currentUser){
        while (true) {
            System.out.print("\nBạn muốn thay đổi thời gian (Y/n)?: ");
            String s = scanner.nextLine();
            if (s.equalsIgnoreCase("y") || s.equalsIgnoreCase("yes")) {
                System.out.print("\nBạn cần hủy lịch hiện tại để đặt lịch mới (Y/n)?: ");
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("yes")) { 
                    String trangThai = layTrangThaiDatBan(currentUser.getID_User());
                    switch (trangThai){
                        case "CHO_XAC_NHAN":
                            huyDatBan(currentUser);
                        break;
                        case "DA_XAC_NHAN":
                            if(!thoiGianHuyBan(currentUser.getID_User())){
                                System.out.println("Khách hàng chỉ được hủy trước thời gian dùng bữa 12 tiếng! \nXin liên hệ hostline nhà hàng để được hỗ trợ!");
                                
                            }else{
                                huyDatBan(currentUser);
                            }
                            break;
                    }                  
                    return;
                } else {
                    DichVuKhachHang.dichVu(currentUser, scanner);
                    return;
                }
            } else {
                DichVuKhachHang.dichVu(currentUser, scanner);
                    return;
            }
        }
    }

    //Hủy đặt bàn
    public static void huyDatBan(User currentUser){
        String sql = "UPDATE datban SET TrangThai = 'DA_HUY' WHERE ID_User = ? AND TrangThai IN ('CHO_XAC_NHAN','DA_XAC_NHAN')";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,currentUser.getID_User());
                stmt.executeUpdate();
                System.out.println("\nBạn đã hủy lịch đặt bàn hiện tại!");
            } catch (SQLException e) {
                System.out.println("Lỗi khi hủy lịch đặt bàn!");
                e.printStackTrace();
            }  
    }

    //Lấy trạng thái đặt bàn (qlHuyDatBan)
    public static String layTrangThaiDatBan(int idUser) {
        String sql = "SELECT TrangThai FROM datban WHERE ID_User = ? AND TrangThai IN ('DA_XAC_NHAN', 'CHO_XAC_NHAN')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("TrangThai");
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra trạng thái đặt bàn!");
            e.printStackTrace();
        }
        return "";
    }

    //Thời gian có thể hủy đặt bàn
    public static boolean thoiGianHuyBan(int idUser){
        String sql = "SELECT * FROM datban WHERE ID_User = ? AND TrangThai = 'DA_XAC_NHAN'";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idUser);
                try(ResultSet rs = stmt.executeQuery()){
                    if(rs.next()){
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime gioAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                        long tgCoTheDat = Duration.between(now,gioAn).toHours();

                        if(tgCoTheDat>12){
                            return true;
                        }else{
                            return false;
                        }
                    }
                }
            }catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra thời gian!");
            e.printStackTrace();
            }
            return false; 
    }
}
