package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.DonHangServices;

public class QuanLyDonHang {
     public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ ĐƠN HÀNG ===");
            System.out.println("1. Xem danh sách đơn hàng");
            System.out.println("2. Sửa trạng thái đơn hàng ");
            System.out.println("3. ");
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
                    DonHangServices.xemDSDonHang();
                    break;
                case 2:
                    DonHangServices.suaTrangThai(scanner);
                    break;
                case 3:
                    System.out.println("Đang phát triển");
                    break;
                case 0:
                    CongViecNhanVien.congViec(currentNV, idChiNhanh);                     
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
