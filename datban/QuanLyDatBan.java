package datban;

import java.util.Scanner;

import nhanvien.NhanVien;
import nhanvien.DangNhap.CongViecNhanVien;

public class QuanLyDatBan {
     public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ ĐẶT BÀN ===");
            System.out.println("1. Xác nhận đặt bàn");
            System.out.println("2. Hủy đặt bàn");
            System.out.println("3. Danh sách đặt bàn ");
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
                DatBanServices.xacNhanDatBan(scanner, currentNV, idChiNhanh);
                    break;
                case 2:
                DatBanServices.huyDatBan(scanner, currentNV, idChiNhanh);
                    break;
                case 3:
                DatBanServices.locDanhSachDatBan(currentNV, idChiNhanh, scanner);
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
