package userinterface;

import java.util.Scanner;

import Main.Main;
import model.NhanVien;
import service.NhanVienServices;

public class QuanLyNhanVien {
    public static NhanVien currentNV = null;
    public static void quanLyNhanVien(Scanner scanner) {       
        while (true) {
            System.out.println("\n=== NHÂN VIÊN ===");
            System.out.println("1. Đăng Nhập");
            System.out.println("0. Thoát");
            System.out.println("Chọn chức năng: ");

            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    currentNV = NhanVienServices.dangNhap(scanner);
                    if (currentNV != null) {
                        int idChiNhanh = currentNV.getID_ChiNhanh();
                        CongViecNhanVien.congViec(currentNV, idChiNhanh,scanner);
                        return; 
                    } else {
                        System.out.println("Đăng nhập thất bại, vui lòng thử lại!");
                    }
                    break;      
                case 0:
                    Main.main(new String[] {});
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
