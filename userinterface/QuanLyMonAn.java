package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.MonAnServices;
import service.NhanVienServices;

public class QuanLyMonAn {
    public static void quanLy(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        
        while (true) {
            System.out.println("\n=== QUẢN LÝ MENU ===");
            System.out.println("1. Xem danh sách món ăn");
            System.out.println("2. Tìm món");
            if(NhanVienServices.ktAdmin(currentNV.getID_NhanVien())){
                System.out.println("3. Thêm món ăn");
                System.out.println("4. Xóa món ăn");
                System.out.println("5. Sửa thông tin món ăn");
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
                    MonAnServices.menuXemMonAn(scanner);
                    break;
                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    break;
                case 3:
                    MonAnServices.themMonAn(scanner);
                    break;
                case 4:
                    MonAnServices.xoaMonAn(scanner);
                    break;
                case 5:
                    MonAnServices.suaMonAn(scanner);
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
