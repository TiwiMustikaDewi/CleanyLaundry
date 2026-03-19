package com.kampus.pertamasistemlaundry;

import java.io.IOException;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField tfUsername;
    @FXML
    private PasswordField pfPassword;

     @FXML
    private void handleBack() throws IOException {
    App.setRoot("role_selection");
}

    @FXML
    private void handleLogin() throws IOException {
        String username = tfUsername.getText();
        String password = pfPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Kosong", "Username dan Password harus diisi.");
            return;
        }

        try {
          if (App.modeLogin.equals("admin")) {
        Admin admin = Database.loginAdmin(username, password);
        if (admin != null) {
            App.adminSaatIni = admin;
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login Admin Berhasil!");
            App.setRoot("PrimarySeller"); 
            return;
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Admin Gagal", "Username atau Password Admin salah.");
            return;
        }
          }
            
            Pelanggan user = Database.loginPelanggan(username, password);

            if (user != null) {
                App.pelangganSaatIni = user; 
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Login Berhasil!");
                App.setRoot("primary"); 
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah.");
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Kesalahan Database", "Terjadi kesalahan saat mencoba login:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void switchToRegister() throws IOException {
        App.setRoot("register");
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}