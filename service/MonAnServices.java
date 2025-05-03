package service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import connection.DatabaseConnection;
import model.MonAn.DanhMucMonAn;
import model.MonAn.LoaiMonAn;
import model.MonAn;

public class MonAnServices {
    //Thêm món ăn
    public static void themMonAn(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Thêm món ăn mới (nhấn 0 để thoát) ===");
            System.out.print("Tên món ăn: ");
            String tenMon = scanner.nextLine().trim();
            if (tenMon.equals("0")) {
                System.out.println("Đã thoát khỏi chức năng thêm món ăn.");
                break;
            }
            if (tenMon.isEmpty()) {
                System.out.println("Lỗi: Tên món ăn không được để trống! Vui lòng nhập lại.");
                continue;
            }
    
            int gia;
            while (true) {
                System.out.print("Giá: ");
                String giaInput = scanner.nextLine().trim();
                if (giaInput.equals("0")) return;
                try {
                    gia = Integer.parseInt(giaInput);
                    if (gia > 0) {
                        break;
                    } else {
                        System.out.println("Lỗi: Giá phải lớn hơn 0! Vui lòng nhập lại.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Lỗi: Vui lòng nhập một số hợp lệ!");
                }
            }
    
            System.out.print("Mô tả: ");
            String moTa = scanner.nextLine();
    
            LoaiMonAn loaiMonAn = null;
            Map<String, LoaiMonAn> loaiMonAnMap = new HashMap<>();
            loaiMonAnMap.put("KHAI_VI", LoaiMonAn.KHAI_VI);
            loaiMonAnMap.put("MON_CHINH", LoaiMonAn.MON_CHINH);
            loaiMonAnMap.put("TRANG_MIENG", LoaiMonAn.TRANG_MIENG);
            loaiMonAnMap.put("DO_UONG", LoaiMonAn.DO_UONG);
    
            while (true) {
                System.out.print("Loại món ăn (KHAI_VI / MON_CHINH / TRANG_MIENG / DO_UONG): ");
                String loaiMonAnInput = scanner.nextLine().trim().toUpperCase();
                if (loaiMonAnInput.equals("0")) return;
    
                if (loaiMonAnMap.containsKey(loaiMonAnInput)) {
                    loaiMonAn = loaiMonAnMap.get(loaiMonAnInput);
                    break;
                } else {
                    System.out.println("Lỗi: Vui lòng nhập đúng một trong các loại: KHAI_VI, MON_CHINH, TRANG_MIENG, DO_UONG.");
                }
            }
    
            Map<LoaiMonAn, List<DanhMucMonAn>> danhMucTheoLoai = new HashMap<>();
            danhMucTheoLoai.put(LoaiMonAn.KHAI_VI, Arrays.asList(DanhMucMonAn.SOUP, DanhMucMonAn.SALAD, DanhMucMonAn.DO_CHIEN));
            danhMucTheoLoai.put(LoaiMonAn.MON_CHINH, Arrays.asList(DanhMucMonAn.MON_BO, DanhMucMonAn.MON_GA, DanhMucMonAn.HAI_SAN));
            danhMucTheoLoai.put(LoaiMonAn.TRANG_MIENG, Arrays.asList(DanhMucMonAn.BANH_NGOT, DanhMucMonAn.KEM, DanhMucMonAn.HOA_QUA));
            danhMucTheoLoai.put(LoaiMonAn.DO_UONG, Arrays.asList(DanhMucMonAn.RUOU, DanhMucMonAn.NUOC_NGOT));
    
            DanhMucMonAn danhMuc = null;
            while (true) {
                List<DanhMucMonAn> danhMucHopLe = danhMucTheoLoai.get(loaiMonAn);
                System.out.print("Danh mục hợp lệ: ");
                for (DanhMucMonAn d : danhMucHopLe) {
                    System.out.print(d.name() + " / ");
                }
                System.out.print("\nNhập danh mục: ");
                String input = scanner.nextLine().trim().toUpperCase();
                if (input.equals("0")) return;
    
                try {
                    DanhMucMonAn danhMucTam = DanhMucMonAn.valueOf(input);
                    if (danhMucHopLe.contains(danhMucTam)) {
                        danhMuc = danhMucTam;
                        break;
                    } else {
                        System.out.println("Lỗi: Danh mục không hợp lệ cho loại món ăn này.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Lỗi: Không tồn tại danh mục " + input);
                }
            }
    
            String sql = "INSERT INTO monan (TenMon, Gia, Mota, LoaiMonAn, DanhMuc) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tenMon);
                stmt.setInt(2, gia);
                stmt.setString(3, moTa);
                stmt.setString(4, loaiMonAn.name());
                stmt.setString(5, danhMuc.name());
                stmt.executeUpdate();
    
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    System.out.println("Thêm món ăn thành công với ID: " + id);
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi thêm món ăn!");
                e.printStackTrace();
            }
    
            System.out.println("Bạn có thể tiếp tục thêm món khác hoặc nhấn 0 để thoát.\n");
        }
    }
    
    // Xóa thông tin món ăn
    public static void xoaMonAn(Scanner scanner){
        System.out.print("Nhập ID món ăn cần xóa: ");
        if(!scanner.hasNextInt()){
            System.out.println("Lỗi: ID không hợp lệ");
            scanner.next();
            return;
        }

        int idMA = scanner.nextInt();
        scanner.nextLine();

        String sql = "DELETE FROM monan WHERE ID_MonAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setInt(1,idMA);
                int rowsAffected = stmt.executeUpdate();
                if(rowsAffected >0){
                    System.out.println("Xóa món ăn thành công");
                }else{
                    System.out.println("Không tìm thấy món ăn có ID "+ idMA);
                }
            }catch (SQLException e){
                System.out.println("Lỗi khi xóa món ăn");
                e.printStackTrace();
            }
    }

    //Sửa món ăn
    public static void suaMonAn(Scanner scanner) {
        xemMenu("Danh sách món ăn", null);
        System.out.print("Nhập ID món ăn cần sửa: ");
        if (!scanner.hasNextInt()) {
            System.out.println("Lỗi: ID không hợp lệ!");
            scanner.next();
            return;
        }
    
        int idMA = scanner.nextInt();
        scanner.nextLine();
    
        String sql = "SELECT * FROM monan WHERE ID_MonAn = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
    
            stmt.setInt(1, idMA);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Không có món ăn có ID: " + idMA);
                    return;
                }
    
                String tenMon = rs.getString("TenMon");
                int gia = rs.getInt("Gia");
                String moTa = rs.getString("Mota");
                String loaiMonAnStr = rs.getString("LoaiMonAn");
                String danhMucStr = rs.getString("DanhMuc");
    
                System.out.println("Các thông tin có thể sửa:");
                System.out.println("1. Tên món: " + tenMon);
                System.out.println("2. Giá: " + gia);
                System.out.println("3. Mô tả: " + moTa);
                System.out.println("4. Loại món ăn: " + loaiMonAnStr);
                System.out.println("5. Danh mục: " + danhMucStr);
    
                System.out.print("Chọn mục cần sửa: ");
                int choice = scanner.nextInt();
                scanner.nextLine();
    
                String field = null;
                Object newValue = null;
    
                switch (choice) {
                    case 1:
                        field = "TenMon";
                        System.out.print("Nhập tên mới: ");
                        newValue = scanner.nextLine().trim();
                        if (((String) newValue).isEmpty()) {
                            System.out.println("Lỗi: Tên món không được để trống.");
                            return;
                        }
                        break;
                    case 2:
                        field = "Gia";
                        System.out.print("Nhập giá mới: ");
                        try {
                            int newGia = Integer.parseInt(scanner.nextLine().trim());
                            if (newGia <= 0) {
                                System.out.println("Lỗi: Giá phải lớn hơn 0.");
                                return;
                            }
                            newValue = newGia;
                        } catch (NumberFormatException e) {
                            System.out.println("Lỗi: Giá không hợp lệ.");
                            return;
                        }
                        break;
                    case 3:
                        field = "Mota";
                        System.out.print("Nhập mô tả mới: ");
                        newValue = scanner.nextLine().trim();
                        break;
                    case 4:
                        field = "LoaiMonAn";
                        Map<String, LoaiMonAn> loaiMap = new HashMap<>();
                        loaiMap.put("KHAI_VI", LoaiMonAn.KHAI_VI);
                        loaiMap.put("MON_CHINH", LoaiMonAn.MON_CHINH);
                        loaiMap.put("TRANG_MIENG", LoaiMonAn.TRANG_MIENG);
                        loaiMap.put("DO_UONG", LoaiMonAn.DO_UONG);
                        while (true) {
                            System.out.print("Loại món ăn mới (KHAI_VI / MON_CHINH / TRANG_MIENG / DO_UONG): ");
                            String input = scanner.nextLine().trim().toUpperCase();
                            if (loaiMap.containsKey(input)) {
                                newValue = input;
                                break;
                            } else {
                                System.out.println("Lỗi: Loại không hợp lệ.");
                            }
                        }
                        break;
                    case 5:
                        field = "DanhMuc";
                        LoaiMonAn loaiHienTai = LoaiMonAn.valueOf(loaiMonAnStr);
                        Map<LoaiMonAn, List<DanhMucMonAn>> danhMucTheoLoai = new HashMap<>();
                        danhMucTheoLoai.put(LoaiMonAn.KHAI_VI, Arrays.asList(DanhMucMonAn.SOUP, DanhMucMonAn.SALAD));
                        danhMucTheoLoai.put(LoaiMonAn.MON_CHINH, Arrays.asList(DanhMucMonAn.MON_BO, DanhMucMonAn.MON_GA, DanhMucMonAn.HAI_SAN));
                        danhMucTheoLoai.put(LoaiMonAn.TRANG_MIENG, Arrays.asList(DanhMucMonAn.BANH_NGOT, DanhMucMonAn.KEM, DanhMucMonAn.HOA_QUA));
                        danhMucTheoLoai.put(LoaiMonAn.DO_UONG, Arrays.asList(DanhMucMonAn.RUOU, DanhMucMonAn.NUOC_NGOT));
    
                        List<DanhMucMonAn> danhMucHopLe = danhMucTheoLoai.get(loaiHienTai);
                        while (true) {
                            System.out.print("Danh mục mới " + danhMucHopLe + ": ");
                            String input = scanner.nextLine().trim().toUpperCase();
                            try {
                                DanhMucMonAn danhMucMoi = DanhMucMonAn.valueOf(input);
                                if (danhMucHopLe.contains(danhMucMoi)) {
                                    newValue = danhMucMoi.name();
                                    break;
                                } else {
                                    System.out.println("Lỗi: Danh mục không hợp lệ.");
                                }
                            } catch (IllegalArgumentException e) {
                                System.out.println("Lỗi: Không tồn tại danh mục này.");
                            }
                        }
                        break;
                    default:
                        System.out.println("Lựa chọn không hợp lệ.");
                        return;
                }
    
                String sqlUpdate = "UPDATE monan SET " + field + " = ? WHERE ID_MonAn = ?";
                try (PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)) {
                    stmtUpdate.setObject(1, newValue);
                    stmtUpdate.setInt(2, idMA);
                    stmtUpdate.executeUpdate();
                    System.out.println("Cập nhật thành công!");
                } catch (SQLException e) {
                    System.out.println("Lỗi khi cập nhật dữ liệu.");
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu.");
            e.printStackTrace();
        }
    }
    
    //==============XEM MENU====================
    public static void menuXemMonAn(Scanner scanner) {
        while (true) {
            System.out.println("\n============= XEM MENU =============");
            System.out.println("1. Món khai vị");
            System.out.println("2. Món chính");
            System.out.println("3. Món tráng miệng");
            System.out.println("4. Đồ uống");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
    
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next(); 
                continue;
            }
    
            int choice = scanner.nextInt();
            scanner.nextLine();
    
            switch (choice) {
                case 1:
                    xemMenu("Danh sách món khai vị", "LoaiMonAn = 'KHAI_VI'");
                    break;
                case 2:
                    xemMenu("Danh sách món chính", "LoaiMonAn = 'MON_CHINH'");
                    break;
                case 3:
                    xemMenu("Danh sách món tráng miệng", "LoaiMonAn = 'TRANG_MIENG'");
                    break;
                case 4:
                    xemMenu("Danh sách đồ uống", "LoaiMonAn = 'DO_UONG'");
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Lỗi: Vui lòng chọn từ 0 đến 4.");
            }
        }
    }
    
    //Menu
    public static List<MonAn> xemMenu(String tieuDe, String dieuKienWhere) {
        List<MonAn> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM monan";
    
        if (dieuKienWhere != null && !dieuKienWhere.trim().isEmpty()) {
            sql += " WHERE " + dieuKienWhere;
        }
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            int stt = 1;
            System.out.println("\n=================================================== " + tieuDe.toUpperCase() + " ===================================================");
            System.out.println("=====================================================================================================================================");
            System.out.printf("| %-3s | %-5s | %-25s | %-12s | %-40s | %-15s | %-15s |\n", "STT","ID", "Tên Món", "Giá", "Mô tả", "Loại món", "Danh mục");
    
            while (rs.next()) {
                int id = rs.getInt("ID_MonAn");
                String tenMon = rs.getString("TenMon");
                int gia = rs.getInt("Gia");
                String moTa = rs.getString("Mota");
                LoaiMonAn loaiMonAn = LoaiMonAn.valueOf(rs.getString("LoaiMonAn"));
                DanhMucMonAn danhMuc = DanhMucMonAn.valueOf(rs.getString("DanhMuc"));
    
                MonAn monAn = new MonAn(id, tenMon, gia, moTa, loaiMonAn, danhMuc);
                danhSach.add(monAn);
    
                System.out.println("------------------------------------------------------------------------------------------------------------------------------------");
                System.out.printf("| %-3d | %-5d | %-25s | %-12d | %-40s | %-15s | %-15s |\n",stt++, id, tenMon.length() > 25 ? tenMon.substring(0, 22)+"...":tenMon, gia, moTa.length() > 40 ? moTa.substring(0, 37) + "..." : moTa, loaiMonAn, danhMuc);
            }
    
            System.out.println("======================================================================================================================================");
    
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách món ăn!");
            e.printStackTrace();
        }
    
        return danhSach;
    }
    

    //Tìm kiếm món ăn
    public static List<MonAn> timKiemMonAn(Scanner scanner) {
        while (true) {
            System.out.println("\n======= TÌM KIẾM =======");
            System.out.println("1. Tìm theo loại món ăn");
            System.out.println("2. Tìm theo danh mục món ăn");
            System.out.println("0. THOÁT");
            System.out.print("Chọn: ");
    
            if (!scanner.hasNextInt()) {
                System.out.println("Lỗi: Vui lòng nhập số hợp lệ!");
                scanner.next();
                continue;
            }
    
            int luaChon = scanner.nextInt();
            scanner.nextLine();
    
            switch (luaChon) {
                case 0:
                    return new ArrayList<>();
                case 1:
                    return timTheoLoaiMonAn(scanner);
                case 2:
                    return timTheoDanhMuc(scanner);
                default:
                    System.out.println("Lỗi: Lựa chọn không hợp lệ.");
            }
        }
    }
    
    //Tìm kiếm theo loại món ăn
    public static List<MonAn> timTheoLoaiMonAn(Scanner scanner) {
        Map<Integer, LoaiMonAn> loaiMap = new LinkedHashMap<>();
        loaiMap.put(1, LoaiMonAn.KHAI_VI);
        loaiMap.put(2, LoaiMonAn.MON_CHINH);
        loaiMap.put(3, LoaiMonAn.TRANG_MIENG);
        loaiMap.put(4, LoaiMonAn.DO_UONG);

        System.out.println("\n======= TÌM THEO LOẠI MÓN ĂN =======");
        for (Map.Entry<Integer, LoaiMonAn> entry : loaiMap.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
        System.out.println("0. THOÁT");
        System.out.print("Chọn: ");

        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 0) return new ArrayList<>();

        LoaiMonAn loai = loaiMap.get(choice);
        if (loai == null) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ.");
            return new ArrayList<>();
        }

        return layMonAnTheoTruong("LoaiMonAn", loai.name());
    }

    //Tìm kiếm theo danh mục
    public static List<MonAn> timTheoDanhMuc(Scanner scanner) {
        Map<Integer, DanhMucMonAn> danhMucMap = new LinkedHashMap<>();
        danhMucMap.put(1, DanhMucMonAn.SOUP);
        danhMucMap.put(2, DanhMucMonAn.SALAD);
        danhMucMap.put(3, DanhMucMonAn.MON_BO);
        danhMucMap.put(4, DanhMucMonAn.MON_GA);
        danhMucMap.put(5, DanhMucMonAn.HAI_SAN);
        danhMucMap.put(6, DanhMucMonAn.BANH_NGOT);
        danhMucMap.put(7, DanhMucMonAn.KEM);
        danhMucMap.put(8, DanhMucMonAn.HOA_QUA);
        danhMucMap.put(9, DanhMucMonAn.RUOU);
        danhMucMap.put(10, DanhMucMonAn.NUOC_NGOT);
    
        System.out.println("\n======= TÌM THEO DANH MỤC =======");
        for (Map.Entry<Integer, DanhMucMonAn> entry : danhMucMap.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
        System.out.println("0. THOÁT");
        System.out.print("Chọn: ");
    
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice == 0) return new ArrayList<>();
    
        DanhMucMonAn danhMuc = danhMucMap.get(choice);
        if (danhMuc == null) {
            System.out.println("Lỗi: Lựa chọn không hợp lệ.");
            return new ArrayList<>();
        }
    
        return layMonAnTheoTruong("DanhMuc", danhMuc.name());
    }
    
    //Lấy món ăn
    public static List<MonAn> layMonAnTheoTruong(String field, String value) {
        List<MonAn> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM monan WHERE " + field + " = ?";
    
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            try (ResultSet rs = stmt.executeQuery()) {
                int stt =1;
                System.out.println("\n======================================= DANH SÁCH MÓN ĂN =======================================");
                System.out.printf("| %-3s | %-5s | %-25s | %-10s | %-40s |\n", "STT","ID", "Tên Món", "Giá", "Mô tả");
               
                while (rs.next()) {
                    int id = rs.getInt("ID_MonAn");
                    String tenMon = rs.getString("TenMon");
                    int gia = rs.getInt("Gia");
                    String moTa = rs.getString("Mota");
                    System.out.println("--------------------------------------------------------------------------------------------------");
    
                    System.out.printf("| %-3d | %-5d | %-25s | %-10d | %-40s |\n", stt++,id, tenMon.length() > 25 ? tenMon.substring(0, 22)+"...":tenMon, gia, moTa.length() > 40 ? moTa.substring(0, 37) + "..." : moTa);
    
                    MonAn mon = new MonAn(id, tenMon, gia, moTa);
                    danhSach.add(mon);
                }
                System.out.println("=================================================================================================");
    
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi lấy danh sách món ăn!");
            e.printStackTrace();
        }
        return danhSach;
    }
    

}

     
    
    