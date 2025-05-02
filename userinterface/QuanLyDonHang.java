package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.DonHangServices;
import service.NhanVienServices;
import service.TrangThaiDonHang;
public class QuanLyDonHang {
     public static void quanLy(NhanVien currentNV, int idChiNhanh,Scanner scanner) {
        if (currentNV == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }
        while (true) {
            System.out.println("\n=== QUẢN LÝ ĐƠN HÀNG ===");
            System.out.println("1. Sửa trạng thái đơn hàng ");
            System.out.println("2. Kiểm tra đơn hàng theo ID ");
            System.out.println("3. Xem trạng thái tất cả đơn hàng ");
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
                    DonHangServices.suaTrangThai(scanner);
                    break;
                case 2:
                    TrangThaiDonHang.kiemTraDonHangNV(scanner, currentNV, idChiNhanh);
                    break;
                case 3:
                    TrangThaiDonHang.kiemTraDonHangNV2(scanner, currentNV, idChiNhanh);
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
