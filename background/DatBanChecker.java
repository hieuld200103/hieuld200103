package background;

import service.BanAnServices;
import service.DatBanServices;
import model.DatBan;
import model.NhanVien;
import service.NhanVienServices;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DatBanChecker implements Runnable {
    @Override
    public void run() {
        while (true) {
            boolean coDonChoXacNhan = false;

            List<DatBan> danhSachDatBan = DatBanServices.xemDanhSach();
            LocalDateTime now = LocalDateTime.now();

            List<DatBan> banDuocXacNhan = new ArrayList<>();
            List<DatBan> banBiHuy = new ArrayList<>();

            for (DatBan datBan : danhSachDatBan) {
                LocalDateTime gioDat = datBan.getNgayDat();
                if (gioDat == null) continue;

                if (datBan.getTrangThai() == DatBan.TrangThai.CHO_XAC_NHAN) {
                    coDonChoXacNhan = true;
                    break; 
                }
        
                long tgBooking = Duration.between(now, gioDat).toMinutes();
                long tgCho = Duration.between(gioDat, now).toMinutes();
                String listBan =  datBan.getListID_BanAn();
                String[] idBan = listBan.split(",");
        
                for (String list : idBan){
                    int id = Integer.parseInt(list.trim());
                    if (datBan.getTrangThai() == DatBan.TrangThai.DA_XAC_NHAN) {
                        if (tgBooking <= 90 && tgCho <= 30) {
                            BanAnServices.capNhatTrangThai(id, "DA_DAT");
                            banDuocXacNhan.add(datBan);
                        } else if (tgCho > 30) {
                            DatBanServices.capNhatTrangThai(datBan.getID_DatBan(), DatBan.TrangThai.DA_HUY);
                            BanAnServices.capNhatTrangThai(id, "TRONG");
                            banBiHuy.add(datBan);
                        }
                    }
                }
               
            }

            if (coDonChoXacNhan) {
                NhanVien currentNV = NhanVienServices.getCurrentNV();
                if(currentNV != null){
                    DatBanServices.thongBao(currentNV);                }
                
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
    Set<String> idSet = new LinkedHashSet<>(); 
    for (DatBan datBan : danhSach) {
        String[] idBans = datBan.getListID_BanAn().split(",");
        for (String id : idBans) {
            idSet.add(id.trim());
        }
    }
    return String.join(" ", idSet);
}
    

}
