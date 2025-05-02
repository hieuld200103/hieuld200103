package userinterface;

import java.util.Scanner;
import model.NhanVien;
import service.HoaDonServices;

public class QuanLyHoaDon {
     public static void quanLy(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        while (true) {
            System.out.println("\n=== QUẢN LÝ HÓA ĐƠN ===");
            System.out.println("1. Lọc danh sách hóa đơn");
            System.out.println("0. Thoát");
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
                    HoaDonServices.locDanhSachHoaDon(currentNV,scanner);
                    break;
                case 0:
                    CongViecNhanVien.congViec(currentNV, idChiNhanh,scanner);                    
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
