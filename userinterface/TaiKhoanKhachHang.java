package userinterface;

import java.util.Scanner;

import Main.Main;
import model.User;
import service.UserServices;

public class TaiKhoanKhachHang {
    public static User currentUser = null;
    public static void taiKhoanKhachHang(){
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== KHÁCH HÀNG ===");
            System.out.println("1. Đăng nhập");
            System.out.println("2. Đăng Ký");
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
                    currentUser = UserServices.dangNhap(scanner);
                    if (currentUser != null){
                        DichVuKhachHang.dichVu(currentUser);
                    
                        return;
                    }else{
                        System.out.println("Đăng nhập thất bại, vui lòng thử lại!");
                    }                    
                    break;
                case 2:
                    UserServices.dangKy(scanner);
                    break;
                                  
                case 0: 
                    Main.main(new String[] {});
                    scanner.close();
                    return; 
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
