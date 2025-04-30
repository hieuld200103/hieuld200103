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
import userinterface.DichVuKhachHang;

public class GoiMonServices {
    // Gọi món tại quán
    public static void goiMon(User currentUser, DonHang donHang, Scanner scanner) {
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();
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
                    goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    break;

                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    break;
                case 3:
                    goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                    break;    
                case 0:
                    if (!danhSachChiTiet.isEmpty()) {
                        DonHangServices.themChiTietDonHang(danhSachChiTiet);
                    }
                    DichVuKhachHang.dichVu(currentUser, scanner);
                    return;

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }    
       
        
    }

    // Gọi món (thao tác thêm món)
    private static void goiMonAction(User currentUser, DonHang donHang, Scanner scanner, List<ChiTietDonHang> danhSachChiTiet) {
        while (true) {
            System.out.print("\nNhập ID để gọi món (0 để quay lại): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }

            int idMon = scanner.nextInt();
            if (idMon == 0) break;

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
            ChiTietDonHang ctdh = new ChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien, kieuDonHang);
            danhSachChiTiet.add(ctdh);

            System.out.println("Đã thêm món vào đơn tạm!");
            DonHangServices.themChiTietDonHang(danhSachChiTiet);
            danhSachChiTiet.clear();
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
            System.out.println("Lỗi khi lấy giá món ăn từ cơ sở dữ liệu:");
            e.printStackTrace();
        }
        return 0; 
    }
}
