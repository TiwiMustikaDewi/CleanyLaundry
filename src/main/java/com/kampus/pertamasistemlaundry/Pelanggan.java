package com.kampus.pertamasistemlaundry;

public class Pelanggan {
    private int id;
    private String nama;
    private String username;
    private String password;
    private String alamat;
    private String noTelepon;

    public Pelanggan(int id, String nama, String username, String password, String alamat, String noTelepon) {
        this.id = id;
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.alamat = alamat;
        this.noTelepon = noTelepon;
    }
    
    public Pelanggan(String nama, String username, String password, String alamat, String noTelepon) {
        this.nama = nama;
        this.username = username;
        this.password = password;
        this.alamat = alamat;
        this.noTelepon = noTelepon;
    }

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    
    public String getAlamat() {
        return alamat;
    }

    public String getNoTelepon() {
        return noTelepon;
    }
}