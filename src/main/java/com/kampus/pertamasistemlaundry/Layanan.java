package com.kampus.pertamasistemlaundry;

public class Layanan {
    private int idLayanan;
    private String namaLayanan;
    private double hargaLayanan; 

    public Layanan(int idLayanan, String namaLayanan, double hargaLayanan) {
        this.idLayanan = idLayanan;
        this.namaLayanan = namaLayanan;
        this.hargaLayanan = hargaLayanan;
    }

    public Layanan(String namaLayanan, double hargaLayanan) {
        this.namaLayanan = namaLayanan;
        this.hargaLayanan = hargaLayanan;
    }

    public int getIdLayanan(){
        return idLayanan;
    }
    public void setIdLayanan(int idLayanan){
        this.idLayanan = idLayanan;
    }
    
    public String getNamaLayanan() {
        return namaLayanan;
    }

    public void setNamaLayanan(String namaLayanan) {
        this.namaLayanan = namaLayanan;
    }

    public double getHargaLayanan() {
        return hargaLayanan;
    }

    public void setHargaLayanan(double hargaLayanan) {
        this.hargaLayanan = hargaLayanan;
    }
 
    public String getHargaFormatted() {
        java.text.NumberFormat format = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        return format.format(hargaLayanan);
    }
}