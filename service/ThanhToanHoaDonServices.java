package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.HoaDon;
import model.User;
import userinterface.DichVuKhachHang;
public class ThanhToanHoaDonServices {

    public static void thanhToan(User currentUser) {
        List<HoaDon> danhSachHoaDon = new ArrayList<>();
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
                        danhSachHoaDon = HoaDonServices.locDanhSachHoaDon_KH(currentUser,scanner);
                        HoaDonServices.thanhtoanAction(currentUser,danhSachHoaDon);
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