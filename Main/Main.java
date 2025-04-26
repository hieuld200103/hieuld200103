package Main;
import java.util.Scanner;
import userinterface.QuanLyNhanVien;
import userinterface.TaiKhoanKhachHang;

public class Main {
    private static final String matKhauNhaHang = "nhom7";
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
              
        while (true) {
            System.out.println("\n=== Bạn là Khách Hàng hay Nhân Viên? ===");
            System.out.println("1. Khách hàng");
            System.out.println("2. Nhân viên");
            System.out.println("0. Thoát");
            System.out.print("Lựa chọn của bạn: ");
            
            if (!scanner.hasNextInt()) {
                System.out.println(" Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    TaiKhoanKhachHang.taiKhoanKhachHang();
                    break;
                case 2:
                    System.out.println("Nhập mật khẩu nhà hàng: ");
                    String mk = scanner.nextLine();
                    if(!matKhauNhaHang.equals(mk)){
                        System.out.println("Mật khẩu không đúng!");
                    } else {
                        System.out.println("Đăng nhập thành công với tư cách Nhân viên!");
                        QuanLyNhanVien.quanLyNhanVien();
                    } 
                    break;    
                case 0:
                    System.out.println("Đã thoát chương trình!");
                    scanner.close();
                    System.exit(0);
                    return;
                default:
                    System.out.println(" Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
        }
    }
}
