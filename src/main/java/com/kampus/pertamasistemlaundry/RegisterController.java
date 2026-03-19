package com.kampus.pertamasistemlaundry;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField tfNama;
    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;
    @FXML private TextField tfAlamat; 
    @FXML private TextField tfNoTelepon; 

 @FXML
private void handleBack() throws IOException {
    App.setRoot("role_selection");
}


    @FXML
    private void handleRegister() throws IOException {
        String nama = tfNama.getText();
        String username = tfUsername.getText();
        String password = pfPassword.getText();
        String alamat = tfAlamat.getText(); 
        String noTelepon = tfNoTelepon.getText(); 

        if (nama.isEmpty() || username.isEmpty() || password.isEmpty() || alamat.isEmpty() || noTelepon.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Semua field harus diisi.");
            return;
        }
        
        try {
            Pelanggan pelangganBaru = new Pelanggan(nama, username, password, alamat, noTelepon);
            Database.registerPelanggan(pelangganBaru);

            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Registrasi Berhasil! Silakan Login.");
            switchToLogin();
            
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                showAlert(Alert.AlertType.ERROR, "Registrasi Gagal", "Username sudah terdaftar. Mohon gunakan username lain.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Gagal melakukan registrasi:\n" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void switchToLogin() throws IOException {
        App.setRoot("login");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
}