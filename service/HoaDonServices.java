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

public class HoaDonServices {
    private static Scanner sc = new Scanner(System.in);
    public static void taoHoaDon(User currentUser) {
        String getDonHangSQL = """
            SELECT dh.ID_DonHang
            FROM donhang dh
            WHERE dh.ID_User = ?
            AND dh.ID_DonHang NOT IN (SELECT ID_DonHang FROM hoadon)
        """;

        String getTongTienSQL = """
            SELECT SUM(thanhtien) AS TongTien
            FROM chitietdonhang
            WHERE ID_DonHang = ?
        """;

        String insertHoaDonSQL = """
            INSERT INTO hoadon (ID_DonHang, TongTien, PTThanhToan, TrangThaiHD, NgayTaoHoaDon, PhaiTra)
            VALUES (?, ?, NULL, 'CHUA_THANH_TOAN', ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmtDonHang = conn.prepareStatement(getDonHangSQL);
            PreparedStatement stmtTongTien = conn.prepareStatement(getTongTienSQL);
            PreparedStatement stmtInsert = conn.prepareStatement(insertHoaDonSQL)) {

            stmtDonHang.setInt(1, currentUser.getID_User());
            ResultSet rs = stmtDonHang.executeQuery();

            while (rs.next()) {
                int idDonHang = rs.getInt("ID_DonHang");

                // Tính tổng tiền
                stmtTongTien.setInt(1, idDonHang);
                ResultSet rsTien = stmtTongTien.executeQuery();
                int tongTien = 0;
                if (rsTien.next()) {
                    tongTien = rsTien.getInt("TongTien");
                }

                // Chèn hóa đơn
                LocalDateTime ngayTaoHoaDon = LocalDateTime.now(); 
                stmtInsert.setInt(1, idDonHang);
                stmtInsert.setInt(2, tongTien);
                stmtInsert.setTimestamp(3, Timestamp.valueOf(ngayTaoHoaDon)); // NgayTaoHoaDon
                stmtInsert.setInt(4, tongTien); // PhaiTra = tổng tiền khi chưa có khuyến mãi

                int rows = stmtInsert.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ Đã tạo hóa đơn cho đơn hàng ID: " + idDonHang);
                } else {
                    System.out.println("❌ Tạo hóa đơn thất bại cho đơn hàng ID: " + idDonHang);
                }
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi tạo hóa đơn!");
            e.printStackTrace();
        }
    }

    private static void printDanhSachHD(PreparedStatement stmt,List<HoaDon> danhSach , String tieuDe){
        try (ResultSet rs = stmt.executeQuery()) {
        System.out.println("\n============================================== "+ tieuDe+ " ======================================================================");
                System.out.println("=============================================================================================================================================");
                System.out.printf("| %-8s | %-10s | %-15s | %-10s | %-10s | %-15s | %-13s | %-16s | %-16s |\n", 
                                  "ID HD", "ID Đơn", "ID Khuyến mãi","Tổng tiền", "Phải trả", "PT Thanh toán", "Trạng thái", "Ngày tạo HD ", "Ngày TT");
                System.out.println("=============================================================================================================================================");

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

                    HoaDon hoaDon = new HoaDon(idHoaDon, idDonHang, tongTien, phaiTra, ptThanhToan, trangThai, idKhuyenMai, ngayTaoHD, ngayThanhToan);
                    danhSach.add(hoaDon);

                    System.out.printf("| %-8d | %-10d | %-15d | %-10d | %-10d | %-15s | %-10s | %-12s | %-12s |\n",
                                      idHoaDon, idDonHang,idKhuyenMai, tongTien, phaiTra, ptThanhToan, trangThai, ngayTaoHD != null ? ngayTaoHD.toString() : "null",ngayThanhToan != null ? ngayThanhToan.toString() : "null");
                }

                System.out.println("=============================================================================================================================================");
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
            idHoaDon = Integer.parseInt(sc.nextLine());
            if(idHoaDon == 0){
                ThanhToanHoaDonServices.thanhToan(currentUser);
            }

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
        System.out.println("1. Thanh toán ngay");
        System.out.println("2. Áp dụng khuyến mãi");
        System.out.print("Lựa chọn của bạn: ");
        int choice = Integer.parseInt(sc.nextLine());

        switch (choice) {
            case 1:
                thanhToan(idHoaDon, null);  // không áp khuyến mãi
                break;

            case 2:
                // Truy vấn khuyến mãi đang diễn ra tại ngày tạo hóa đơn
                List<KhuyenMai> danhSachKM = layKhuyenMaiTrongNgay(hoaDon.getNgayThanhToan());
                if (danhSachKM.isEmpty()) {
                    System.out.println("Không có khuyến mãi nào phù hợp!");
                    thanhToan(idHoaDon, null);
                    return;
                }

                System.out.println("\n===== Danh sách khuyến mãi khả dụng =====");
                for (int i = 0; i < danhSachKM.size(); i++) {
                    KhuyenMai km = danhSachKM.get(i);
                    System.out.printf("%d. %s (%.0f%%)\n", i + 1, km.getTenChuongTrinh(), km.getPhanTramKM() * 100);
                }

                System.out.print("Chọn khuyến mãi (1-" + danhSachKM.size() + "): ");
                int kmChoice = Integer.parseInt(sc.nextLine());
                KhuyenMai khuyenMaiChon = danhSachKM.get(kmChoice - 1);

                thanhToan(idHoaDon, khuyenMaiChon);
                break;
        }
    }

    // ===================================================================

    public static void thanhToan(int idHoaDon, KhuyenMai khuyenMai) {
        System.out.println("VUI LÒNG CHỌN PHƯƠNG THỨC THANH TOÁN:");
        System.out.println("1. Tiền mặt");
        System.out.println("2. Chuyển khoản ngân hàng");
        System.out.println("3. VNPAY");
        System.out.print("Lựa chọn: ");
        int pt = Integer.parseInt(sc.nextLine());
        String phuongThuc;

        switch (pt) {
            case 1: phuongThuc = "TIEN_MAT"; break;
            case 2: phuongThuc = "CHUYEN_KHOAN"; break;
            case 3: phuongThuc = "VNPAY"; break;
            default: phuongThuc = "TIEN_MAT";
        }

        String sql = "UPDATE hoadon SET TongTien = ?, PTThanhToan = ?, TrangThaiHD = ?, ID_KhuyenMai = ?, NgayThanhToan = ? WHERE ID_HoaDon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Tính tổng tiền (có khuyến mãi hay không)
            int phaiTra = getPhaiTraFromDB(idHoaDon);
            double phanTram = (khuyenMai != null) ? khuyenMai. getPhanTramKM() : 0;
            int tongTienSauGiam = (int)(phaiTra * (1 - phanTram));
            LocalDateTime ngayThanhToan = LocalDateTime.now();

            stmt.setInt(1, tongTienSauGiam);
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
                System.out.println("✅ Vui lòng đợi nhân viên xác nhận thanh toán !");
            } else {
                System.out.println("❌ Không tìm thấy hóa đơn để cập nhật.");
            }

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật hóa đơn!");
            e.printStackTrace();
        }
    }

    // Lấy số tiền phải trả từ bảng hoadon
    private static int getPhaiTraFromDB(int idHoaDon) {
        int phaiTra = 0;
        String sql = "SELECT PhaiTra FROM hoadon WHERE ID_HoaDon = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idHoaDon);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                phaiTra = rs.getInt("PhaiTra");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phaiTra;
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
    public static void capNhatTrangThai(List<HoaDon> danhSachHoaDon, Scanner scanner, String trangThaiMoi) {
        List<Integer> idHoaDons = new ArrayList<>();
        System.out.print("\nNhập danh sách ID hóa đơn cần cập nhật trạng thái (ngăn cách bởi dấu phẩy): ");
        String input = scanner.nextLine();

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
                    System.out.println("===== XÁC NHẬN THANH TOÁN ===== ");
                    System.out.println("1. Xác nhận thanh toán cho hóa đơn ");
                    System.out.println("0. Thoát ");
                    System.out.println("Mời nhập lựa chọn: ");
                    
                    int choice1;
                    if (scanner.hasNextInt()) {
                        choice1 = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                        scanner.next(); 
                        continue;
                    }
                    switch (choice1){
                        case 1: 
                            capNhatTrangThai(danhSachHoaDon, scanner, "DA_THANH_TOAN");
                            break;
                        case 0:
                            locDanhSachHoaDon(currentNV,idChiNhanh,scanner);
                            break;

                    }
                    break;
                case 2:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh, "Danh sách chờ xác nhận", "TrangThaiHD = 'CHO_XAC_NHAN'");
                    System.out.println("===== XÁC NHẬN THANH TOÁN ===== ");
                    System.out.println("1. Xác nhận thanh toán cho hóa đơn ");
                    System.out.println("0. Thoát ");
                    System.out.println("Mời nhập lựa chọn: ");

                    int choice2;
                    if (scanner.hasNextInt()) {
                        choice2 = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                        scanner.next(); 
                        continue;
                    }
                    switch (choice2){
                        case 1: 
                            capNhatTrangThai(danhSachHoaDon, scanner, "DA_THANH_TOAN");
                            break;
                        case 0:
                            locDanhSachHoaDon(currentNV,idChiNhanh,scanner);
                            break;

                    }
                    break;
                case 3:
                    danhSachHoaDon = xemDanhSachHoaDon(currentNV,idChiNhanh,"Danh sách đã thanh toán", "TrangThaiHD = 'DA_THANH_TOAN'");
                    System.out.println("====== XUẤT HÓA ĐƠN ======= ");
                    System.out.println("1. Xuất hóa đơn thanh toán ");
                    System.out.println("0. Thoát ");
                    System.out.println("Mời nhập lựa chọn: ");
                    int choice3;
                    if (scanner.hasNextInt()) {
                        choice3 = scanner.nextInt();
                        scanner.nextLine();
                    } else {
                        System.out.println("Lỗi: Lựa chọn không hợp lệ!");
                        scanner.next(); 
                        continue;
                    }
                    switch (choice3){
                        case 1: 
                            xuatHoaDon(danhSachHoaDon, scanner);
                            break;
                        case 0:
                            locDanhSachHoaDon(currentNV,idChiNhanh,scanner);
                            break;

                    }
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
    public static void xuatHoaDon(List<HoaDon> danhSachHoaDon, Scanner scanner) {
        List<Integer> idHoaDons = new ArrayList<>();
        System.out.print("\nNhập danh sách ID hóa đơn cần xuất hóa đơn (cách nhau bằng dấu phẩy): ");
        String input = scanner.nextLine();
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
                    System.out.printf("%-25s %-10s %-12s %-12s%n", "Tên món", "Số lượng", "Đơn giá", "Thành tiền");
                    while (rs.next()) {
                        String tenMon = rs.getString("TenMon");
                        int soLuong = rs.getInt("SoLuong");
                        int donGia = rs.getInt("dongia");
                        int thanhTien = rs.getInt("thanhtien");
                        System.out.printf("%-25s %-10d %-12d %-12d%n", tenMon, soLuong, donGia, thanhTien);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi lấy danh sách món ăn cho đơn hàng ID: " + hoaDon.getIdDonHang());
                e.printStackTrace();
            }
            System.out.println("\nTỔNG TIỀN                                         " + hoaDon.getTongTien());
            System.out.println("PHẢI TRẢ                                          " + hoaDon.getPhaiTra() +"\n");
        }
    }

}
    