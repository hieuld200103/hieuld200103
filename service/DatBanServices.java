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
import java.util.stream.Collectors;

import connection.DatabaseConnection;
import model.DatBan;
import model.NhanVien;
import model.User;
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

            BanAnServices.xemBanTrong(idChiNhanh);

            System.out.println("Nhập danh sách bàn bạn chọn (cách nhau bằng dấu phẩy hoặc dấu cách, 0 để thoát)");
            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return null;
            }

            String[] idStrings = input.split("[,\\s]+");
            List<Integer> listIDBan = new ArrayList<>();
            for( String idList : idStrings ){
                try{
                    int id = Integer.parseInt(idList);
                    listIDBan.add(id);
                }catch (NumberFormatException e){
                    System.out.println("ID không hợp lệ: "+idList +". Bỏ qua ID này.");
                }
            }
            
            if (listIDBan.isEmpty()) {
                System.out.println("Không có ID hợp lệ để cập nhật!");
                return null;
            }
    
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

            boolean coBanBiTrung = false;
            boolean banKhongTrong = false;
            boolean saiChiNhanh =false;
            for( int idBan : listIDBan){
                String sqlCheckBan = "SELECT TrangThai FROM banan WHERE ID_BanAn = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sqlCheckBan)) {
                    stmt.setInt(1, idBan);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        String trangThai = rs.getString("TrangThai");
                        if (!trangThai.equals("TRONG")) {
                            System.out.println("Bàn này hiện không trống. Vui lòng chọn bàn khác!");
                            banKhongTrong = true;
                        }
                    } else {
                        System.out.println("Không tìm thấy bàn này!");
                        banKhongTrong = true;
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi khi kiểm tra trạng thái bàn");
                    e.printStackTrace();
                }

                String sqlCheckDatBan = 
                "SELECT * FROM datban " +
                "WHERE ID_USER = ? AND FIND_IN_SET(?, List_BanAn) > 0 " +
                "AND TrangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN') " +
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
                        System.out.println("Bàn "+ idBan + " đã được đặt trong khoảng thời gian này. Vui lòng chọn bàn hoặc thời gian khác.");
                        coBanBiTrung = true;
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi khi kiểm tra lịch đặt bàn.");
                    e.printStackTrace();
                }

                String sqlCheckChiNhanh = "SELECT * FROM banan WHERE ID_BanAn = ? AND ID_ChiNhanh = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlCheckChiNhanh)) {
                    stmt.setInt(1,idBan);
                    stmt.setInt(2, idChiNhanh);
                    ResultSet rs = stmt.executeQuery();
            
                    if (!rs.next()) {
                        System.out.println("Bàn "+ idBan + " không tồn tại. Vui lòng chọn bàn hoặc thời gian khác.");
                        saiChiNhanh = true;
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi khi kiểm tra lịch đặt bàn.");
                    e.printStackTrace();
                }
                
            }
            if (coBanBiTrung || banKhongTrong || saiChiNhanh) {
                return null;
            }

            String idBanAnChuoi = listIDBan.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(","));
            
            String sql = "INSERT INTO datban (ID_ChiNhanh, ID_User, List_BanAn, NgayDat, TrangThai) VALUES (?, ?, ?, ?, 'CHO_XAC_NHAN')";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, idChiNhanh);
                stmt.setInt(2, userID);
                stmt.setString(3, idBanAnChuoi);
                stmt.setTimestamp(4, Timestamp.valueOf(thoiGianAn));
                int rowInserted = stmt.executeUpdate();
                if (rowInserted > 0) {
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int idDatBan = generatedKeys.getInt(1);
                            System.out.println("Đặt bàn thành công! ID_DatBan: " + idDatBan);
                            return new DatBan(idDatBan, idChiNhanh, userID, idBanAnChuoi, thoiGianAn, DatBan.TrangThai.CHO_XAC_NHAN);
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
        int idChiNhanh = currentNV.getID_ChiNhanh();
        String sql = "SELECT COUNT(*) AS soDon FROM datban WHERE ID_ChiNhanh = ? AND TrangThai = 'CHO_XAC_NHAN'"; 
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idChiNhanh);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    int soDon = rs.getInt("soDon");
                    if(soDon>0){
                        System.out.println("!!!Có "+soDon+" đơn hàng đang chờ xác nhận!!!");
                    }else{
                        System.out.println("Không có đơn đặt bàn nào đang chờ!");
                    }
                }
            
            } catch (SQLException e) {
                System.out.println("Lỗi khi kiểm tra đơn đặt bàn chờ xác nhận.");
                e.printStackTrace();
            }
    }

    //Danh sách Đặt bàn
    public static List<DatBan> xemDanhSachDatBan(NhanVien currentNV, String tieuDe, String dieuKienWhere) {
        List<DatBan> danhSach = new ArrayList<>();
        int idCN = currentNV.getID_ChiNhanh();
        String sql = "SELECT * FROM datban WHERE ID_ChiNhanh = ?";
        
        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()) {
            sql += " AND " + dieuKienWhere;
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,idCN);
            ResultSet rs = stmt.executeQuery();
    
            System.out.println("\n=============================== " + tieuDe.toUpperCase() + " ==============================");
            System.out.println("================================================================================");
            System.out.printf("| %-5s | %-7s | %-8s | %-7s | %-20s | %-15s |\n", "ID", "ID_CN", "ID_User", "ID_Bàn", "Ngày đặt", "Trạng thái");
            System.out.println("================================================================================");
    
            while (rs.next()) {
                int id = rs.getInt("ID_DatBan");
                int idUser = rs.getInt("ID_User");
                String idBanAn = rs.getString("List_BanAn");
                LocalDateTime thoiGianAn = rs.getTimestamp("NgayDat").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, thoiGianAn, trangThai);
                danhSach.add(datBan);
    
                System.out.printf("| %-5d | %-7d | %-8d | %-7s | %-20s | %-15s |\n",
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
    
    
    public static List<DatBan> xemDanhSachDatBan(NhanVien currentNV) {
        return xemDanhSachDatBan(currentNV, "Danh sách đặt bàn", null);
    }
    
    public static List<DatBan> xemDSChoXacNhan(NhanVien currentNV) {
        return xemDanhSachDatBan(currentNV,"Danh sách chờ xác nhận", "TrangThai = 'CHO_XAC_NHAN'");
    }
    
    public static List<DatBan> xemDSCoTheHuy(NhanVien currentNV) {
        return xemDanhSachDatBan(currentNV, "Danh sách có thể huỷ", "TrangThai ='CHO_XAC_NHAN' OR TrangThai = 'DA_XAC_NHAN'");
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
                String idBanAn = rs.getString("List_BanAn");
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
    public static void locDanhSachDatBan(NhanVien currentNV, Scanner scanner) {
        while (true) {
            int idChiNhanh = currentNV.getID_ChiNhanh();
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
                    xemDanhSachDatBan(currentNV, "Danh sách chờ xác nhận", "TrangThai = 'CHO_XAC_NHAN'");
                    break;
                case 2:
                    xemDanhSachDatBan(currentNV, "Danh sách đã xác nhận", "TrangThai = 'DA_XAC_NHAN'");
                    break;
                case 3:
                    xemDanhSachDatBan(currentNV,"Danh sách đã hủy", "TrangThai = 'DA_HUY'");
                    break;
                case 4:
                    xemDanhSachDatBan(currentNV, "Toàn bộ danh sách", null);
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
        DatBan.TrangThai trangThaiMoi,
        Runnable hienThiDS
    ) {
        while(true){
            hienThiDS.run(); 

        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }

        System.out.println("Nhập danh sách ID_DatBan cần cập nhật (cách nhau bằng dấu phẩy hoặc dấu cách, nhập 0 để thoát): ");
        String input = scanner.nextLine().trim();

        if (input.equals("0")) {
            return;
        }

        String[] idStrings = input.split("[,\\s]+"); 
        List<Integer> idList = new ArrayList<>();

        for (String idStr : idStrings) {
            try {
                int id = Integer.parseInt(idStr);
                idList.add(id);
            } catch (NumberFormatException e) {
                System.out.println("ID không hợp lệ: " + idStr + ". Bỏ qua ID này.");
            }
        }

        if (idList.isEmpty()) {
            System.out.println("Không có ID hợp lệ để cập nhật!");
            return;
        }
        int idChiNhanh = currentNV.getID_ChiNhanh();
        for (int idDatBan : idList) {
            String sql = "SELECT * FROM datban WHERE ID_DatBan = ? AND ID_ChiNhanh = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idDatBan);
                stmt.setInt(2,idChiNhanh);
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    System.out.println("Không tìm thấy ID_DatBan: " + idDatBan);
                    continue;
                }

                int idUser = rs.getInt("ID_User");
                String idBanAn = rs.getString("List_BanAn");
                LocalDateTime thoiGianAn = rs.getTimestamp("NgayDat").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));

                System.out.println("Cập nhật đơn:");
                hienThiThongTin(idDatBan, idUser, idBanAn, thoiGianAn, trangThai);

                String sqlUpdate = "UPDATE datban SET TrangThai = ? WHERE ID_DatBan = ?";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setString(1, trangThaiMoi.name());
                    stmtUpdate.setInt(2, idDatBan);
                    stmtUpdate.executeUpdate();
                    System.out.println("Đã cập nhật ID_DatBan: " + idDatBan + " thành trạng thái " + trangThaiMoi);
                }
                rs.close();
            } catch (SQLException e) {
                System.out.println("Lỗi khi cập nhật ID_DatBan: " + idDatBan);
                e.printStackTrace();
            }
        }

        }
    }

    private static void hienThiThongTin(int idDatBan, int idUser, String idBanAn, LocalDateTime thoiGianAn, DatBan.TrangThai trangThai) {
        System.out.println("ID: " + idDatBan + " | idUser: " + idUser +
            " | Bàn: " + idBanAn + " | Ngày đặt: " + thoiGianAn + " | Trạng thái: " + trangThai);
    }

    public static void xacNhanDatBan(Scanner scanner, NhanVien currentNV) {
        capNhatTrangThaiDatBan(scanner, currentNV, DatBan.TrangThai.DA_XAC_NHAN, () -> DatBanServices.xemDSChoXacNhan(currentNV));
    }

    public static void huyDatBan(Scanner scanner, NhanVien currentNV) {
        capNhatTrangThaiDatBan(scanner, currentNV, DatBan.TrangThai.DA_HUY, () -> DatBanServices.xemDSCoTheHuy(currentNV));
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
                String id_BanAn = rs.getString("List_BanAn");
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


    //Check khách đã nhận bàn
    public static boolean daNhanBan(int idUser) {
        String sql = "SELECT * FROM khachhang WHERE ID_User = ? AND TrangThai = 'DA_NHAN_BAN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra trạng thái nhận bàn!");
            e.printStackTrace();
        }
        return false;
    }

}
