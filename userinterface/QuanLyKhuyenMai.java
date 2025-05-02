package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.KhuyenMaiServices;
import service.NhanVienServices;

public class QuanLyKhuyenMai {
    public static void quanLy(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== QUẢN LÝ KHUYẾN MÃI ===");
            System.out.println("1. Xem danh sách khuyến mãi");
            if(NhanVienServices.ktAdmin(currentNV.getID_NhanVien())){
                System.out.println("2. Thêm khuyến mãi");
                System.out.println("3. Xóa khuyến mãi");
                System.out.println("4. Sửa thông tin khuyến mãi");
            }
            
            
            System.out.println("0. THOÁT");
            System.out.print("Chọn: ");

            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    KhuyenMaiServices.xemKhuyenMai(currentNV, idChiNhanh);
                    break;
                case 2:
                    KhuyenMaiServices.themKhuyenMai(scanner);
                    break;
                case 3:
                    KhuyenMaiServices.xoaKhuyenMai(scanner);
                    break;
                case 4:
                    KhuyenMaiServices.suaKhuyenMai(scanner);
                    break;
               
                case 0:
                    if(NhanVienServices.ktAdmin(currentNV.getID_NhanVien())){
                        QuanLy.congViec(currentNV, idChiNhanh, scanner);
                    }else{
                        CongViecNhanVien.congViec(currentNV, idChiNhanh,scanner);
                    }                    
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }


}
