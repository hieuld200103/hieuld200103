package service;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import background.DatBanChecker;
import connection.DatabaseConnection;
import model.BanAn;
import model.BanAn.TrangThai;
import model.DatBan;
import model.NhanVien;
import model.User;
import userinterface.QuanLyDatBan;

public class DatBanServices {
    //Đặt bàn
    public static DatBan datBan(User currentUser, Scanner scanner){
        if (currentUser == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return null;
        }
        while (true) {
            System.out.println("\n============== ĐẶT BÀN ==============");
            int idChiNhanh = BanAnServices.chonChiNhanh(scanner);
            if (idChiNhanh == 0) {
                return null; 
            }

            int userID = currentUser.getID_User();
       
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

            LocalDateTime ngayDat = LocalDateTime.now(); 
            DateTimeFormatter dinhDang = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime ngayAn = null;
            while (ngayAn == null) {
                try {
                    System.out.print("Nhập thời gian bạn muốn ăn (định dạng yyyy-MM-dd HH:mm),(0 để thoát): ");
                    String thoiGianInput = scanner.nextLine();
                    if (thoiGianInput.equals("0")) {
                        return null; 
                    }
                    ngayAn = LocalDateTime.parse(thoiGianInput, dinhDang);
                    long tgDatBan = Duration.between(ngayDat, ngayAn).toHours();
                    if(tgDatBan >168){
                        System.out.println("Bạn chỉ được đặt bàn trong 7 ngày tới!!");
                        ngayAn = null;
                    }
                    // else if (tgDatBan<6){
                    //     System.out.println("Bàn cần được đặt trước thời gian ăn 6 tiếng!!");
                    //     ngayAn = null;
                    // }
                    else{
                        System.out.println("Thời gian bạn muốn ăn: " + ngayAn);
                    }
                    
                } catch (Exception e) {
                    System.out.println("Lỗi: Định dạng ngày giờ không đúng. Vui lòng nhập lại theo định dạng yyyy-MM-dd HH:mm");
                }
            }

            xemDSBanTrong(idChiNhanh, ngayAn);

            boolean danhSachBanHopLe = false;
            while(!danhSachBanHopLe){
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
    
                boolean coBanBiTrung = false;
                boolean saiChiNhanh = false;
    
                try (Connection conn = DatabaseConnection.getConnection()) {
    
                    for (int idBan : listIDBan) {
                        if(!kiemTraBanRanh(idBan, ngayAn)){
                            System.out.println("Bàn " + idBan + " đã được đặt trong khoảng thời gian này. Vui lòng chọn bàn khác!");
                            coBanBiTrung = true;
                        }
    
                        // Kiểm tra bàn có thuộc chi nhánh không
                        String sqlCheckChiNhanh = "SELECT * FROM banan WHERE ID_BanAn = ? AND ID_ChiNhanh = ?";
                        try (PreparedStatement checkChiNhanh = conn.prepareStatement(sqlCheckChiNhanh)) {
                            checkChiNhanh.setInt(1, idBan);
                            checkChiNhanh.setInt(2, idChiNhanh);
                            try (ResultSet rs = checkChiNhanh.executeQuery()) {
                                if (!rs.next()) {
                                    System.out.println("Bàn " + idBan + " không tồn tại trong chi nhánh. Vui lòng chọn bàn khác!");
                                    saiChiNhanh = true;
                                }
                            }
                        }
    
                    }
    
                } catch (Exception e) {
                    System.out.println("Lỗi khi kiểm tra lịch đặt bàn.");
                    e.printStackTrace();
                }
    
                if (coBanBiTrung || saiChiNhanh) {
                    continue;
                }
    
    
                String idBanAnChuoi = listIDBan.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
                
                String sql = "INSERT INTO datban (ID_ChiNhanh, ID_User, List_BanAn, NgayDat, NgayAn, TrangThai) VALUES (?, ?, ?, ?, ?, 'CHO_XAC_NHAN')";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, idChiNhanh);
                    stmt.setInt(2, userID);
                    stmt.setString(3, idBanAnChuoi);
                    stmt.setTimestamp(4, Timestamp.valueOf(ngayDat));
                    stmt.setTimestamp(5, Timestamp.valueOf(ngayAn));
                    int rowInserted = stmt.executeUpdate();
                    if (rowInserted > 0) {
                        try (ResultSet rs = stmt.getGeneratedKeys()) {
                            if (rs.next()) {
                                int idDatBan = rs.getInt(1);
                                System.out.println("Đặt bàn thành công! ID_DatBan: " + idDatBan);
                                return new DatBan(idDatBan, idChiNhanh, userID, idBanAnChuoi, ngayDat, ngayAn, DatBan.TrangThai.CHO_XAC_NHAN);
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("Lỗi khi đặt bàn!");
                    e.printStackTrace();
                }

                danhSachBanHopLe = true;
            }
            
            
            return null;
        }        
    }

    //Kiểm tra trạng thái bàn
    public static boolean kiemTraBanRanh(int idBan, LocalDateTime ngayAn) {
        String sql = "SELECT * FROM datban " +
                "WHERE FIND_IN_SET(?, List_BanAn) > 0 " +
                "AND TrangThai IN ('CHO_XAC_NHAN', 'DA_XAC_NHAN') " +
                "AND NgayAn < DATE_ADD(?, INTERVAL 90 MINUTE) " +
                "AND DATE_ADD(NgayAn, INTERVAL 90 MINUTE) > ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idBan);
            stmt.setTimestamp(2, Timestamp.valueOf(ngayAn));
            stmt.setTimestamp(3, Timestamp.valueOf(ngayAn));
            try (ResultSet rs = stmt.executeQuery()) {
                return !rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra bàn!");
            e.printStackTrace();
        }
        return false;
    }

    //Danh sách bàn trống trong thời gian khách đặt
    public static List<BanAn> xemDSBanTrong(int idChiNhanh, LocalDateTime ngayAn){
        List<BanAn> danhSach = new ArrayList<>();
        
        String sql = "SELECT * FROM banan WHERE ID_ChiNhanh = ?";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setInt(1,idChiNhanh);
            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("\n========= DANH SÁCH BÀN CÓ THỂ ĐẶT =========");
                System.out.println("===========================================");
                System.out.printf("| %-5s | %-7s | %-7s | %-10s | \n", "ID", "ID CN", "Số ghế", "Trạng thái");
                System.out.println("===========================================");
                
                while(rs.next()){
                    int id = rs.getInt("ID_BanAn");
                    int idCN = rs.getInt("ID_ChiNhanh");
                    int soGhe = rs.getInt("Soghe");
                    TrangThai trangThai = trangThaiBan(id, ngayAn);
                    BanAn banAn = new BanAn(id, idCN,soGhe, trangThai);
                    danhSach.add(banAn);
                    System.out.printf("| %-5d | %-7s|  %-7s | %-10s | \n", id, idCN, soGhe,trangThai);
                }
                System.out.println("===========================================");
            }
        }catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách món ăn!");
            e.printStackTrace();
        }
        return danhSach;
    }

    //Set trạng thái bàn
    public static BanAn.TrangThai trangThaiBan(int idBan, LocalDateTime thoiGianCanXet){
        if (!kiemTraBanRanh(idBan, thoiGianCanXet)) {
            return TrangThai.valueOf("DA_DAT");
        }
        return TrangThai.valueOf("TRONG");
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
            try(ResultSet rs = stmt.executeQuery()){
                System.out.println("\n===================================== " + tieuDe.toUpperCase() + " ====================================");
                System.out.println("=========================================================================================================");
                System.out.printf("| %-5s | %-7s | %-8s | %-7s | %-20s | %-20s | %-15s |\n", "ID", "ID_CN", "ID_User", "ID_Bàn", "Ngày đặt", "Ngày ăn", "Trạng thái");
                System.out.println("=========================================================================================================");
        
                while (rs.next()) {
                    int id = rs.getInt("ID_DatBan");
                    int idUser = rs.getInt("ID_User");
                    String idBanAn = rs.getString("List_BanAn");
                    LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                    LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                    DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
        
                    DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                    danhSach.add(datBan);
        
                    System.out.printf("| %-5d | %-7d | %-8d | %-7s | %-20s | %-20s | %-15s |\n",
                            id, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                }
        
                System.out.println("=========================================================================================================");
            }

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

    public static List<DatBan> xemDSDaXacNhan(NhanVien currentNV) {
        return xemDanhSachDatBan(currentNV,"Danh sách chờ xác nhận", "TrangThai = 'DA_XAC_NHAN'");
    }
    
    public static List<DatBan> xemDSCoTheHuy(NhanVien currentNV) {
        return xemDanhSachDatBan(currentNV, "Danh sách có thể huỷ", "TrangThai ='CHO_XAC_NHAN' OR TrangThai = 'DA_XAC_NHAN'");
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
                    QuanLyDatBan.quanLy(currentNV, idChiNhanh, scanner);
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
           
        }
    }
    
    //Cập nhận trạng thái đặt bàn
    public static void capNhatTrangThaiDatBan(Scanner scanner, NhanVien currentNV, DatBan.TrangThai trangThaiMoi, Runnable hienThiDS) {
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
        if(input.equals("all")){
            idList = dsIDChoiXacNhan();
        }else{
            for (String idStr : idStrings) {
                try {
                    int id = Integer.parseInt(idStr);
                    idList.add(id);
                } catch (NumberFormatException e) {
                    System.out.println("ID không hợp lệ: " + idStr + ". Bỏ qua ID này.");
                }
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
                try(ResultSet rs = stmt.executeQuery()){
                    if (!rs.next()) {
                        System.out.println("Không tìm thấy ID_DatBan: " + idDatBan);
                        continue;
                    }
    
                    int idUser = rs.getInt("ID_User");
                    String idBanAn = rs.getString("List_BanAn");
                    LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                    LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                    DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                    System.out.println("\nCập nhật đơn:");
                    hienThiThongTin(idDatBan, idChiNhanh, idUser, idBanAn, ngayDat, ngayAn, trangThai);
    
                    String sqlUpdate = "UPDATE datban SET TrangThai = ? WHERE ID_DatBan = ?";
                    try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                        stmtUpdate.setString(1, trangThaiMoi.name());
                        stmtUpdate.setInt(2, idDatBan);
                        stmtUpdate.executeUpdate();
                        System.out.println("Đã cập nhật ID_DatBan: " + idDatBan + " thành trạng thái " + trangThaiMoi);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi cập nhật ID_DatBan: " + idDatBan);
                e.printStackTrace();
            }
        }

        }
    }

    private static void hienThiThongTin(int idDatBan, int idChiNhanh, int idUser, String idBanAn, LocalDateTime ngayDat, LocalDateTime ngayAn, DatBan.TrangThai trangThai) {
        System.out.println("ID: " + idDatBan +  " | CN: "+ idChiNhanh +" | idUser: " + idUser + " | Bàn: " + idBanAn + " | Ngày đặt: " +ngayDat+" | Ngày ăn: "+ ngayAn + " | Trạng thái: " + trangThai);
    }

    public static void xacNhanDatBan(Scanner scanner, NhanVien currentNV) {
        capNhatTrangThaiDatBan(scanner, currentNV, DatBan.TrangThai.DA_XAC_NHAN, () -> DatBanServices.xemDSChoXacNhan(currentNV));
    }

    public static void huyDatBan(Scanner scanner, NhanVien currentNV) {
        capNhatTrangThaiDatBan(scanner, currentNV, DatBan.TrangThai.DA_HUY, () -> DatBanServices.xemDSCoTheHuy(currentNV));
    }

    //Lọc ID đặt bàn
    public static List<Integer> dsIDChoiXacNhan(){
        List<Integer> dsID = new ArrayList<>();
        List<DatBan> dsDatBan = DatBanChecker.xemDSChoXacNhan();
        for(DatBan datBan : dsDatBan){
            int id = datBan.getID_DatBan();
            dsID.add(id);
        }
        return dsID;
    }
    
    //Lấy lich từ sdt
    public static List<DatBan> layDatBanTheoSDT(String sdt) {
        List<DatBan> danhSachDatBan = new ArrayList<>();
        String sql = "SELECT db.* FROM datban db JOIN user u ON db.ID_User = u.ID_User WHERE u.SDT = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setString(1, sdt);
    
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idDB = rs.getInt("ID_DatBan");
                    int idCN = rs.getInt("ID_ChiNhanh");
                    int idUser = rs.getInt("ID_User");
                    String idBanAn = rs.getString("List_BanAn");
                    LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                    LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                    DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                    DatBan datBan = new DatBan(idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                    danhSachDatBan.add(datBan);
    
                    hienThiThongTin(idDB, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy lịch đặt bàn theo SĐT");
            e.printStackTrace();
        }
    
        return danhSachDatBan;
    }
    

    //Tìm lịch đặt bàn từ sdt
    public static void timDatBan(Scanner scanner){
        while(true){
            System.out.println("\n=== TÌM LỊCH ĐẶT BÀN TỪ SDT ===");    
            System.out.print("Nhập số điện thoại đã dùng để đặt bàn (0 để thoát): ");
            String sdt = scanner.nextLine();
    
            if (sdt.equals("0")) return;
            if (!sdt.matches("\\d{10}")) {
                System.out.println("Lỗi: Số điện thoại không hợp lệ!");
                continue;
            } 
            List<DatBan> danhSach = layDatBanTheoSDT(sdt); 

            if (danhSach.isEmpty()) {
                System.out.println("Không tìm thấy lịch đặt bàn nào với số điện thoại này.");
            }
        }
        
    }
}
