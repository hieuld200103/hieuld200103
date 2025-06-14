package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.NhanVienServices;

public class CongViecNhanVien {
     public static void congViec(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        if (currentNV == null){
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }
    
        while (true) {
            String[] menu = {
                "CÔNG VIỆC NHÂN VIÊN",
                "1. Check đặt bàn",
                "2. Quản lý khách hàng",
                "3. Quản lý đơn hàng",
                "4. Quản lý menu",
                "5. Quản lý bàn ăn",
                "6. Quản lý khuyến mãi",
                "7. Quản lý hóa đơn",
                "0. Đăng xuất"
            };
            
            System.out.println("\n=== " + menu[0] + " ===");
            for (int i = 1; i < menu.length; i++) {
                System.out.println(menu[i]);
            }
            
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
                    QuanLyDatBan.quanLy(currentNV, idChiNhanh,scanner);
                    break;
                case 2:
                    QuanLyKhachHang.quanLy(currentNV,idChiNhanh,scanner);;
                    break;
                case 3:
                    QuanLyDonHang.quanLy(currentNV, idChiNhanh, scanner);
                    break;
                case 4:
                    QuanLyMonAn.quanLy(currentNV, idChiNhanh,scanner);
                    break;
                case 5:
                    QuanLyBanAn.quanLy(currentNV, idChiNhanh, scanner);                    
                    break;
                case 6:
                    QuanLyKhuyenMai.quanLy(currentNV, idChiNhanh);
                    break;
                case 7:
                    QuanLyHoaDon.quanLy(currentNV, idChiNhanh, scanner);
                    break;
                case 0:
                    NhanVienServices.dangXuat();
                    QuanLyNhanVien.quanLyNhanVien(scanner);
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }

}
