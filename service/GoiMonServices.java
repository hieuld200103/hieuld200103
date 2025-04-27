package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.ChiTietDonHang;
import model.DonHang;
import model.User;
import userinterface.DichVuKhachHang;

public class GoiMonServices {
    // Gọi món tại quán
    public static void goiMon(User currentUser, DonHang donHang, Scanner scanner) {
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

        if(DatBanServices.daNhanBan(currentUser.getID_User())){
            while (true) {
                System.out.println("\n=== GỌI MÓN ===");
                System.out.println("1. Xem menu");
                System.out.println("2. Tìm món ăn");
                System.out.println("3. Gọi món trực tiếp");
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
                        MonAnServices.xemMenu();
                        goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                        break;
    
                    case 2:
                        MonAnServices.timKiemMonAn(scanner);
                        goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                        break;
    
                    case 3:
                        goiMonAction(currentUser, donHang, scanner, danhSachChiTiet);
                        break;
    
                    case 0:
                        if (!danhSachChiTiet.isEmpty()) {
                            DonHangServices.themChiTietDonHang(danhSachChiTiet);
                        }
                        DichVuKhachHang.dichVu(currentUser);
                        return;
    
                    default:
                        System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
                }
            }
        }else{
            System.out.println("Bạn chưa có bàn!! Vui lòng nhận bàn để gọi món");
        }
        
    }

    // Gọi món (thao tác thêm món)
    private static void goiMonAction(User currentUser, DonHang donHang, Scanner scanner, List<ChiTietDonHang> danhSachChiTiet) {
        while (true) {
            System.out.print("\nNhập ID để gọi món (0 để quay lại): ");
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }

            int idMon = scanner.nextInt();
            if (idMon == 0) break;

            if (!MonAnServices.kiemTraMonAnTonTai(idMon)) {
                System.out.println("Không có món ăn với ID này! Vui lòng thử lại.");
                continue;
            }

            System.out.print("Nhập số lượng: ");
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }
            int soLuong = scanner.nextInt();

            int donGia = MonAnServices.layGiaMonAn(idMon);
            int thanhTien = donGia * soLuong;
            DonHang.KieuDonHang kieuDonHang = donHang.getKieuDonHang();
            ChiTietDonHang ctdh = new ChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien, kieuDonHang);
            danhSachChiTiet.add(ctdh);

            System.out.println("Đã thêm món vào đơn tạm!");
        }
    }
}
