package service;
import connection.DatabaseConnection;
import java.sql.*;
import java.time.*;
import java.util.Scanner;

public class DoanhThuServices {
    public static void printThongKe(ResultSet rs, String tieuDe) throws SQLException {
    int tongDoanhThu = 0;
    boolean coDuLieu = false;
    System.out.printf("\n======== " + tieuDe + "========\n");
    System.out.println("+------------------+----------------------+");
    System.out.printf("| %-16s | %-20s |\n", "Kiểu đơn hàng", "Doanh thu (VND)");
    System.out.println("+------------------+----------------------+");

    while (rs.next()) {
        coDuLieu = true;
        String kieuDonHang = rs.getString("kieudonhang");
        int doanhThu = rs.getInt("TongDoanhThu");
        tongDoanhThu += doanhThu;
        System.out.printf("| %-16s | %,20d |\n", kieuDonHang, doanhThu);
    }

    if (coDuLieu) {
        System.out.println("+------------------+----------------------+");
        System.out.printf("| %-16s | %,20d |\n", "TỔNG", tongDoanhThu);
        System.out.println("+------------------+----------------------+");
    } else {
        System.out.printf("⚠️ Không có dữ liệu " + tieuDe);
    }
}

    public static void thongKeTheoQuy(int ID_ChiNhanh, Scanner scanner) {
        System.out.println("\n=== THỐNG KÊ DOANH THU THEO QUÝ ===");
        System.out.println("Nhập danh sách quý-năm cần thống kê (ví dụ: 1-2024,2-2025,4-2023): ");
        String input = scanner.nextLine();
        String[] pairs = input.split(",");

        String sql = """
            SELECT dh.kieudonhang, SUM(hd.PhaiTra) AS TongDoanhThu
            FROM hoadon hd
            JOIN donhang dh ON hd.ID_DonHang = dh.ID_DonHang
            WHERE dh.ID_ChiNhanh = ?
            AND hd.TrangThaiHD = 'DA_THANH_TOAN'
            AND MONTH(hd.NgayThanhToan) BETWEEN ? AND ?
            AND YEAR(hd.NgayThanhToan) = ?
            GROUP BY dh.kieudonhang
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String pair : pairs) {
                pair = pair.trim();
                if (!pair.matches("\\d{1}-\\d{4}")) {
                    System.out.println("❌ Định dạng sai ở mục: " + pair + " (đúng định dạng là quý-năm, ví dụ: 2-2025)");
                    continue;
                }

                String[] parts = pair.split("-");
                int quy = Integer.parseInt(parts[0]);
                int nam = Integer.parseInt(parts[1]);

                if (quy < 1 || quy > 4) {
                    System.out.println("⚠️ Quý không hợp lệ: " + quy);
                    continue;
                }

                int thangBatDau = (quy - 1) * 3 + 1;
                int thangKetThuc = thangBatDau + 2;

                stmt.setInt(1, ID_ChiNhanh);
                stmt.setInt(2, thangBatDau);
                stmt.setInt(3, thangKetThuc);
                stmt.setInt(4, nam);
                
                String tieuDe = String.format("Doanh thu Quý %d/%d - Chi nhánh %d:", quy, nam, ID_ChiNhanh);
                ResultSet rs = stmt.executeQuery();
                printThongKe(rs, tieuDe);
            }

        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thống kê doanh thu:");
            e.printStackTrace();
        }
    }


    public static void thongKeTheoThang(int ID_ChiNhanh, Scanner scanner) {
        System.out.println("\n=== THỐNG KÊ DOANH THU THEO THÁNG ===");
        System.out.println("Nhập danh sách tháng-năm cần thống kê (ví dụ: 1-2024,10-2025,11-2023): ");
        String input = scanner.nextLine();
        String[] pairs = input.split(",");

        String sql = """
            SELECT dh.kieudonhang, SUM(hd.PhaiTra) AS TongDoanhThu
            FROM hoadon hd
            JOIN donhang dh ON hd.ID_DonHang = dh.ID_DonHang
            WHERE dh.ID_ChiNhanh = ?
            AND hd.TrangThaiHD = 'DA_THANH_TOAN'
            AND MONTH(hd.NgayThanhToan) = ?
            AND YEAR(hd.NgayThanhToan) = ?
            GROUP BY dh.kieudonhang
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (String pair : pairs) {
                pair = pair.trim();
                if (!pair.matches("\\d{1,2}-\\d{4}")) {
                    System.out.println("❌ Định dạng sai ở mục: " + pair + " (đúng định dạng là tháng-năm, ví dụ: 10-2025)");
                    continue;
                }

                String[] parts = pair.split("-");
                int thang = Integer.parseInt(parts[0]);
                int nam = Integer.parseInt(parts[1]);

                if (thang < 1 || thang > 12) {
                    System.out.println("⚠️ Tháng không hợp lệ: " + thang);
                    continue;
                }

                stmt.setInt(1, ID_ChiNhanh);
                stmt.setInt(2, thang);
                stmt.setInt(3, nam);

                String tieuDe = String.format("Doanh thu tháng %d/%d - Chi nhánh %d:", thang, nam, ID_ChiNhanh);
                ResultSet rs = stmt.executeQuery();
                printThongKe(rs, tieuDe);

            }
        } catch (SQLException e) {
            System.out.println("❌ Lỗi khi thống kê doanh thu:");
            e.printStackTrace();
        }
    }

    public static void thongKeTheoNgay(int ID_ChiNhanh, Scanner scanner) {
        System.out.println("\n=== THỐNG KÊ DOANH THU THEO NGÀY ===");
        System.out.print("Nhập ngày bắt đầu (yyyy-MM-dd): ");
        String startDateInput = scanner.nextLine().trim();

        System.out.print("Nhập ngày kết thúc (yyyy-MM-dd) [có thể bỏ trống]: ");
        String endDateInput = scanner.nextLine().trim();

        LocalDate startDate;
        LocalDate endDate;

        try {
            startDate = LocalDate.parse(startDateInput);
            if (endDateInput.isEmpty()) {
                endDate = startDate;  // Nếu bỏ trống, chỉ thống kê 1 ngày
            } else {
                endDate = LocalDate.parse(endDateInput);
                if (endDate.isBefore(startDate)) {
                    System.out.println("❌ Ngày kết thúc không thể trước ngày bắt đầu.");
                    return;
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Lỗi định dạng ngày. Hãy nhập theo dạng yyyy-MM-dd.");
            return;
        }

        String sql = "SELECT dh.kieudonhang, SUM(hd.PhaiTra) AS TongDoanhThu " +
                     "FROM hoadon hd " +
                     "JOIN donhang dh ON hd.ID_DonHang = dh.ID_DonHang " +
                     "WHERE dh.ID_ChiNhanh = ? " +
                     "AND hd.TrangThaiHD = 'DA_THANH_TOAN' " +
                     "AND DATE(hd.NgayThanhToan) BETWEEN ? AND ?" +
                     "GROUP BY dh.kieudonhang";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ID_ChiNhanh);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));

            String tieuDe = String.format("Doanh thu từ %s đến %s:%n", startDate, endDate);
            ResultSet rs = stmt.executeQuery();
            printThongKe(rs, tieuDe);

        } 
        catch (SQLException e) {
            System.out.println("❌ Lỗi truy vấn CSDL.");
            e.printStackTrace();
        }
    }
    
    public static void thongKeTheoKhungGio(int ID_ChiNhanh, Scanner scanner) {
        System.out.println("\n=== THỐNG KÊ DOANH THU THEO KHUNG GIỜ TRONG NHIỀU NGÀY ===");

        System.out.print("Nhập ngày bắt đầu (yyyy-MM-dd): ");
        String startDateInput = scanner.nextLine().trim();

        System.out.print("Nhập ngày kết thúc (yyyy-MM-dd) [có thể bỏ trống]: ");
        String endDateInput = scanner.nextLine().trim();

        System.out.print("Nhập giờ bắt đầu (HH:mm): ");
        String startTimeInput = scanner.nextLine().trim();

        System.out.print("Nhập giờ kết thúc (HH:mm): ");
        String endTimeInput = scanner.nextLine().trim();

        LocalDate startDate;
        LocalDate endDate;
        LocalTime startTime;
        LocalTime endTime;

        try {
            startDate = LocalDate.parse(startDateInput);

            if (endDateInput.isEmpty()) {
                endDate = startDate;
            } else {
                endDate = LocalDate.parse(endDateInput);
                if (endDate.isBefore(startDate)) {
                    System.out.println("❌ Ngày kết thúc không thể trước ngày bắt đầu.");
                    return;
                }
            }

            startTime = LocalTime.parse(startTimeInput);
            endTime = LocalTime.parse(endTimeInput);

            if (endTime .isBefore(startTime)) {
                System.out.println("❌ Giờ kết thúc không thể trước giờ bắt đầu.");
                return;
            }
        } catch (Exception e) {
            System.out.println("❌ Định dạng ngày hoặc giờ không hợp lệ.");
            return;
        }

            // Ghép ngày + giờ để tạo Timestamp
            String sql = """
                SELECT dh.kieudonhang, SUM(hd.PhaiTra) AS TongDoanhThu
                FROM hoadon hd
                JOIN donhang dh ON hd.ID_DonHang = dh.ID_DonHang
                WHERE dh.ID_ChiNhanh = ?
                AND hd.TrangThaiHD = 'DA_THANH_TOAN'
                AND TIME(hd.NgayThanhToan) BETWEEN ? AND ?
                AND DATE(hd.NgayThanhToan) BETWEEN ? AND ?
                GROUP BY dh.kieudonhang
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, ID_ChiNhanh);
                stmt.setTime(2, Time.valueOf(startTime));
                stmt.setTime(3, Time.valueOf(endTime));
                stmt.setDate(4, Date.valueOf(startDate));
                stmt.setDate(5, Date.valueOf(endDate));

                String tieuDe = String.format("Doanh thu từ %s đến %s trong khung giờ %s – %s:",
                        startDate, endDate, startTime, endTime);

                ResultSet rs = stmt.executeQuery();
                printThongKe(rs, tieuDe);

            } catch (SQLException e) {
                System.out.println("❌ Lỗi truy vấn CSDL.");
                e.printStackTrace();
            }
        }
    }
