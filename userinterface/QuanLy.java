package userinterface;

import java.util.Scanner;

import model.NhanVien;
import service.BanAnServices;
import service.NhanVienServices;

public class QuanLy {
    public static void congViec(NhanVien currentQL,int idChiNhanh, Scanner scanner) {
        if (currentQL == null){
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }
        while (true) {
            String[] menu = {
                "CÔNG VIỆC QUẢN LÝ",
                "1. Quản lý nhân viên",
                "2. Check đặt bàn",
                "3. Quản lý khách hàng",
                "4. Quản lý đơn hàng",
                "5. Quản lý menu",
                "6. Quản lý bàn ăn",
                "7. Quản lý hóa đơn",
                "8. Quản lý khuyến mãi",
                "9. Quản lý doanh thu",
                "0. Thoát"
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
                    quanLyNhanVien(currentQL,idChiNhanh, scanner);
                    break;
                case 2:
                    QuanLyDatBan.quanLy(currentQL,idChiNhanh, scanner);
                    break;
                case 3:
                    QuanLyKhachHang.quanLy(currentQL,idChiNhanh,scanner);;
                    break;
                case 4:
                    QuanLyDonHang.quanLy(currentQL, idChiNhanh, scanner);
                    break;
                case 5:
                    QuanLyMonAn.quanLy(currentQL, idChiNhanh,scanner);
                    break;
                case 6:
                    QuanLyBanAn.quanLy(currentQL, idChiNhanh, scanner);                    
                    break;
                case 7:
                    QuanLyHoaDon.quanLy(currentQL, idChiNhanh, scanner);
                    break;
                case 8:
                    QuanLyKhuyenMai.quanLy(currentQL, idChiNhanh);
                    break;
                case 9:
                    QuanLyDoanhThu.quanLy(currentQL, idChiNhanh, scanner);  
                    break;
                case 0:
                    chonChiNhanhVaVaoCongViec(currentQL, scanner);
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }

    public static void chonChiNhanhVaVaoCongViec(NhanVien currentQL, Scanner scanner) {
        if (currentQL == null){
            System.out.println("Bạn chưa đăng nhập!");
            return;
        }
    
        System.out.println("\nVai trò của bạn là Quản lý nhà hàng!");
        
        while (true) {
            int idChiNhanh = BanAnServices.chonChiNhanh(scanner); 
            if (idChiNhanh == 0) {
                System.out.println("Bạn đã chọn thoát. Đăng xuất...");
                NhanVienServices.dangXuat();
                QuanLyNhanVien.quanLyNhanVien(scanner);
                return;
            }
            System.out.println("Quản lý chi nhánh: " + idChiNhanh);
            congViec(currentQL, idChiNhanh, scanner); 
        }
    }

    //Quản lý nhân viên
    public static void quanLyNhanVien(NhanVien currendQL, int idChiNhanh, Scanner scanner) {       
        while (true) {
            System.out.println("\n=== QUẢN LÝ NHÂN VIÊN ===");
            System.out.println("1. Đăng ký");
            System.out.println("2. Danh sách nhân viên");
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
                    NhanVienServices.dangKy(idChiNhanh,scanner);
                    break;
                case 2:
                    NhanVienServices.xemDanhSachNV(idChiNhanh);
                    break;
                case 0:
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
