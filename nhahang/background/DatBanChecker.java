package background;

import service.BanAnServices;
import service.DatBanServices;
import model.DatBan;
import model.NhanVien;
import service.NhanVienServices;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatBanChecker implements Runnable {
    @Override
    public void run() {
        while (true) {
            NhanVien currentNV = NhanVienServices.getCurrentNV();

            if (currentNV == null) {
                sleepOneMinute();
                continue;
            }

            List<DatBan> danhSachDatBan = DatBanServices.xemDanhSach();
            LocalDateTime now = LocalDateTime.now();

            List<DatBan> banDuocXacNhan = new ArrayList<>();
            List<DatBan> banBiHuy = new ArrayList<>();

            for (DatBan datBan : danhSachDatBan) {
                LocalDateTime gioDat = datBan.getNgayDat();
                if (gioDat == null) continue;

                long tgBooking = Duration.between(now, gioDat).toMinutes();
                long tgCho = Duration.between(gioDat, now).toMinutes();

                if (tgBooking <= 90 && tgCho <= 30) {
                    if (datBan.getTrangThai() == DatBan.TrangThai.DA_XAC_NHAN) {
                        BanAnServices.capNhatTrangThai(datBan.getID_BanAn(), "DA_DAT");
                        banDuocXacNhan.add(datBan);
                    }
                }

                if (tgCho > 30 && datBan.getTrangThai() == DatBan.TrangThai.DA_XAC_NHAN) {
                    DatBanServices.capNhatTrangThai(datBan.getID_DatBan(), DatBan.TrangThai.DA_HUY);
                    BanAnServices.capNhatTrangThai(datBan.getID_BanAn(), "TRONG");
                    banBiHuy.add(datBan);
                }
            }

            if (!banDuocXacNhan.isEmpty()) {
                
                System.out.println("\n✅ Các bàn đã được đặt: " + inDanhSach(banDuocXacNhan));
            }
            if (!banBiHuy.isEmpty()) {
                System.out.println("\n❌ Các bàn bị huỷ: " + inDanhSach(banBiHuy));
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
        StringBuilder sb = new StringBuilder();
        for (DatBan datBan : danhSach) {
            sb.append(datBan.getID_BanAn()).append(" ");
        }
        return sb.toString().trim();
    }

}
