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
    //Thêm món vào đơn
    public static DonHang themDonHang(User user, DonHang.KieuDonHang kieuDonHang, Scanner scanner) {
        if (user == null || kieuDonHang == null) {
            System.out.println("Thông tin người dùng hoặc kiểu đơn hàng không hợp lệ.");
            return null;
        }
        System.out.print("ID bàn của bạn: ");
        int idBanAn = scanner.nextInt();
        String sql = "INSERT INTO donhang (ID_User, ID_BanAn, TrangThai, KieuDonHang) VALUES (?, ?, 'DANG_CHUAN_BI', ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {    
            stmt.setInt(1, user.getID_User());
            stmt.setInt(2, idBanAn);
            stmt.setString(3, kieuDonHang.name());
    
            int row = stmt.executeUpdate();
            if (row > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idDH = rs.getInt(1);
                        DonHang donHang = new DonHang(idDH, user.getID_User(), idBanAn, DonHang.TrangThai.DANG_CHUAN_BI, kieuDonHang);
                        donHang.setKieuDonHang(kieuDonHang);
                        System.out.println("Tạo đơn hàng thành công! Mã đơn: " + idDH);
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
   
    //Lấy đơn hiện tại
    public static DonHang layDonHangHienTai(int idUser) {
        String sql = "SELECT * FROM donhang WHERE ID_User = ? AND TrangThai = 'DANG_CHUAN_BI'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                int idDonHang = rs.getInt("ID_DonHang");
                return new DonHang(idDonHang, idUser, DonHang.TrangThai.DANG_CHUAN_BI);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    //Thêm vào chi tiết đơn
    public static void themChiTietDonHang(List<ChiTietDonHang> dsChiTiet) {
        String sql = "INSERT INTO chitietdonhang (ID_DonHang, ID_MonAn, SoLuong, dongia, thanhtien) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (ChiTietDonHang ctdh : dsChiTiet) {
                stmt.setInt(1, ctdh.getID_DonHang());
                stmt.setInt(2, ctdh.getID_MonAn());
                stmt.setInt(3, ctdh.getSoLuong());
                stmt.setInt(4, ctdh.getGia());
                stmt.setInt(5, ctdh.getThanhTien());
                stmt.addBatch();
            }

            stmt.executeBatch();
            System.out.println("Đã lưu các món vào đơn hàng thành công!");

        } catch (SQLException e) {
            System.out.println("Lỗi khi thêm chi tiết đơn hàng!");
            e.printStackTrace();
        }
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
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ!");
            scanner.next();
            return null;
        }
        int idDH = scanner.nextInt();
        scanner.nextLine();

        String sqlUpdate = "UPDATE donhang SET TrangThai = 'DA_HOAN_THANH' WHERE ID_DonHang = ?";
        try(Connection conn =DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlUpdate)){
                stmt.setInt(1,idDH);
                stmt.executeUpdate();
                System.out.println("Cập hật thành công!");
                
            }catch (SQLException e){
                System.out.println("Lỗi kết nối cơ sở dữ liệu!");
                e.printStackTrace();
            }
            return null;
    }
    
}
