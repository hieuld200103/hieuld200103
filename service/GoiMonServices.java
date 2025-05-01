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

public class GoiMonServices {
    // Gọi món tại quán
    public static boolean goiMon(User currentUser, DonHang donHang, Scanner scanner) {
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();
        boolean daThemMon = false;
        while (true) {
            System.out.println("\n=== GỌI MÓN ===");
            System.out.println("1. Xem menu");
            System.out.println("2. Tìm món ăn");
            System.out.println("3. Gọi món trực tiếp");
            System.out.println("0. Thoát");
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
                    MonAnServices.xemMenu();
                    boolean themMon1 = goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    if(themMon1) daThemMon = true;
                    break;

                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    boolean themMon2 = goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    if(themMon2) daThemMon = true;
                    break;
                case 3:
                    boolean themMon3 = goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    if(themMon3) daThemMon = true;
                    break;    
                case 0:
                    return daThemMon;

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }       
    }

    // Gọi món (thao tác thêm món)
    private static boolean goiMonAction(User currentUser, DonHang donHang, Scanner scanner, List<ChiTietDonHang> danhSachChiTiet) {
        while (true) {
            System.out.print("\nNhập ID để gọi món (0 để quay lại): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }
            int idDH = donHang.getID_DonHang();
            int idMon = scanner.nextInt();
            if (idMon == 0){
                return false;
            }

            if (!kiemTraMonAnTonTai(idMon)) {
                System.out.println("Không có món ăn với ID này! Vui lòng thử lại.");
                continue;
            }

            System.out.print("Nhập số lượng: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }
            int soLuong = scanner.nextInt();

            int donGia = layGiaMonAn(idMon);
            int thanhTien = donGia * soLuong;
            DonHang.KieuDonHang kieuDonHang = donHang.getKieuDonHang();
            DonHangServices.themChiTietDonHang(idDH, idMon, soLuong, donGia, thanhTien, kieuDonHang);
            return true;         
        }
    }

    //Kiểm tra món có tồn tại k
    public static boolean kiemTraMonAnTonTai(int idMonAn) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM monan WHERE ID_MonAn = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idMonAn);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
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
            try(ResultSet rs = stmt.executeQuery()){
                if (rs.next()) {
                    return rs.getInt("Gia");
                } else {
                    System.out.println("Không tìm thấy món ăn với ID: " + idMonAn);
                }    
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy giá món ăn từ cơ sở dữ liệu");
            e.printStackTrace();
        }
        return 0; 
    }
}
