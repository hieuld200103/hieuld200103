package nhanvien.DangNhap;

import java.util.Scanner;

import nhanvien.NhanVien;
import nhanvien.NhanVienServices;
public class QuanLyUser {
    public static void quanLy(NhanVien currentNV,int idChiNhanh) {
            if (currentNV == null) {
                System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
                return;
            }
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== QUẢN LÝ USER ===");
                System.out.println("1. Xem danh sách user");
                System.out.println("2. Xóa user");
                System.out.println("3. Sửa thông tin user");
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
                        System.out.println("=== DANH SÁCH USER ===");
                        NhanVienServices.xemDanhSachUser();
                        break;
                    case 2:
                        NhanVienServices.xoaUser(scanner);
                        break;
                    case 3:
                        NhanVienServices.suaUser(scanner);
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
