package service;
import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import model.HoaDon;
import model.KhuyenMai;
import model.NhanVien;
import model.User;
import userinterface.DichVuKhachHang;
import userinterface.QuanLyHoaDon;
import userinterface.QuanLyThanhToanHoaDon;

public class HoaDonServices {
    
    private static Scanner sc = new Scanner(System.in);
    private static void printDanhSachHD(PreparedStatement stmt,List<HoaDon> danhSach , String tieuDe){
        try (ResultSet rs = stmt.executeQuery()) {
            int stt = 1;
        System.out.println("\n============================================================ "+ tieuDe+ " ====================================================================");
                System.out.println("=========================================================================================================================================================");
                System.out.printf("| %-3s | %-5s | %-7s | %-7s | %-15s | %-15s | %-15s | %-15s | %-20s | %-20s |\n", 
                                  "STT","ID HĐ", "ID ĐH", "ID KM","Tổng tiền (VND)", "Phải trả (VND)", "PT Thanh toán", "Trạng thái", "Ngày tạo HD ", "Ngày TT");

                while (rs.next()) {
                    int idHoaDon = rs.getInt("ID_HoaDon");
                    int idDonHang = rs.getInt("ID_DonHang");
                    int tongTien = rs.getInt("TongTien");
                    int phaiTra = rs.getInt("PhaiTra");
                    String ptThanhToan = rs.getString("PTThanhToan");
                    String trangThai = rs.getString("TrangThaiHD");
                    LocalDateTime ngayThanhToan = rs.getTimestamp("NgayThanhToan")!= null ? rs.getTimestamp("NgayThanhToan").toLocalDateTime(): null;
                    LocalDateTime ngayTaoHD = rs.getTimestamp("NgayTaoHoaDon")!= null ? rs.getTimestamp("NgayTaoHoaDon").toLocalDateTime(): null;
                    int idKhuyenMai = rs.getInt("ID_KhuyenMai");
                    System.out.println("---------------------------------------------------------------------------------------------------------------------------------------------------------");

                    HoaDon hoaDon = new HoaDon(idHoaDon, idDonHang, tongTien, phaiTra, ptThanhToan, trangThai, idKhuyenMai, ngayTaoHD, ngayThanhToan);
                    danhSach.add(hoaDon);

                    System.out.printf("| %-3d | %-5d | %-7d | %-7d | %-,15d | %-,15d | %-15s | %-15s | %-20s | %-20s |\n",
                                      stt++,idHoaDon, idDonHang,idKhuyenMai, tongTien,phaiTra, ptThanhToan, trangThai, ngayTaoHD != null ? ngayTaoHD.toString() : "null",ngayThanhToan != null ? ngayThanhToan.toString() : "null");
                }

                System.out.println("=========================================================================================================================================================\n");
        }
        catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách hóa đơn!");
            e.printStackTrace();
        }
    } 
    
    
    // ===================================================================
    // XEM DANH SÁCH HÓA ĐƠN - KHÁCH HÀNG
    public static List<HoaDon> xemHoaDon(int id_user,String tieuDe, String dieuKienWhere) {
        List<HoaDon> danhSach = new ArrayList<>();

        String sql = "SELECT h.* " +
                     "FROM hoadon h " +
                     "JOIN donhang d ON h.ID_DonHang = d.ID_DonHang " +
                     "WHERE d.ID_User = ?";

        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()) {
            sql += " AND " + dieuKienWhere;
        }

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id_user);
            printDanhSachHD(stmt, danhSach, tieuDe);
            }
        catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách hóa đơn!");
            e.printStackTrace();
        }
        
        return danhSach;
    }
    // XEM DANH SÁCH HÓA ĐƠN - NHÂN VIÊN
    public static List<HoaDon> xemDanhSachHoaDon(NhanVien currentNV, int idChiNhanh, String tieuDe, String dieuKienWhere) {
        List<HoaDon> danhSach = new ArrayList<>();
        
        String sql = "SELECT h.* " +
                     "FROM hoadon h " +
                     "JOIN donhang d ON h.ID_DonHang = d.ID_DonHang " +
                     "WHERE d.ID_ChiNhanh = ?";
        
        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()) {
            sql += " AND " + dieuKienWhere;
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1,idChiNhanh);
            printDanhSachHD(stmt, danhSach, tieuDe);
            }
        catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách hóa đơn!");
            e.printStackTrace();
        }
        return danhSach;
    }

    // ===================================================================

    public static void thanhtoanAction(User currentUser, List<HoaDon> danhSach) {
        int idHoaDon = currentUser.getID_User();
        HoaDon hoaDon = null;

        // Bước 1: Nhập ID hóa đơn hợp lệ
        while (true) {
            System.out.print("Nhập ID hóa đơn muốn thanh toán (0 để thoát): ");
            String input = sc.nextLine().trim();
        
            if (input.isEmpty()) {
                System.out.println("Bạn chưa nhập gì. Vui lòng thử lại.");
                continue;
            }
        
            try {
                idHoaDon = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("ID không hợp lệ. Vui lòng nhập số nguyên.");
                continue;
            }
        
            if (idHoaDon == 0) {
                QuanLyThanhToanHoaDon.thanhToan(currentUser);
                return; 
            }
        
            hoaDon = null; // reset lại trước khi tìm
            for (HoaDon hd : danhSach) {
                if (hd.getIdHoaDon() == idHoaDon) {
                    hoaDon = hd;
                    break;
                }
            }
        
            if (hoaDon != null) break;
        
            System.out.println("ID không hợp lệ. Vui lòng nhập lại!");
        }
        

        // Bước 2: Hiển thị lựa chọn
        int choice = -1;
        while (true) {
            System.out.println("1. Thanh toán ngay");
            System.out.println("2. Áp dụng khuyến mãi");
            System.out.print("Lựa chọn của bạn: ");
            String input = sc.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("Không được để trống!");
                continue;
            }

            try {
                choice = Integer.parseInt(input);
                if (choice == 1 || choice == 2) {
                    break;
                } else {
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng chọn 1 hoặc 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Vui lòng nhập một số nguyên hợp lệ.");
            }
        }
        switch (choice) {
            case 1:
                thanhToan(idHoaDon, null);  // không áp khuyến mãi
                break;

            case 2:
               // Truy vấn khuyến mãi đang diễn ra tại ngày tạo hóa đơn
                List<KhuyenMai> danhSachKM = layKhuyenMaiTrongNgay(hoaDon.getNgayTaoHD());
                if (danhSachKM.isEmpty()) {
                    System.out.println("Không có khuyến mãi nào phù hợp!");
                    thanhToan(idHoaDon, null);
                    return;
                }

                System.out.println("\n===== Danh sách khuyến mãi khả dụng =====");
                for (int i = 0; i < danhSachKM.size(); i++) {
                    KhuyenMai km = danhSachKM.get(i);
                    System.out.printf("%d. %s (%.0f%%)\n", i + 1, km.getTenChuongTrinh(), km.getPhanTramKM());
                }

                KhuyenMai khuyenMaiChon = null;
                while (true) {
                    System.out.print("Chọn khuyến mãi (1-" + danhSachKM.size() + "): ");
                    try {
                        int kmChoice = Integer.parseInt(sc.nextLine());
                        if (kmChoice >= 1 && kmChoice <= danhSachKM.size()) {
                            khuyenMaiChon = danhSachKM.get(kmChoice - 1);
                            break;
                        } else {
                            System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 1 đến " + danhSachKM.size() + ".");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Vui lòng nhập một số nguyên hợp lệ!");
                    }
                }

                thanhToan(idHoaDon, khuyenMaiChon);
                break;
                        }
                    }

    // ===================================================================

    public static void thanhToan(int idHoaDon, KhuyenMai khuyenMai) {
        int pt;
        while (true) {
            System.out.println("VUI LÒNG CHỌN PHƯƠNG THỨC THANH TOÁN:");
            System.out.println("1. Tiền mặt");
            System.out.println("2. Chuyển khoản ngân hàng");
            System.out.println("3. VNPAY");
            System.out.print("Lựa chọn: ");
            try {
                pt = Integer.parseInt(sc.nextLine());
                if (pt >= 1 && pt <= 3) break;
                else System.out.println("❌ Vui lòng chọn số từ 1 đến 3.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Nhập không hợp lệ. Vui lòng nhập số nguyên.");
            }
        }
    
        String phuongThuc = switch (pt) {
            case 1 -> "TIEN_MAT";
            case 2 -> "CHUYEN_KHOAN";
            case 3 -> "VNPAY";
            default -> "TIEN_MAT"; 
        };
    
        String sql = "UPDATE hoadon SET PhaiTra = ?, PTThanhToan = ?, TrangThaiHD = ?, ID_KhuyenMai = ?, NgayThanhToan = ? WHERE ID_HoaDon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            int tongTien = getTongTienFromDB(idHoaDon);
            double phanTram = (khuyenMai != null) ? khuyenMai.getPhanTramKM() : 0;
            int phaiTra = (int)(tongTien * (100 - phanTram) / 100);
            LocalDateTime ngayThanhToan = LocalDateTime.now();
    
            stmt.setInt(1, phaiTra);
            stmt.setString(2, phuongThuc);
            stmt.setString(3, "CHO_XAC_NHAN");
            if (khuyenMai != null) {
                stmt.setInt(4, khuyenMai.getID_KhuyenMai());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setTimestamp(5, Timestamp.valueOf(ngayThanhToan));
            stmt.setInt(6, idHoaDon);
    
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.printf("✅ Số tiền bạn cần thanh toán: %,d VND%n", phaiTra);
                System.out.println("✅ Vui lòng thanh toán và đợi nhân viên xác nhận!");
            } else {
                System.out.println("❌ Không tìm thấy hóa đơn để cập nhật.");
            }
    
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật hóa đơn!");
            e.printStackTrace();
        }
    }
    

    // Lấy số tiền phải trả từ bảng hoadon
    private static int getTongTienFromDB(int idHoaDon) {
        int tongTien = 0;
        String sql = "SELECT TongTien FROM hoadon WHERE ID_HoaDon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHoaDon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                tongTien = rs.getInt("TongTien");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTien;
    }
    // ===================================================================
    // Lấy danh sách khuyến mãi đang diễn ra trong ngày
    private static List<KhuyenMai> layKhuyenMaiTrongNgay(LocalDateTime ngay) {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM khuyenmai WHERE NgayBatDau <= ? AND NgayKetThuc >= ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf (ngay));
            stmt.setTimestamp(2, Timestamp.valueOf (ngay));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                KhuyenMai km = new KhuyenMai(
                    rs.getInt("ID_KhuyenMai"),
                    rs.getString("TenChuongTrinhKM"),
                    rs.getDouble("PhanTramKM"),
                    rs.getDate("NgayBatDau"),
                    rs.getDate("NgayKetThuc")
                );
                ds.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    // ===================================================================
    //Cập nhật theo checker
    public static void capNhatTrangThai(NhanVien currentNV,int idChiNhanh, List<HoaDon> danhSachHoaDon, Scanner scanner, String trangThaiMoi) {
        List<Integer> idHoaDons = new ArrayList<>();
        System.out.print("\nNhập danh sách ID hóa đơn cần cập nhật trạng thái (ngăn cách bởi dấu phẩy) hoặc nhập 0 để thoát: ");
        String input = scanner.nextLine();
        if (input.equals("0")){
            locDanhSachHoaDon(currentNV,idChiNhanh,scanner); // Nếu nhập 0 thì thoát
        }
        try {
            String[] parts = input.split(",");
            for (String part : parts) {
                int id = Integer.parseInt(part.trim());
                idHoaDons.add(id);
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Lỗi: Chỉ nhập số nguyên, ngăn cách bằng dấu phẩy.");
            return;
        }

        for (int idHoaDon : idHoaDons) {
            HoaDon hoaDon = null;
            for (HoaDon h : danhSachHoaDon) {
                if (h.getIdHoaDon() == idHoaDon) {
                    hoaDon = h;
                    break;
                }
            }

            if (hoaDon != null) {
                // Cập nhật trạng thái trong danh sách Java
                hoaDon.setTrangThai(trangThaiMoi);

                // Cập nhật trạng thái trong CSDL - bảng hoadon
                String sqlHoaDon = "UPDATE hoadon SET TrangThaiHD = ? WHERE ID_HoaDon = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sqlHoaDon)) {

                    stmt.setString(1, trangThaiMoi);
                    stmt.setInt(2, idHoaDon);
                    int rows = stmt.executeUpdate();

                    if (rows > 0) {
                        System.out.println("✅ Đã cập nhật trạng thái hóa đơn ID " + idHoaDon + " trong CSDL.");
                    } else {
                        System.out.println("⚠️ Không tìm thấy hóa đơn ID " + idHoaDon + " trong CSDL.");
                    }

                    // Nếu trạng thái mới của hóa đơn là DA_THANH_TOAN, cập nhật trạng thái đơn hàng
                    if ("DA_THANH_TOAN".equalsIgnoreCase(trangThaiMoi)) {
                        int idDonHang = hoaDon.getIdDonHang();
                        String sqlDonHang = "UPDATE donhang SET TrangThai = 'DA_HOAN_THANH' WHERE ID_DonHang = ?";
                        try (PreparedStatement stmtDH = conn.prepareStatement(sqlDonHang)) {
                            stmtDH.setInt(1, idDonHang);
                            stmtDH.executeUpdate();
                            System.out.println("➡️ Đơn hàng ID " + idDonHang + " đã được cập nhật thành 'DA_HOAN_THANH'.");
                        }
                    }

                } catch (SQLException e) {
                    System.out.println("❌ Lỗi khi cập nhật CSDL cho hóa đơn ID " + idHoaDon);
                    e.printStackTrace();
                }

            } else {
                System.out.println("⚠️ Không tìm thấy hóa đơn ID " + idHoaDon + " trong danh sách.");
            }
        }
    }



    //===================================================================
    public static void locDanhSachHoaDon(NhanVien currentNV, int idChiNhanh, Scanner scanner) {
        while (true) {
            List<HoaDon> danhSachHoaDon;
            System.out.println("\n=== LỌC DANH SÁCH ===");
            System.out.println("1. Chưa thanh toán");
            System.out.println("2. Chờ xác nhận");
            System.out.println("3. Đã thanh toán");
            System.out.println("4. Tất cả danh sách");
            System.out.println("0. Thoát");
            System.out.print("Nhập: ");
    
            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                scanner.next(); 
                continue;
            }
    
            switch (choice) {
                case 1:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh, "Danh sách chưa thanh toán", "TrangThaiHD = 'CHUA_THANH_TOAN'");
                    System.out.println("\n===== XÁC NHẬN THANH TOÁN ===== ");
                    capNhatTrangThai(currentNV,idChiNhanh, danhSachHoaDon, scanner, "DA_THANH_TOAN");
                    break;
                case 2:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh, "Danh sách chờ xác nhận", "TrangThaiHD = 'CHO_XAC_NHAN'");
                    System.out.println("\n===== XÁC NHẬN THANH TOÁN ===== ");
                    capNhatTrangThai(currentNV,idChiNhanh, danhSachHoaDon, scanner, "DA_THANH_TOAN");
                    break;
                case 3:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh,"Danh sách đã thanh toán", "TrangThaiHD = 'DA_THANH_TOAN'");
                    System.out.println("\n====== XUẤT HÓA ĐƠN ======= ");
                    xuatHoaDon(currentNV,idChiNhanh, danhSachHoaDon, scanner);
                    break;
                case 4:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh, "Toàn bộ danh sách", null);
                    break;
                case 0:
                    QuanLyHoaDon.quanLy(currentNV, idChiNhanh,scanner);
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }
           
        }
    }
    public static List<HoaDon> locDanhSachHoaDon_KH(User currentUser, Scanner scanner) {
        List<HoaDon> danhSachHoaDon = new ArrayList<>();
        while (true) {
            int idUser = currentUser.getID_User();
            System.out.println("\n=== XEM DANH SÁCH ===");
            System.out.println("1. Chưa thanh toán");
            System.out.println("2. Chờ xác nhận");
            System.out.println("3. Đã thanh toán");
            System.out.println("4. Tất cả danh sách");
            System.out.println("0. Thoát");
            System.out.print("Nhập: ");
    
            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine();
            } else {
                System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                scanner.next(); 
                continue;
            }
    
            switch (choice) {
                case 1:
                    danhSachHoaDon = xemHoaDon(idUser, "Danh sách chưa thanh toán", "TrangThaiHD = 'CHUA_THANH_TOAN'");
                    HoaDonServices.thanhtoanAction(currentUser,danhSachHoaDon);
                    break;
                case 2:
                    danhSachHoaDon = xemHoaDon(idUser, "Danh sách chờ xác nhận", "TrangThaiHD = 'CHO_XAC_NHAN'");
                    break;
                case 3:
                    danhSachHoaDon = xemHoaDon(idUser,"Danh sách đã thanh toán", "TrangThaiHD = 'DA_THANH_TOAN'");
                    break;
                case 4:
                    xemHoaDon(idUser, "Toàn bộ danh sách", null);
                    break;
                case 0:
                    DichVuKhachHang.dichVu(currentUser,scanner);
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại!");
            }

            return danhSachHoaDon;
        }
        
    }
    //==========XUẤT HÓA ĐƠN CHI TIẾT================================================
    public static void xuatHoaDon(NhanVien currentNV,int idChiNhanh, List<HoaDon> danhSachHoaDon, Scanner scanner) {
        List<Integer> idHoaDons = new ArrayList<>();
        System.out.print("\nNhập danh sách ID hóa đơn cần xuất hóa đơn (cách nhau bằng dấu phẩy) hoặc nhập 0 để thoát: ");
        String input = scanner.nextLine();
        if (input.equals("0")){
            locDanhSachHoaDon(currentNV,idChiNhanh,scanner);
        }

        try {
            String[] parts = input.split(",");
            for (String part : parts) {
                int id = Integer.parseInt(part.trim());
                idHoaDons.add(id);
            }
        } catch (NumberFormatException e) {
            System.out.println("Lỗi: Dãy nhập vào phải chỉ chứa các số nguyên, ngăn cách bởi dấu phẩy.");
            return;
        }

        for (int idHoaDon : idHoaDons) {
            // Tìm hóa đơn trong danh sách
            HoaDon hoaDon = null;
            for (HoaDon h : danhSachHoaDon) {
                if (h.getIdHoaDon() == idHoaDon) {
                    hoaDon = h;
                    break;
                }
            }
            if (hoaDon == null) {
                System.out.println("❌ Không tìm thấy hóa đơn với ID: " + idHoaDon);
                continue;
            }

            // In thông tin hóa đơn
            System.out.println(hoaDon);

            // In danh sách món ăn từ chi tiết đơn hàng
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = """
                    SELECT ma.TenMon, ctdh.SoLuong, ctdh.dongia, ctdh.thanhtien
                    FROM chitietdonhang ctdh
                    JOIN monan ma ON ctdh.ID_MonAn = ma.ID_MonAn
                    WHERE ctdh.ID_DonHang = ?
                """;
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, hoaDon.getIdDonHang());
                    ResultSet rs = stmt.executeQuery();

                    System.out.println("\nDanh sách món ăn:");
                    System.out.printf("%-40s %-10s %-12s %-12s%n", "Tên món", "Số lượng", "Đơn giá ", "Thành tiền");
                    while (rs.next()) {
                        String tenMon = rs.getString("TenMon");
                        int soLuong = rs.getInt("SoLuong");
                        int donGia = rs.getInt("dongia");
                        int thanhTien = rs.getInt("thanhtien");
                        System.out.printf("%-40s %-10d %-,12d %-,12d%n", tenMon.length() > 40 ? tenMon.substring(0, 37)+"...":tenMon, soLuong, donGia, thanhTien);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi lấy danh sách món ăn cho đơn hàng ID: " + hoaDon.getIdDonHang());
                e.printStackTrace();
            }
            System.out.printf("\n%-64s %,d VND", "TỔNG TIỀN",hoaDon.getTongTien());
            System.out.printf("\n%-64s %,d VND%n", "PHẢI TRẢ",hoaDon.getPhaiTra());
        }
    }

}
    
