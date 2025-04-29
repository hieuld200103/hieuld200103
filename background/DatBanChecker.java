package background;

import model.DatBan;
import model.KhachHang;
import model.NhanVien;
import service.NhanVienServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import connection.DatabaseConnection;

public class DatBanChecker implements Runnable {
    @Override
    public void run() {
        while (true) {
            List<DatBan> danhSachDatBan = xemDanhSachDatBan();
            List<KhachHang> danhSachKhachHang = xemDanhSachKhachHang();
            LocalDateTime now = LocalDateTime.now();

            List<DatBan> banDuocXacNhan = new ArrayList<>();
            List<DatBan> banBiHuy = new ArrayList<>();
            List<KhachHang> danhSachKH = new ArrayList<>();
            
            boolean coDonChoXacNhan = false;

            for (DatBan datBan : danhSachDatBan) {
                LocalDateTime gioAn = datBan.getNgayAn();
                if (gioAn == null) continue;

                if (datBan.getTrangThai() == DatBan.TrangThai.CHO_XAC_NHAN) {
                    coDonChoXacNhan = true;
                    break; 
                }
        
                long tgBooking = Duration.between(now, gioAn).toMinutes();
                long tgCho = Duration.between(gioAn, now).toMinutes();
                String listBan =  datBan.getListID_BanAn();
                String[] idBan = listBan.split(",");
        
                for (String list : idBan){
                    int id = Integer.parseInt(list.trim());
                    if (datBan.getTrangThai() == DatBan.TrangThai.DA_XAC_NHAN) {
                        if (tgBooking <= 90 && tgCho <= 30) {
                            capNhatTrangThai(id, "DA_DAT");
                            banDuocXacNhan.add(datBan);
                        } else if (tgCho > 30) {
                            capNhatTrangThai(datBan.getID_DatBan(), DatBan.TrangThai.DA_HUY);
                            capNhatTrangThai(id, "TRONG");
                            banBiHuy.add(datBan);
                        }
                    }
                }
            }

            for(KhachHang khachHang : danhSachKhachHang){
                int idUser = khachHang.getID_User();
                if(daNhanBan(idUser)){
                    // BanAnServices.capNhatTrangThai(, null);
                }
            }
            
            if (coDonChoXacNhan) {
                NhanVien currentNV = NhanVienServices.getCurrentNV();
                if(currentNV != null){
                    thongBao(currentNV);
                }                
            }

            if (!banDuocXacNhan.isEmpty()) {
                
                System.out.println("\n Các bàn đã được đặt: " + inDanhSach(banDuocXacNhan));
            }
            if (!banBiHuy.isEmpty()) {
                System.out.println("\n Các bàn bị huỷ: " + inDanhSach(banBiHuy));
            }

            sleepOneMinute();
        }
    }

    private void sleepOneMinute() {
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String inDanhSach(List<DatBan> danhSach) {
        Set<String> idSet = new LinkedHashSet<>(); 
        for (DatBan datBan : danhSach) {
            String[] idBans = datBan.getListID_BanAn().split(",");
            for (String id : idBans) {
                idSet.add(id.trim());
            }
        }
        return String.join(" ", idSet);
    }

    //Cập nhật theo checker
    public static void capNhatTrangThai(int idBan, String trangThaiMoi) {
        String sql = "UPDATE banan SET TrangThai = ? WHERE ID_BanAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi);
            stmt.setInt(2, idBan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật trạng thái bàn.");
            e.printStackTrace();
        }
    }

    //Cập nhật theo checker
    public static void capNhatTrangThai(int idDatBan, DatBan.TrangThai trangThaiMoi) {
        String sql = "UPDATE datban SET TrangThai = ? WHERE ID_DatBan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, trangThaiMoi.name());
            stmt.setInt(2, idDatBan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật trạng thái đặt bàn.");
            e.printStackTrace();
        }
    }

    //Danh sách để checker
    public static List<DatBan> xemDanhSachDatBan() {
        List<DatBan> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM datban";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
    
            while (rs.next()) {
                int id = rs.getInt("ID_DatBan");
                int idCN = rs.getInt("ID_ChiNhanh");
                int idUser = rs.getInt("ID_User");
                String idBanAn = rs.getString("List_BanAn");
                LocalDateTime ngayDat = rs.getTimestamp("NgayDat").toLocalDateTime();
                LocalDateTime ngayAn = rs.getTimestamp("NgayAn").toLocalDateTime();
                DatBan.TrangThai trangThai = DatBan.TrangThai.valueOf(rs.getString("TrangThai"));
    
                DatBan datBan = new DatBan(id, idCN, idUser, idBanAn, ngayDat, ngayAn, trangThai);
                danhSach.add(datBan);
        } 
        rs.close();
        }catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách đặt bàn!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
    
    //Thông báo có đơn chờ xác nhận
    public static void thongBao(NhanVien currentNV){
        int idChiNhanh = currentNV.getID_ChiNhanh();
        String sql = "SELECT COUNT(*) AS soDon FROM datban WHERE ID_ChiNhanh = ? AND TrangThai = 'CHO_XAC_NHAN'"; 
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idChiNhanh);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()){
                    int soDon = rs.getInt("soDon");
                    if(soDon>0){
                        System.out.println("!!!Có "+soDon+" đơn hàng đang chờ xác nhận!!!");
                    }else{
                        System.out.println("Không có đơn đặt bàn nào đang chờ!");
                    }
                }            
        } catch (SQLException e) {
            System.out.println("Lỗi khi kiểm tra đơn đặt bàn chờ xác nhận.");
            e.printStackTrace();
        }    
    }

    //Xem danh sách khách hàng
    public static List<KhachHang> xemDanhSachKhachHang() {
        List<KhachHang> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM khachhang";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("ID_KhachHang");
                int idUser = rs.getInt("ID_User");
                String tenKH = rs.getString("TenKH");
                String sdt = rs.getString("SDT");
                KhachHang.TrangThai trangThai = KhachHang.TrangThai.valueOf(rs.getString("TrangThai"));
                danhSach.add(new KhachHang(id,idUser, tenKH, sdt,trangThai));
            }
        rs.close();
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách khách hàng!");
            e.printStackTrace();
        }
        return danhSach;
    }

    //Check khách đã nhận bàn
    public static boolean daNhanBan(int idUser) {
        String sql = "SELECT * FROM khachhang WHERE ID_User = ? AND TrangThai = 'DA_NHAN_BAN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idUser);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Lỗi kiểm tra trạng thái nhận bàn!");
            e.printStackTrace();
        }
        return false;
    }
}
