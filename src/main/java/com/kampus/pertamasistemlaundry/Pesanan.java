package com.kampus.pertamasistemlaundry;

import java.text.NumberFormat; 
import java.util.Locale;      

public class Pesanan {
    private String idTransaksi;
    private String tanggal;
    private String jenisLayanan;
    private double berat;
    private double totalHarga;
    private String status;
    private String namaPelanggan;

    public Pesanan(String idTransaksi, String tanggal, String jenisLayanan, double berat, double totalHarga, String status) {
        this.idTransaksi = idTransaksi;
        this.tanggal = tanggal;
        this.jenisLayanan = jenisLayanan;
        this.berat = berat;
        this.totalHarga = totalHarga;
        this.status = status;
    }

    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJenisLayanan() {
        return jenisLayanan;
    }

    public double getBerat() {
        return berat;
    }

    public double getTotalHarga() {
        return totalHarga;
    }
    
    public String getTotalHargaFormatted() {
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        return formatRupiah.format(totalHarga).replace(",00", ""); 
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getNamaPelanggan() {
        return namaPelanggan;
    }
    
    public void setNamaPelanggan(String namaPelanggan) {
        this.namaPelanggan = namaPelanggan;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}