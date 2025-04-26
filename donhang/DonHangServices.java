package donhang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Main.Main;
import connection.DatabaseConnection;
import datban.DatBan;
import menu.MonAnServices;
import user.User;
import user.DangNhap.DichVuKhachHang;


public class DonHangServices {
    //Thêm món vào đơn
   public static DonHang themDonHang(DatBan currentDatBan){
    if(currentDatBan == null){
        System.out.println("Bạn chưa đặt bàn!");
        return null;
    }
    int idUser = currentDatBan.getID_User();
    int idBanAn = currentDatBan.getID_BanAn();

    String sql = "INSERT INTO donhang (ID_User, ID_BanAn, TrangThai) VALUES (?,?, 'DANG_CHUAN_BI')";
    try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setInt(1,idUser);
            stmt.setInt(2, idBanAn);
            stmt.executeUpdate();
              ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int idDH = rs.getInt(1);
                    System.out.println("Thêm bàn ăn thành công! Mã bàn: " + idDH);
                    return new DonHang(idDH, idUser , idBanAn, DonHang.TrangThai.DANG_CHUAN_BI);
                }
        }catch (SQLException e){
                System.out.println("Lỗi!");
                e.printStackTrace();
        }
        return null;
    }

    //Lấy đơn hiện tại
    public static DonHang layDonHangHienTai(int idUser, int idBanAn) {
        String sql = "SELECT * FROM donhang WHERE ID_User = ? AND ID_BanAn = ? AND TrangThai = 'DANG_CHUAN_BI'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idUser);
            stmt.setInt(2, idBanAn);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                int idDonHang = rs.getInt("ID_DonHang");
                return new DonHang(idDonHang, idUser, idBanAn, DonHang.TrangThai.DANG_CHUAN_BI);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Gọi món
    public static void goiMon(DonHang donHang, User currentUser) {
        Scanner scanner = new Scanner(System.in);
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

        while (true) {
            System.out.println("\n=== GỌI MÓN ===");
            System.out.println("1. Tìm món ăn");
            System.out.println("2. Gọi món");
            System.out.println("0. THOÁT");
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
                    timMon(donHang, currentUser);
                    break;

                case 2: 
                    while (true) {
                        System.out.print("Nhập ID món (0 để quay lại): ");
                        int idMon = scanner.nextInt();
                        if (idMon == 0) break;
                        if (!MonAnServices.kiemTraMonAnTonTai(idMon)) {
                            System.out.println("Không có món ăn với ID này! Vui lòng thử lại.");
                            continue; 
                        }
                        System.out.print("Nhập số lượng: ");
                        int soLuong = scanner.nextInt();

                        int donGia = MonAnServices.layGiaMonAn(idMon);
                        int thanhTien = donGia * soLuong;

                        ChiTietDonHang ctdh = new ChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien);
                        danhSachChiTiet.add(ctdh);

                        System.out.println("Đã thêm món vào đơn tạm.");
                    }
                    themChiTietDonHang(danhSachChiTiet);
                    break;

                case 0:
                    DichVuKhachHang.dichVu(currentUser);                    
                    System.out.println("Bạn chưa đăng nhập!");
                    Main.main(new String [] {});   
                    scanner.close();
                    return;

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
   
    }


    public static void themChiTietDonHang(List<ChiTietDonHang> dsChiTiet) {
        String sql = "INSERT INTO chitietdonhang (ID_DonHang, ID_MonAn, SoLuong, dongia, thanhTien) VALUES (?, ?, ?, ?,?)";

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

    public static void timMon(DonHang donHang, User currentUser) {
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
                    goiMon(donHang,currentUser);                   
                    scanner.close();
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  
}
