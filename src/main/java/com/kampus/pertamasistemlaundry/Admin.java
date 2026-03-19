package com.kampus.pertamasistemlaundry;

public class Admin {
    private int idAdmin;
    private String usrAdmin;
    private String passAdmin;

    public Admin(int idAdmin, String usrAdmin, String passAdmin) {
        this.idAdmin = idAdmin;
        this.usrAdmin = usrAdmin;
        this.passAdmin = passAdmin;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public String getUsrAdmin() {
        return usrAdmin;
    }
     public String getPassAdmin() {
        return passAdmin;
    }
}