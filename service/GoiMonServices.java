package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Scanner;

import connection.DatabaseConnection;
import model.DonHang;
import model.MonAn.LoaiMonAn;
import model.User;

public class GoiMonServices {
    // Gọi món tại quán
    public static boolean goiMon(User currentUser, DonHang donHang, Scanner scanner) {
        boolean daThemMon = false;
    
        Map<Integer, LoaiMonAn> loaiMonTheoMenu = Map.of(
            1, LoaiMonAn.KHAI_VI,
            2, LoaiMonAn.MON_CHINH,
            3, LoaiMonAn.TRANG_MIENG,
            4, LoaiMonAn.DO_UONG
        );
    
        while (true) {
            System.out.println("\n=========== GỌI MÓN ===========");
            System.out.println("1. Món khai vị");
            System.out.println("2. Món chính");
            System.out.println("3. Món tráng miệng");
            System.out.println("4. Đồ uống");
            System.out.println("5. Tìm món ăn");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
    
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }
    
            int choice = scanner.nextInt();
            scanner.nextLine();
    
            if (choice == 0) break;
    
            if (choice >= 1 && choice <= 4) {
                LoaiMonAn loaiDangChon = loaiMonTheoMenu.get(choice);
                String tenLoai = switch (loaiDangChon) {
                    case KHAI_VI -> "Món khai vị";
                    case MON_CHINH -> "Món chính";
                    case TRANG_MIENG -> "Món tráng miệng";
                    case DO_UONG -> "Đồ uống";
                };
    
                MonAnServices.xemMenu(tenLoai, "LoaiMonAn = '" + loaiDangChon.name() + "'");
    
                while (true) {
                    System.out.print("\nNhập ID để gọi " + tenLoai.toLowerCase() + " (0 để quay lại): ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Lỗi: Vui lòng nhập số!");
                        scanner.next();
                        continue;
                    }
    
                    int idMon = scanner.nextInt();
                    if (idMon == 0) break;
    
                    if (!kiemTraMonAnTonTai(idMon)) {
                        System.out.println("Không có món ăn với ID này!");
                        continue;
                    }
    
                    if (!monThuocLoai(idMon, loaiDangChon)) {
                        System.out.println("⚠ Món này không thuộc loại " + tenLoai.toLowerCase() + "!");
                        continue;
                    }
    
                    System.out.print("Nhập số lượng: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Lỗi: Vui lòng nhập số!");
                        scanner.next();
                        continue;
                    }
    
                    int soLuong = scanner.nextInt();
                    scanner.nextLine();
    
                    int donGia = layGiaMonAn(idMon);
                    String tenMon = layTenMonAn(idMon);
                    int thanhTien = donGia * soLuong;
    
                    DonHangServices.themChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien, donHang.getKieuDonHang());
                    System.out.println("✔ Đã thêm món '" + tenMon + "' (ID: " + idMon + "), số lượng " + soLuong + " vào đơn hàng.");
                    daThemMon = true;
                }
    
            } else if (choice == 5) {
                MonAnServices.timKiemMonAn(scanner);
    
                while (true) {
                    System.out.print("\nNhập ID món ăn muốn gọi (0 để quay lại): ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Lỗi: Vui lòng nhập số!");
                        scanner.next();
                        continue;
                    }
    
                    int idMon = scanner.nextInt();
                    if (idMon == 0) break;
    
                    if (!kiemTraMonAnTonTai(idMon)) {
                        System.out.println("Không có món ăn với ID này!");
                        continue;
                    }
    
                    System.out.print("Nhập số lượng: ");
                    if (!scanner.hasNextInt()) {
                        System.out.println("Lỗi: Vui lòng nhập số!");
                        scanner.next();
                        continue;
                    }
    
                    int soLuong = scanner.nextInt();
                    scanner.nextLine();
    
                    int donGia = layGiaMonAn(idMon);
                    String tenMon = layTenMonAn(idMon);
                    int thanhTien = donGia * soLuong;
    
                    DonHangServices.themChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien, donHang.getKieuDonHang());
                    System.out.println("✔ Đã thêm món '" + tenMon + "' (ID: " + idMon + "), số lượng " + soLuong + " vào đơn hàng.");
                    daThemMon = true;
                }
            } else {
                System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn lại.");
            }
        }
    
        return daThemMon;
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

    //Lấy tên
    public static String layTenMonAn(int idMon) {
        String sql = "SELECT TenMon FROM monan WHERE ID_MonAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("TenMon");
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy tên món ăn!");
            e.printStackTrace();
        }
        return null;
    }

    //Hạn chế chọn món
    public static boolean monThuocLoai(int idMon, LoaiMonAn loai) {
        String sql = "SELECT LoaiMonAn FROM monan WHERE ID_MonAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idMon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return LoaiMonAn.valueOf(rs.getString("LoaiMonAn")) == loai;
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra loại món ăn!");
            e.printStackTrace();
        }
        return false;
    }

}
