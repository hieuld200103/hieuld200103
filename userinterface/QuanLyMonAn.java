package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.MonAnServices;

public class QuanLyMonAn {
    public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ MENU ===");
            System.out.println("1. Thêm món ăn");
            System.out.println("2. Xóa món ăn");
            System.out.println("3. Sửa thông tin món ăn");
            System.out.println("4. Xem danh sách món ăn");
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
                    MonAnServices.themMonAn(scanner);
                    break;
                case 2:
                    MonAnServices.xoaMonAn(scanner);
                    break;
                case 3:
                    MonAnServices.suaMonAn(scanner);
                    break;
                case 4:
                    MonAnServices.xemMenu();
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
