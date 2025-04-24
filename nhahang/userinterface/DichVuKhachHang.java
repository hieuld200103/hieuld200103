package userinterface;

import java.util.Scanner;

import model.DatBan;
import service.DatBanServices;
import model.DonHang;
import service.DonHangServices;
import service.GoiMonServices;
import service.MonAnServices;
import model.User;
import service.UserServices;

public class DichVuKhachHang {
    public static void dichVu(User currentUser) {
    if (currentUser == null) {
        System.out.println("Bạn chưa đăng nhập! Vui lòng đăng nhập trước.");
        return;
    }

    Scanner scanner = new Scanner(System.in);

    while (true) {
        System.out.println("\n=== DỊCH VỤ KHÁCH HÀNG ===");
        System.out.println("1. Gọi món tại quán");
        System.out.println("2. Đặt món mang về");
        System.out.println("3. Đặt món online");
        System.out.println("4. Đặt bàn");
        System.out.println("5. Xem thực đơn");
        System.out.println("6. Sửa thông tin cá nhân");
        System.out.println("0. Đăng xuất");
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
                DatBan currentDatBan = DatBanServices.layDatBan(currentUser.getID_User());
                if (currentDatBan != null) {
                    DonHang donHang = DonHangServices.layDonHangHienTai(currentUser.getID_User(), currentDatBan.getID_BanAn());
                    if (donHang == null) {
                        donHang = DonHangServices.themDonHang(currentUser, currentDatBan.getID_BanAn(),DonHang.KieuDonHang.TAI_QUAN );
                    }
                    if (donHang != null) {
                        donHang.setKieuDonHang(DonHang.KieuDonHang.TAI_QUAN);
                        GoiMonServices.goiMon(currentUser, donHang, scanner);
                    } else {
                        System.out.println("Không thể tạo hoặc lấy đơn hàng.");
                    }
                } else {
                    System.out.println("Bạn cần đặt và nhận bàn trước khi gọi món tại quán.");
                }
                break;

            case 2: 
                // DonHang donMangVe = DonHangServices.taoDonHangMangVe(currentUser); 
                // if (donMangVe != null) {
                //     donMangVe.setKieuDonHang(ChiTietDonHang.KieuDonHang.MANG_VE);
                //     DonHangServices.goiMon(donMangVe, currentUser);
                // }
                // break;

            case 3: 
                // DonHang donOnline = DonHangServices.taoDonHangOnline(currentUser); 
                // if (donOnline != null) {
                //     donOnline.setKieuDonHang(ChiTietDonHang.KieuDonHang.DAT_ONLINE);
                //     DonHangServices.goiMon(donOnline, currentUser);
                // }
                // break;

            case 4:
                DatBanServices.datBan(currentUser);
                break;

            case 5:
                MonAnServices.timMon(currentUser);
                break;

            case 6:
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
