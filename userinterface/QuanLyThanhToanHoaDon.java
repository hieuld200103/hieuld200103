package userinterface;

import java.util.Scanner;
import model.User;
import service.HoaDonServices;
public class QuanLyThanhToanHoaDon {
    public static void thanhToan(User currentUser) {
        Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("\n=== THANH TOÁN ===");
                System.out.println("1. Xem hóa đơn");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
                if (!scanner.hasNextInt()) {
                    System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                    scanner.next();
                    continue;
                }
        
                int choice = scanner.nextInt();
                scanner.nextLine();
        
                switch (choice) {
                    case 1:
                        HoaDonServices.taoHoaDon(currentUser);
                        HoaDonServices.locDanhSachHoaDon_KH(currentUser,scanner);
                
                        break;
                    case 0:
                        DichVuKhachHang.dichVu(currentUser,scanner);
                        return;
        
                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
                }
            }    
    }
}