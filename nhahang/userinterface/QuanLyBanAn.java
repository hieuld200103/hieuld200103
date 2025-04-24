package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.BanAnServices;
import service.NhanVienServices;

public class QuanLyBanAn {
    public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ BÀN ĂN ===");
            System.out.println("1. Thêm bàn ăn");
            System.out.println("2. Xóa bàn ăn");
            System.out.println("3. Sửa thông tin bàn ăn");
            System.out.println("4. Xem danh sách bàn ăn");
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
                    BanAnServices.themBanAn(scanner);
                    break;
                case 2:
                    BanAnServices.xoaBanAn(scanner);
                    break;
                case 3:
                    BanAnServices.suaBanAn(scanner);
                    break;
                case 4:
                    NhanVienServices.checkBan(idChiNhanh);
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
