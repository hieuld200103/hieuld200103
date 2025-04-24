package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.NhanVienServices;

public class CongViecNhanVien {
     public static void congViec(NhanVien currentNV, int idChiNhanh) {
        if (currentNV == null){
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== CÔNG VIỆC NHÂN VIÊN ===");
            System.out.println("1. Check đặt bàn");
            System.out.println("2. Quản lý khách hàng");
            System.out.println("3. Quản lý user");
            System.out.println("4. Quản lý menu");
            System.out.println("5. Quản lý bàn ăn");
            System.out.println("6. Check menu");
            System.out.println("7. Check hóa đơn");     
            System.out.println("0. Đăng xuất");
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
                    QuanLyDatBan.quanLy(currentNV, idChiNhanh);
                    break;
                case 2:
                    QuanLyKhachHang.quanLy(currentNV,idChiNhanh);;
                    break;
                case 3:
                    QuanLyUser.quanLy(currentNV, idChiNhanh);
                    break;
                case 4:
                    QuanLyMonAn.quanLy(currentNV, idChiNhanh);
                    break;
                case 5:
                    QuanLyBanAn.quanLy(currentNV, idChiNhanh);                    
                    break;
                case 6:
                    NhanVienServices.timMon(currentNV, idChiNhanh);
                    break;
                case 7:
                    System.out.println("Đang phát triển");
                    break;
                case 0:
                    NhanVienServices.dangXuat();
                    QuanLyNhanVien.quanLyNhanVien();
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }

}
