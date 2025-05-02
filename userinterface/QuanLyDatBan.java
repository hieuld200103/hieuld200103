package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.DatBanServices;
import service.NhanVienServices;

public class QuanLyDatBan {
     public static void quanLy(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        while (true) {
            System.out.println("\n=== QUẢN LÝ ĐẶT BÀN ===");
            System.out.println("1. Xác nhận đặt bàn");
            System.out.println("2. Kiểm tra đặt bàn");
            System.out.println("3. Hủy đặt bàn");
            System.out.println("4. Danh sách đặt bàn ");
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
                    DatBanServices.xacNhanDatBan(scanner, currentNV,idChiNhanh);
                    break;
                case 2:
                    DatBanServices.timDatBan(scanner);
                    break;
                case 3:
                    DatBanServices.huyDatBan(scanner, currentNV,idChiNhanh);
                    break;
                case 4:
                    DatBanServices.locDanhSachDatBan(currentNV,idChiNhanh, scanner);
                    break;
                case 0:
                    if(NhanVienServices.ktAdmin(currentNV.getID_NhanVien())){
                        QuanLy.congViec(currentNV, idChiNhanh, scanner);
                    }else{
                        CongViecNhanVien.congViec(currentNV, idChiNhanh,scanner);
                    }                    
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }

}
