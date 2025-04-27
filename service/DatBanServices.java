package service;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connection.DatabaseConnection;
import model.DatBan;
import model.NhanVien;
import model.User;
import model.BanAn;
import userinterface.QuanLyDatBan;


public class DatBanServices {
    //Đặt bàn
    public static DatBan datBan(User currentUser){
        if (currentUser == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return null;
        }
        Scanner scanner = new Scanner(System.in);
        int idChiNhanh = BanAnServices.chonChiNhanh(scanner);
        System.out.println("\n========= ĐẶT BÀN =========");
        int userID = currentUser.getID_User();
        while (true) {
            System.out.println("ID Chi nhánh: " + idChiNhanh);
            
            int soNg = -1;
            while (soNg <= 0) {
                System.out.print("Bạn muốn đặt bàn với bao nhiêu người: ");
                if (scanner.hasNextInt()) {
                    soNg = scanner.nextInt();
                    if (soNg <= 0) {
                        System.out.println("Số người phải lớn hơn 0.");
                    }
                } else {
                    System.out.println("Lỗi: Vui lòng nhập số nguyên hợp lệ!");
                    scanner.next();
                }
            }
            scanner.nextLine();
        
            List<BanAn> danhSachBan = BanAnServices.locDatBan(idChiNhanh, soNg);
            if (danhSachBan.isEmpty()) {
                System.out.println("Không có bàn phù hợp. Vui lòng chọn số ghế khác.");
                return null;
            }
        
            int idBan = -1;
            while (idBan <= 0) {
                System.out.print("Nhập ID bàn cần đặt: ");
                if (scanner.hasNextInt()) {
                    idBan = scanner.nextInt();
                    if (idBan <= 0) {
                        System.out.println("ID bàn phải lớn hơn 0.");
                    }
                } else {
                    System.out.println("Lỗi: Vui lòng nhập số nguyên hợp lệ!");
                    scanner.next();
                }
            }
            scanner.nextLine();
        
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime thoiGianAn = null;
            while (thoiGianAn == null) {
                try {
                    System.out.print("Nhập thời gian bạn muốn ăn (định dạng yyyy-MM-dd HH:mm): ");
                    String thoiGianInput = scanner.nextLine();
                    thoiGianAn = LocalDateTime.parse(thoiGianInput, formatter);
                    System.out.println("Thời gian bạn muốn ăn: " + thoiGianAn);
                } catch (Exception e) {
                    System.out.println("Lỗi: Định dạng ngày giờ không đúng. Vui lòng nhập lại theo định dạng yyyy-MM-dd HH:mm");
                }
            }
        
            String sqlCheckBan = "SELECT TrangThai FROM banan WHERE ID_BanAn = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlCheckBan)) {
                stmt.setInt(1, idBan);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String trangThai = rs.getString("TrangThai");
                    if (!trangThai.equals("TRONG")) {
                        System.out.println("Bàn này hiện không trống. Vui lòng chọn bàn khác!");
                        return null;
                    }
                } else {
                    System.out.println("Không tìm thấy bàn này!");
                    return null;
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi kiểm tra trạng thái bàn");
                e.printStackTrace();
            }
        
            String sqlCheckDatBan = 
                "SELECT * FROM datban " +
                "WHERE ID_USER = ? AND ID_BanAn = ? AND TrangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN') " +
                "AND NgayDat < DATE_ADD(?, INTERVAL 90 MINUTE) " +
                "AND DATE_ADD(NgayDat, INTERVAL 90 MINUTE) > ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sqlCheckDatBan)) {
        
                stmt.setInt(1, userID);
                stmt.setInt(2, idBan);
                stmt.setTimestamp(3, Timestamp.valueOf(thoiGianAn)); 
                stmt.setTimestamp(4, Timestamp.valueOf(thoiGianAn)); 
                ResultSet rs = stmt.executeQuery();
        
                if (rs.next()) {
                    System.out.println("Bàn đã được đặt trong khoảng thời gian này. Vui lòng chọn bàn hoặc thời gian khác.");
                    return null;
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi kiểm tra lịch đặt bàn.");
                e.printStackTrace();
            }
        
            String sql = "INSERT INTO datban (ID_ChiNhanh, ID_User, ID_BanAn, NgayDat, TrangThai) VALUES (?, ?, ?, ?, 'CHO_XAC_NHAN')";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idChiNhanh);
                stmt.setInt(2, userID);
                stmt.setInt(3, idBan);
                stmt.setTimestamp(4, Timestamp.valueOf(thoiGianAn));
                int rowInserted = stmt.executeUpdate();
                if (rowInserted > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idDatBan = generatedKeys.getInt(1);
                            System.out.println("Đặt bàn thành công! ID_DatBan: " + idDatBan);
                            return new DatBan(idDatBan, idChiNhanh, userID, idBan, thoiGianAn, DatBan.TrangThai.CHO_XAC_NHAN);
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi đặt bàn.");
                e.printStackTrace();
            }
            return null;
        }        
    }

    //Thông báo có đơn chờ xác nhận
    public static void thongBao(NhanVien currentNV){
        String sql = "SELECT COUNT(*) AS soDon FROM datban WHERE TrangThai = 'CHO_XAC_NHAN'"; 
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
                if (rs.next()){
                    int soDon = rs.getInt("soDon");
                    if(soDon>0){
                        System.out.println("!!!Có "+soDon+" đơn hàng đang chờ xác nhận!!!");
                    }else{
                        System.out.println("Không có đơn đựt bàn nào đang chờ!");
                    }
                }
            
            } catch (SQLException e) {
                System.out.println("Lỗi khi kiểm tra đơn đặt bàn chờ xác nhận.");
                e.printStackTrace();
            }
    }

    //Danh sách Đặt bàn
    public static List<DatBan> xemDanhSachDatBan(String tieuDe, String dieuKienWhere) {
        List<DatBan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM datban";
        
        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()) {
            sql += " WHERE " + dieuKienWhere;
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            System.out.println("\n=============================== " + tieuDe.toUpperCase() + " ==============================");
            System.out.println("================================================================================");
            System.out.printf("| %-5s | %-7s | %-8s | %-7s | %-20s | %-15s |\n", "ID", "ID_CN", "ID_User", "ID_Bàn", "Ngày đặt", "Trạng thái");
            System.out.println("================================================================================");
    
            while (rs.next()) {
                int id = rs.getInt("ID_DatBan");
                int idCN = rs.getInt("ID_ChiNhanh");
                int idUser = rs.getInt("ID_User");
                int idBanAn = rs.getInt("ID_BanAn");
                LocalDateTime thoiGianAn = rs.getTimestamp("NgayDat").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, thoiGianAn, trangThai);
                danhSach.add(datBan);
    
                System.out.printf("| %-5d | %-7d | %-8d | %-7d | %-20s | %-15s |\n",
                        id, idCN, idUser, idBanAn, thoiGianAn, trangThai);
            }
    
            System.out.println("================================================================================");
            rs.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách đặt bàn!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
    
    
    public static List<DatBan> xemDanhSachDatBan() {
        return xemDanhSachDatBan("Danh sách đặt bàn", null);
    }
    
    public static List<DatBan> xemDSChoXacNhan() {
        return xemDanhSachDatBan("Danh sách chờ xác nhận", "TrangThai = 'CHO_XAC_NHAN'");
    }
    
    public static List<DatBan> xemDSCoTheHuy() {
        return xemDanhSachDatBan("Danh sách có thể huỷ", "TrangThai ='CHO_XAC_NHAN' OR TrangThai = 'DA_XAC_NHAN'");
    }

    //Danh sách để checker
    public static List<DatBan> xemDanhSach() {
        List<DatBan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM datban";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("ID_DatBan");
                int idCN = rs.getInt("ID_ChiNhanh");
                int idUser = rs.getInt("ID_User");
                int idBanAn = rs.getInt("ID_BanAn");
                LocalDateTime thoiGianAn = rs.getTimestamp("NgayDat").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, thoiGianAn, trangThai);
                danhSach.add(datBan);
            
        } 
        rs.close();
        }catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách đặt bàn!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
    
    //Lọc danh sách đặt bàn
    public static void locDanhSachDatBan(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        while (true) {
            System.out.println("\n=== LỌC DANH SÁCH ===");
            System.out.println("1. Chờ xác nhận");
            System.out.println("2. Đã xác nhận");
            System.out.println("3. Đã hủy");
            System.out.println("4. Tất cả danh sách");
            System.out.println("0. Thoát");
            System.out.print("Nhập: ");
    
            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                scanner.next(); 
                continue;
            }
    
            switch (choice) {
                case 1:
                    xemDanhSachDatBan("Danh sách chờ xác nhận", "TrangThai = 'CHO_XAC_NHAN'");
                    break;
                case 2:
                    xemDanhSachDatBan("Danh sách đã xác nhận", "TrangThai = 'DA_XAC_NHAN'");
                    break;
                case 3:
                    xemDanhSachDatBan("Danh sách đã hủy", "TrangThai = 'DA_HUY'");
                    break;
                case 4:
                    xemDanhSachDatBan("Toàn bộ danh sách", null);
                    break;
                case 0:
                    QuanLyDatBan.quanLy(currentNV, idChiNhanh);
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
           
        }
    }
    
    
    //Cập nhận trạng thái đặt bàn
    public static void capNhatTrangThaiDatBan(
        Scanner scanner,
        NhanVien currentNV,
        int idChiNhanh,
        DatBan.TrangThai trangThaiMoi,
        Runnable hienThiDS
    ) {
        while(true){
            hienThiDS.run(); 

        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }

        System.out.println("Nhập ID_DatBan cần cập nhật (Nhập 0 để thoát): ");
        if (!scanner.hasNextInt()) {
            System.out.println("Lỗi: ID không hợp lệ!");
            scanner.next(); 
            return;
        }

        int idDatBan = scanner.nextInt();
        scanner.nextLine();
        
        if (idDatBan == 0) {
            return;
        }

        String sql = "SELECT * FROM datban WHERE ID_DatBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idDatBan);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Không tìm thấy ID_DatBan " + idDatBan);
                return;
            }

            int idCN = rs.getInt("ID_ChiNhanh");
            int idUser = rs.getInt("ID_User");
            int idBanAn = rs.getInt("ID_BanAn");
            LocalDateTime thoiGianAn = rs.getTimestamp("NgayDat").toLocalDateTime();
            DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));

            System.out.println("Trước khi cập nhật:");
            hienThiThongTin(idDatBan, idCN, idUser, idBanAn, thoiGianAn, trangThai);

            String sqlUpdate = "UPDATE datban SET TrangThai = ? WHERE ID_DatBan = ?";
            try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                stmtUpdate.setString(1, trangThaiMoi.name());
                stmtUpdate.setInt(2, idDatBan);
                stmtUpdate.executeUpdate();
                System.out.println("Đã lưu thay đổi!");
                hienThiThongTin(idDatBan, idCN, idUser, idBanAn, thoiGianAn, trangThaiMoi);
            }
            rs.close();
            break;
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật!");
            e.printStackTrace();
        }
        }
    }

    private static void hienThiThongTin(int idDatBan, int idCN, int idUser, int idBanAn, LocalDateTime thoiGianAn, DatBan.TrangThai trangThai) {
        System.out.println("ID: " + idDatBan + " | IDChiNhanh: " + idCN + " | idUser: " + idUser +
            " | Bàn: " + idBanAn + " | Ngày đặt: " + thoiGianAn + " | Trạng thái: " + trangThai);
    }

    public static void xacNhanDatBan(Scanner scanner, NhanVien currentNV, int idChiNhanh) {
        capNhatTrangThaiDatBan(scanner, currentNV, idChiNhanh, DatBan.TrangThai.DA_XAC_NHAN, DatBanServices::xemDSChoXacNhan);
    }

    public static void huyDatBan(Scanner scanner, NhanVien currentNV, int idChiNhanh) {
        capNhatTrangThaiDatBan(scanner, currentNV, idChiNhanh, DatBan.TrangThai.DA_HUY, DatBanServices::xemDSCoTheHuy);
    }
    

    //Cập nhật theo checker
    public static void capNhatTrangThai(int idDatBan, DatBan.TrangThai trangThaiMoi) {
        String sql = "UPDATE datban SET TrangThai = ? WHERE ID_DatBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi.name());
            stmt.setInt(2, idDatBan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật trạng thái đặt bàn.");
            e.printStackTrace();
        }
    }
    
    //Lấy bàn đã đặt
    public static DatBan layDatBan(int idUser) {
        String sql = "SELECT * FROM datban WHERE ID_User = ? AND TrangThai = 'DA_XAC_NHAN'";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id_DatBan = rs.getInt("ID_DatBan");
                int id_ChiNhanh = rs.getInt("ID_ChiNhanh");
                int id_BanAn = rs.getInt("ID_BanAn");
                java.sql.Timestamp ngayDatSQL = rs.getTimestamp("NgayDat");
                LocalDateTime ngayDat = ngayDatSQL.toLocalDateTime();

                String trangThaiStr = rs.getString("TrangThai");
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(trangThaiStr);

                return new DatBan(id_DatBan, id_ChiNhanh, idUser, id_BanAn, ngayDat, trangThai);
            }

        } catch (Exception e) {
            System.out.println("Lỗi khi lấy thông tin đặt bàn: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    
    // public static boolean daNhanBan(int idUser) {
    //     String sql = "SELECT * FROM khachhang WHERE ID_User = ? AND TrangThai = 'DA_NHAN_BAN'";
    //     try (Connection conn = DatabaseConnection.getConnection();
    //          PreparedStatement stmt = conn.prepareStatement(sql)) {
    //         stmt.setInt(1, idUser);
    //         ResultSet rs = stmt.executeQuery();
    //         return rs.next();
    //     } catch (SQLException e) {
    //         System.out.println("Lỗi kiểm tra trạng thái nhận bàn!");
    //         e.printStackTrace();
    //     }
    //     return false;
    // }

}
