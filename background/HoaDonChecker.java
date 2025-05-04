package background;

import connection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class HoaDonChecker implements Runnable {

    @Override
    public void run() {
        while(true){
            String getDonHangSQL = """
                SELECT dh.ID_DonHang
                FROM donhang dh
                WHERE dh.TrangThai IN ('DANG_CHUAN_BI', 'DA_HOAN_THANH')
            """;

            String getTongTienSQL = """
                SELECT SUM(thanhtien) AS TongTien
                FROM chitietdonhang
                WHERE ID_DonHang = ?
            """;

            String checkHoaDonSQL = "SELECT COUNT(*) AS count FROM hoadon WHERE ID_DonHang = ?";
            
            String insertSQL = """
                INSERT INTO hoadon (ID_DonHang, TongTien, PTThanhToan, TrangThaiHD, NgayTaoHoaDon, PhaiTra)
                VALUES (?, ?, NULL, 'CHUA_THANH_TOAN', ?, ?)
            """;

            String updateSQL = """
                UPDATE hoadon SET TongTien = ?, NgayTaoHoaDon = ?, PhaiTra = ?
                WHERE ID_DonHang = ?
            """;

            try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmtDonHang = conn.prepareStatement(getDonHangSQL);
                PreparedStatement stmtTongTien = conn.prepareStatement(getTongTienSQL);
                PreparedStatement stmtCheckHD = conn.prepareStatement(checkHoaDonSQL);
                PreparedStatement stmtInsert = conn.prepareStatement(insertSQL);
                PreparedStatement stmtUpdate = conn.prepareStatement(updateSQL)) {

                ResultSet rs = stmtDonHang.executeQuery();

                while (rs.next()) {
                    int idDonHang = rs.getInt("ID_DonHang");

                    // Tính tổng tiền đơn hàng
                    stmtTongTien.setInt(1, idDonHang);
                    ResultSet rsTien = stmtTongTien.executeQuery();
                    int tongTien = 0;
                    if (rsTien.next()) {
                        tongTien = rsTien.getInt("TongTien");
                    }

                    LocalDateTime now = LocalDateTime.now();

                    // Kiểm tra đã có hóa đơn chưa
                    stmtCheckHD.setInt(1, idDonHang);
                    ResultSet rsCheck = stmtCheckHD.executeQuery();
                    rsCheck.next();
                    boolean daCoHoaDon = rsCheck.getInt("count") > 0;

                    if (daCoHoaDon) {
                        // Cập nhật hóa đơn
                        stmtUpdate.setInt(1, tongTien);
                        stmtUpdate.setTimestamp(2, Timestamp.valueOf(now));
                        stmtUpdate.setInt(3, tongTien);
                        stmtUpdate.setInt(4, idDonHang);
                        stmtUpdate.executeUpdate();
                    } else {
                        // Tạo mới hóa đơn
                        stmtInsert.setInt(1, idDonHang);
                        stmtInsert.setInt(2, tongTien);
                        stmtInsert.setTimestamp(3, Timestamp.valueOf(now));
                        stmtInsert.setInt(4, tongTien);
                        stmtInsert.executeUpdate();
                    }
                }

            } catch (SQLException e) {
                System.out.println("❌ Lỗi khi tạo/cập nhật hóa đơn.");
                e.printStackTrace();
            }
            sleep10second();
        }
    }

    private void sleep10second(){
        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

