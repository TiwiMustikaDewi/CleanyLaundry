package com.kampus.pertamasistemlaundry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    
    // URL KONEKSI ke database 'laundry_app'
    private static final String URL = "jdbc:mysql://localhost:3306/laundry_app?useSSL=false&serverTimezone=UTC"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = ""; 
    
    // Metode koneksi
    private static Connection connect() throws SQLException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ DRIVER TIDAK DITEMUKAN: Pastikan library MySQL Connector sudah ditambahkan di pom.xml/module path.");
            throw new SQLException("Driver MySQL tidak ditemukan.", e);
        } catch (SQLException e) {
            System.err.println("❌ KONEKSI GAGAL! Pastikan Laragon/XAMPP running dan kredensial database benar.");
            throw e;
        }
        return conn;
    }

    // =========================================================================
    //                             SETUP & INIT
    // =========================================================================
    public static void initializeDatabase() {
        try {
            // Coba koneksi dan buat tabel jika belum ada
            createTables();
        } catch (SQLException e) {
            System.err.println("Gagal Inisialisasi Database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Metode untuk membuat tabel (jika belum ada)
    private static void createTables() throws SQLException {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // 1. Tabel Layanan
            String createLayanan = "CREATE TABLE IF NOT EXISTS layanan ("
                    + "idLayanan INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "namaLayanan VARCHAR(50) NOT NULL UNIQUE, "
                    + "hargaLayanan DOUBLE NOT NULL"
                    + ");";
            stmt.execute(createLayanan);
            
            // 2. Tabel Pelanggan
            String createPelanggan = "CREATE TABLE IF NOT EXISTS pelanggan ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "nama VARCHAR(100) NOT NULL, "
                    + "username VARCHAR(50) NOT NULL UNIQUE, "
                    + "password VARCHAR(100) NOT NULL, " 
                    + "alamat VARCHAR(255), "
                    + "no_telepon VARCHAR(15)" 
                    + ");";
            stmt.execute(createPelanggan);
            
            // 3. Tabel Pesanan
            String createPesanan = "CREATE TABLE IF NOT EXISTS pesanan ("
                    + "idTransaksi VARCHAR(50) PRIMARY KEY, "
                    + "idPelanggan INTEGER, "
                    + "tanggal DATE NOT NULL, "
                    + "jenisLayanan VARCHAR(50) NOT NULL, "
                    + "berat DOUBLE NOT NULL, "
                    + "totalHarga DOUBLE NOT NULL, "
                    + "status VARCHAR(20) NOT NULL, "
                    + "FOREIGN KEY (idPelanggan) REFERENCES pelanggan(id)"
                    + ");";
            stmt.execute(createPesanan);
            
            // 4. Tabel Admin (Opsional, untuk login Admin)
            String createAdmin = "CREATE TABLE IF NOT EXISTS admin ("
                    + "idAdmin INTEGER PRIMARY KEY AUTO_INCREMENT, "
                    + "usrAdmin VARCHAR(50) NOT NULL UNIQUE, "
                    + "passAdmin VARCHAR(100) NOT NULL"
                    + ");";
            stmt.execute(createAdmin);
            
            // Sisipkan data layanan default (jika tabel kosong)
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM layanan");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO layanan (namaLayanan, hargaLayanan) VALUES ('Cuci Kering', 5000)");
                stmt.executeUpdate("INSERT INTO layanan (namaLayanan, hargaLayanan) VALUES ('Cuci Setrika', 8000)");
                stmt.executeUpdate("INSERT INTO layanan (namaLayanan, hargaLayanan) VALUES ('Setrika Saja', 3500)");
            }
            
            // Sisipkan data admin default (jika tabel kosong)
            rs = stmt.executeQuery("SELECT COUNT(*) FROM admin");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO admin (usrAdmin, passAdmin) VALUES ('admin', 'admin123')");
            }
        }
    }
    
    // =========================================================================
    //                              PELANGGAN (USER)
    // =========================================================================
    public static void registerPelanggan(Pelanggan p) throws SQLException {
        String sql = "INSERT INTO pelanggan (nama, username, password, alamat, no_telepon) VALUES (?, ?, ?, ?, ?)"; 
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNama());
            pstmt.setString(2, p.getUsername());
            pstmt.setString(3, p.getPassword());
            pstmt.setString(4, p.getAlamat());
            pstmt.setString(5, p.getNoTelepon());
            
            pstmt.executeUpdate();
        }
    }
    
    public static Pelanggan loginPelanggan(String username, String password) throws SQLException {
        String sql = "SELECT * FROM pelanggan WHERE username = ? AND password = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return new Pelanggan(
                        rs.getInt("id"),
                        rs.getString("nama"),
                        rs.getString("username"),
                        rs.getString("password"), // Ganti 'pass' jika di tabel pakai 'password'
                        rs.getString("alamat"),
                        rs.getString("no_telepon")
                    );
                }
            }
        }
        return null;
    }
    
    // =========================================================================
    //                                 PESANAN
    // =========================================================================
    public static void addPesanan(Pesanan p) throws SQLException {
        String sql = "INSERT INTO pesanan (idTransaksi, idPelanggan, tanggal, jenisLayanan, berat, totalHarga, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getIdTransaksi());
            pstmt.setInt(2, App.pelangganSaatIni.getId());
            pstmt.setString(3, p.getTanggal());
            pstmt.setString(4, p.getJenisLayanan());
            pstmt.setDouble(5, p.getBerat());
            pstmt.setDouble(6, p.getTotalHarga());
            pstmt.setString(7, p.getStatus());

            pstmt.executeUpdate();
        }
    }

    public static List<Pesanan> getPesananByPelanggan(int idPelanggan) throws SQLException {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT * FROM pesanan WHERE idPelanggan = ? ORDER BY tanggal DESC";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelanggan);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    list.add(new Pesanan(
                        rs.getString("idTransaksi"),
                        rs.getString("tanggal"),
                        rs.getString("jenisLayanan"),
                        rs.getDouble("berat"),
                        rs.getDouble("totalHarga"),
                        rs.getString("status")
                    ));
                }
            }
        }
        return list;
    }
    
    public static Pesanan getLastPesananByPelanggan(int idPelanggan) throws SQLException {
        Pesanan p = null;
        String sql = "SELECT * FROM pesanan WHERE idPelanggan = ? ORDER BY idTransaksi DESC LIMIT 1";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idPelanggan);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()) {
                    p = new Pesanan(
                        rs.getString("idTransaksi"),
                        rs.getString("tanggal"),
                        rs.getString("jenisLayanan"),
                        rs.getDouble("berat"),
                        rs.getDouble("totalHarga"),
                        rs.getString("status")
                    );
                }
            }
        }
        return p;
    }
    
    public static void updateStatusPesanan(String idTransaksi, String statusBaru) throws SQLException {
        String sql = "UPDATE pesanan SET status = ? WHERE idTransaksi = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, statusBaru);
            pstmt.setString(2, idTransaksi);

            pstmt.executeUpdate();
        }
    }
    
    public static int getTotalPesananAktif(int idPelanggan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pesanan WHERE idPelanggan = ? AND status IN ('Diproses', 'Siap Ambil')";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPelanggan);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static int getCountPesananByStatus(String status, int idPelanggan) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pesanan WHERE idPelanggan = ? AND status = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idPelanggan);
            pstmt.setString(2, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    // Metode Admin yang sudah ada
    public static int countPesananStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM pesanan";
        if (status != null) {
            sql += " WHERE status = ?";
        }
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (status != null) {
                pstmt.setString(1, status);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    public static double calculateTotalRevenue() throws SQLException {
        String sql = "SELECT SUM(totalHarga) FROM pesanan WHERE status = 'Selesai'";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    
    public static List<Pesanan> getAllPesananWithPelanggan() throws SQLException {
        List<Pesanan> list = new ArrayList<>();
        String sql = "SELECT pe.*, pl.nama as namaPelanggan, pl.no_telepon as noTeleponPelanggan " 
                   + "FROM pesanan pe JOIN pelanggan pl ON pe.idPelanggan = pl.id "
                   + "ORDER BY pe.tanggal DESC, pe.idTransaksi DESC";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Pesanan p = new Pesanan(
                    rs.getString("idTransaksi"),
                    rs.getString("tanggal"),
                    rs.getString("jenisLayanan"),
                    rs.getDouble("berat"),
                    rs.getDouble("totalHarga"),
                    rs.getString("status")
                );
                p.setNamaPelanggan(rs.getString("namaPelanggan"));
                
                list.add(p);
            }
        }
        return list;
    }
    public static void deletePesanan(String idTransaksi) throws SQLException {
    String sql = "DELETE FROM pesanan WHERE idTransaksi = ?";
    try (Connection conn = connect();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, idTransaksi);
        pstmt.executeUpdate();
    }
}


    // =========================================================================
    //                                 LAYANAN
    // =========================================================================
    public static void addLayanan(Layanan l) throws SQLException {
        String sql = "INSERT INTO layanan (namaLayanan, hargaLayanan) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, l.getNamaLayanan());
            pstmt.setDouble(2, l.getHargaLayanan());
            
            pstmt.executeUpdate();
        }
    }

    public static List<Layanan> getAllLayanan() throws SQLException {
        List<Layanan> list = new ArrayList<>();
        String sql = "SELECT * FROM layanan ORDER BY idLayanan ASC";
        
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while(rs.next()) {
                Layanan l = new Layanan(
                    rs.getString("namaLayanan"),
                    rs.getDouble("hargaLayanan")
                );
                l.setIdLayanan(rs.getInt("idLayanan"));
                list.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Error getAllServices: " + e.getMessage());
        }

        return list;
    }
    
    public static void updateLayanan(Layanan l) throws SQLException {
        String sql = "UPDATE layanan SET namaLayanan=?, hargaLayanan=? WHERE idLayanan=?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, l.getNamaLayanan());
            pstmt.setDouble(2, l.getHargaLayanan());
            pstmt.setInt(3, l.getIdLayanan());

            pstmt.executeUpdate();
        }
    }

    public static void deleteLayanan(int idLayanan) throws SQLException {
        String sql = "DELETE FROM layanan WHERE idLayanan = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idLayanan);
            pstmt.executeUpdate();
        }
    }

    // =========================================================================
    //                                 ADMIN
    // =========================================================================
    public static Admin loginAdmin(String usrAdmin, String passAdmin) throws SQLException {
        String sql = "SELECT * FROM admin WHERE usrAdmin = ? AND passAdmin = ?";
        
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usrAdmin);
            pstmt.setString(2, passAdmin);

            try (ResultSet rs = pstmt.executeQuery()) {
                if(rs.next()){
                    return new Admin(
                        rs.getInt("idAdmin"),
                        rs.getString("usrAdmin"),
                        rs.getString("passAdmin")
                    );
                }
            }
        }
        return null;
    }
}