package nhanvien;

import java.util.Scanner;

import Main.Main;
import datban.DatBanChecker;
import nhanvien.DangNhap.CongViecNhanVien;

public class QuanLyNhanVien {
    public static NhanVien currentNV = null;
    public static void quanLyNhanVien() {
        Scanner scanner = new Scanner(System.in);        
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
                        DatBanChecker datBanChecker = new DatBanChecker();
                        Thread checker = new Thread(datBanChecker);
                        checker.setDaemon(true);
                        checker.start(); 
                        CongViecNhanVien.congViec(currentNV, idChiNhanh);
                        return; 
                    } else {
                        System.out.println("Đăng nhập thất bại, vui lòng thử lại!");
                    }
                    break;      
                case 0:
                    Main.main(new String[] {});
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
