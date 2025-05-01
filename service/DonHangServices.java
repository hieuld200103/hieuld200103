package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import connection.DatabaseConnection;
import model.ChiTietDonHang;
import model.DonHang;
import model.User;

public class DonHangServices {
    //Thêm đơn hàng tại nhà hàng
    public static DonHang themDonHangTaiNhaHang(User user, DonHang.KieuDonHang kieuDonHang, Scanner scanner) {
        if (user == null || kieuDonHang == null) {
            System.out.println("Thông tin người dùng hoặc kiểu đơn hàng không hợp lệ.");
            return null;
        }
        System.out.print("\nID bàn của bạn (đại diện 1 bàn): ");
        int idBanAn = scanner.nextInt();
        
        return themDH(user.getID_User(), idBanAn, kieuDonHang);    
        
    }

    //Thêm đơn hàng mang về
    public static DonHang themDonHangMangVe(User user, DonHang.KieuDonHang kieuDonHang, Scanner scanner) {
        if (user == null || kieuDonHang == null) {
            System.out.println("Thông tin người dùng hoặc kiểu đơn hàng không hợp lệ.");
            return null;
        }
        return themDH(user.getID_User(), null, kieuDonHang);

    }


    //Thêm đơn hàng
    public static DonHang themDH(int idUser, Integer idBanAn, DonHang.KieuDonHang kieuDonHang){
        String sql = "INSERT INTO donhang (ID_User, ID_BanAn, TrangThai, KieuDonHang) VALUES (?, ?, 'DANG_CHUAN_BI', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {    
            stmt.setInt(1, idUser);
            if (idBanAn == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, idBanAn);
            }
            stmt.setString(3, kieuDonHang.name());
    
            int row = stmt.executeUpdate();
            if (row > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idDH = rs.getInt(1);
                        DonHang donHang = new DonHang(idDH, idUser, idBanAn, DonHang.TrangThai.DANG_CHUAN_BI, kieuDonHang);
                        donHang.setKieuDonHang(kieuDonHang);
                        System.out.println("\nTạo đơn hàng thành công! Mã đơn: " + idDH);
                        return donHang;
                    }
                }
            }
    
        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo đơn hàng!");
            e.printStackTrace();
        }
        return null;
    }

    //Thêm vào chi tiết đơn hàng
    public static ChiTietDonHang themChiTietDonHang(int idDH, int idMonAn, int soLuong, int donGia, int thanhTien, DonHang.KieuDonHang kieuDonHang){
        String sql = "INSERT INTO chitietdonhang (ID_DonHang, ID_MonAn, Soluong, dongia, thanhtien, kieudonhang) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
                stmt.setInt(1,idDH);
                stmt.setInt(2,idMonAn);
                stmt.setInt(3,soLuong);
                stmt.setInt(4,donGia);
                stmt.setInt(5,thanhTien);
                stmt.setString(6, kieuDonHang.name());
                int row = stmt.executeUpdate();
                if(row>0){
                    try (ResultSet rs = stmt.getGeneratedKeys()){
                        if(rs.next()){
                            ChiTietDonHang ctDonHang = new ChiTietDonHang(idDH, idMonAn, soLuong, donGia, thanhTien,kieuDonHang);
                            ctDonHang.setKieuDonHang(kieuDonHang);
                            System.out.println("Thêm vào chi tiết đơn hàng thành công!");
                            return ctDonHang;
                        }
                    }
                }
            }catch (SQLException e) {
                System.out.println("Lỗi khi thêm chi tiết đơn hàng!");
                e.printStackTrace();
            }
        return null;
    }

    //Danh sách đơn hàng
    public static List<DonHang> xemDSDonHang(){
        List<DonHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM donhang";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()){
            System.out.println("=====================================================================================");
                System.out.printf("| %-5s | %-12s | %-12s | %-25s | %-20s |\n",
                                  "ID", "IDUser", "IDBanAn", "Trạng Thái", "Kiểu");
                System.out.println("=====================================================================================");
                
                while (rs.next()) {
                    int id = rs.getInt("ID_DonHang");
                    int idUser = rs.getInt("ID_User");
                    Integer idBanAn = rs.getInt("ID_BanAn");
                    DonHang.TrangThai trangThai = DonHang.TrangThai.valueOf(rs.getString("TrangThai"));
                    DonHang.KieuDonHang kieuDonHang = DonHang.KieuDonHang.valueOf(rs.getString("kieudonhang"));
                    
                    danhSach.add(new DonHang(id, idUser, idBanAn, trangThai, kieuDonHang));
                    System.out.printf("| %-5d | %-12s | %-12s | %-25s | %-20s |\n",id, idUser, idBanAn , trangThai, kieuDonHang);
                }               
                System.out.println("=====================================================================================");
        }catch(SQLException e){
            System.out.println("Lỗi khi lấy danh sách đơn hàng!");
            e.printStackTrace();
        }
        return danhSach;
    }

    // Sửa trạng thái đơn
    public static DonHang suaTrangThai(Scanner scanner){
        xemDSDonHang();
        System.out.println("Nhập ID đơn hàng cần sửa: ");
        String input = scanner.nextLine().trim();
    
        if (input.equals("0")) {
            return null;
        }

        String[] idStrings = input.split("[,\\s]+");
        List<Integer> listDH = new ArrayList<>();
        for( String idList : idStrings ){
            try{
                int id = Integer.parseInt(idList);
                listDH.add(id);
            }catch (NumberFormatException e){
                System.out.println("ID không hợp lệ: "+idList +". Bỏ qua ID này.");
            }
        }
        
        if (listDH.isEmpty()) {
            System.out.println("Không có ID hợp lệ để cập nhật!");
            return null;
        }

        for(int idDH : listDH){
            String sqlUpdate = "UPDATE donhang SET TrangThai = 'DA_HOAN_THANH' WHERE ID_DonHang = ?";
            try(Connection conn =DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlUpdate)){
                    stmt.setInt(1,idDH);
                    stmt.executeUpdate();
                }catch (SQLException e){
                    System.out.println("Lỗi kết nối cơ sở dữ liệu!");
                    e.printStackTrace();
                }
        } 
        System.out.println("Cập nhật thành công!");
        
        return null;
    }
    
}
