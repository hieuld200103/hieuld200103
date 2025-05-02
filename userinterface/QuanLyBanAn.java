package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.BanAnServices;
import service.NhanVienServices;

public class QuanLyBanAn {
    public static void quanLy(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        while (true) {
            System.out.println("\n=== QUẢN LÝ BÀN ĂN ===");
            System.out.println("1. Xem danh sách bàn ăn");
            if(NhanVienServices.ktAdmin(currentNV.getID_NhanVien())){
                System.out.println("2. Thêm bàn ăn");
                System.out.println("3. Xóa bàn ăn");
                System.out.println("4. Sửa thông tin bàn ăn");
            }
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
                    NhanVienServices.checkBan(currentNV, idChiNhanh, scanner);
                    break;
                case 2:
                    BanAnServices.themBanAn(currentNV,idChiNhanh, scanner);
                    break;
                case 3:
                    BanAnServices.xoaBanAn(currentNV,idChiNhanh, scanner);
                    break;
                case 4:
                    BanAnServices.suaBanAn(currentNV,idChiNhanh,scanner);
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
