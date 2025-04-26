package user.DangNhap;

import java.util.Scanner;

import datban.DatBan;
import datban.DatBanServices;
import donhang.DonHang;
import donhang.DonHangServices;
import menu.MonAnServices;
import user.User;
import user.UserServices;

public class DichVuKhachHang {
    public static void dichVu(User currentUser) {
        if (currentUser == null) {
            System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
            return;
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            boolean daNhanBan = DatBanServices.daNhanBan(currentUser.getID_User());

            System.out.println("\n=== DỊCH VỤ KHÁCH HÀNG ===");

            if (daNhanBan) {
                System.out.println("1. Gọi món");
                System.out.println("2. Xem món đã gọi");
                System.out.println("3. Thanh toán");
                System.out.println("0. Thoát");
                System.out.print("Chọn chức năng: ");
            } else {
                System.out.println("1. Đặt bàn");
                System.out.println("2. Xem thực đơn");
                System.out.println("3. Đặt hàng online");
                System.out.println("4. Sửa thông tin cá nhân");
                System.out.println("0. Đăng xuất");
                System.out.print("Chọn chức năng: ");
            }

           

            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            if (daNhanBan) {
                switch (choice) {
                    case 1:                        
                    DatBan currentDatBan = DatBanServices.layDatBan(currentUser.getID_User());
                    if (currentDatBan != null) {
                        DonHang donHang = DonHangServices.layDonHangHienTai(currentUser.getID_User(), currentDatBan.getID_BanAn());
                        if (donHang == null) {
                            donHang = DonHangServices.themDonHang(currentDatBan);
                        }
                        if (donHang != null) {
                            DonHangServices.goiMon(donHang, currentUser);
                        } else {
                            System.out.println("Không thể tạo hoặc lấy đơn hàng.");
                        }
                    } else {
                        System.out.println("Bạn chưa đặt bàn!");
                    }
                    break;
                    case 2:
                        System.out.println("Đang pt");
                        break;
                    case 3:
                        System.out.println("Đang pt");
                        break;
                    case 0:
                        UserServices.dangXuat();
                        TaiKhoanKhachHang.taiKhoanKhachHang();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            } else {
                switch (choice) {
                    case 1:
                        DatBanServices.datBan(currentUser);
                        break;
                    case 2:
                        MonAnServices.timMon(currentUser);
                        break;
                    case 3:
                        System.out.println("Chức năng đặt hàng online đang phát triển.");
                        break;
                    case 4:
                        UserServices.suaThongTinCaNhan(scanner, currentUser.getID_User(), currentUser);
                        break;
                    case 0:
                        UserServices.dangXuat();
                        TaiKhoanKhachHang.taiKhoanKhachHang();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Lựa chọn không hợp lệ!");
                }
            }
        }
    }

    

}
