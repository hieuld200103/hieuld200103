package userinterface;
import java.util.Scanner;

import model.NhanVien;
import service.KhachHangServices;
import service.NhanVienServices;

public class QuanLyKhachHang {
    public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ KHÁCH HÀNG ===");
            System.out.println("1. Xác nhận nhận bàn");
            System.out.println("2. Xóa khách hàng");
            System.out.println("3. Xem danh sách khách hàng ăn tại nhà hàng");
            System.out.println("4. Xem danh sách user");
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
                    KhachHangServices.khachHangNhanBan(scanner, currentNV);
                    break;
                case 2:
                    KhachHangServices.xoaKhachHang(scanner);
                    break;
                case 3:
                    KhachHangServices.xemDanhSachKhachHang();
                    break;
                case 4:
                    NhanVienServices.xemDanhSachUser();
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
