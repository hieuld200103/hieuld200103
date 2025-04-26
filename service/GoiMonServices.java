package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import Main.Main;
import model.ChiTietDonHang;
import model.DonHang;
import model.User;
import userinterface.DichVuKhachHang;

public class GoiMonServices {
    //Gọi món tại quán
    public static void goiMon(User currentUser, DonHang donHang, Scanner scanner) {       
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();

        while (true) {
            System.out.println("\n=== GỌI MÓN ===");
            System.out.println("1. Tìm món ăn");
            System.out.println("2. Gọi món");
            System.out.println("0. THOÁT");
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
                    timMon(currentUser, donHang, scanner);
                    break;

                case 2: 
                    while (true) {
                        System.out.print("Nhập ID món (0 để quay lại): ");
                        int idMon = scanner.nextInt();
                        if (idMon == 0) break;
                        if (!MonAnServices.kiemTraMonAnTonTai(idMon)) {
                            System.out.println("Không có món ăn với ID này! Vui lòng thử lại.");
                            continue; 
                        }
                        System.out.print("Nhập số lượng: ");
                        int soLuong = scanner.nextInt();

                        int donGia = MonAnServices.layGiaMonAn(idMon);
                        int thanhTien = donGia * soLuong;
                        DonHang.KieuDonHang kieuDonHang = donHang.getKieuDonHang();
                        ChiTietDonHang ctdh = new ChiTietDonHang(donHang.getID_DonHang(), idMon, soLuong, donGia, thanhTien,kieuDonHang);     
                        danhSachChiTiet.add(ctdh);

                        System.out.println("Đã thêm món vào đơn tạm.");
                    }
                    DonHangServices.themChiTietDonHang(danhSachChiTiet);
                    break;

                case 0:
                    DichVuKhachHang.dichVu(currentUser);                    
                    System.out.println("Bạn chưa đăng nhập!");
                    Main.main(new String [] {});   
                    return;

                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
   
    }


    //Tìm món
    public static void timMon(User currentUser, DonHang donHang, Scanner scanner) {        
        while (true) {            
            System.out.println("\n=== TÌM MÓN ĂN ===");
            System.out.println("1. Xem menu");
            System.out.println("2. Tìm món ăn");
            System.out.println("0. THOÁT");
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
                    MonAnServices.xemMenu();
                    break;
                case 2:
                    MonAnServices.timKiemMonAn(scanner);
                    break;
               
                case 0: 
                    goiMon(currentUser, donHang, scanner);
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }  
}
