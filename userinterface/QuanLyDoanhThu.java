package userinterface;
import java.util.Scanner;
import model.NhanVien;
import service.DoanhThuServices;
public class QuanLyDoanhThu {
    public static void quanLy(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        while (true) { 
            System.out.println("\n=== QUẢN LÝ DOANH THU ===");
            System.out.println("1. Thống kê doanh thu theo quý ");
            System.out.println("2. Thống kê doanh thu theo tháng ");
            System.out.println("3. Thống kê doanh thu theo ngày ");
            System.out.println("4. Thống kê doanh thu theo khung giờ ");
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
                    DoanhThuServices.thongKeTheoQuy(idChiNhanh,scanner);
                    break;
                case 2:
                    DoanhThuServices.thongKeTheoThang(idChiNhanh,scanner);
                    break;
                case 3:
                    DoanhThuServices.thongKeTheoNgay(idChiNhanh,scanner);
                    break;
                case 4:
                    DoanhThuServices.thongKeTheoKhungGio(idChiNhanh,scanner);
                    break;
                case 0:
                    QuanLy.congViec(currentNV, idChiNhanh, scanner);              
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}