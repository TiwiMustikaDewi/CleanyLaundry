package com.kampus.pertamasistemlaundry;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class RoleSelectionController {

    @FXML
    private void handlePembeli() throws IOException {
        App.modeLogin = "pelanggan";
        App.setRoot("login");
    }

    @FXML
    private void handlePenjual() throws IOException {
            App.modeLogin = "admin";
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