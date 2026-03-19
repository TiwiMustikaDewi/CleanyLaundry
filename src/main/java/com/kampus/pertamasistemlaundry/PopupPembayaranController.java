package com.kampus.pertamasistemlaundry;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.InputStream;

public class PopupPembayaranController {

    @FXML private ImageView imgQR;
    @FXML private Label lblAmount;
    @FXML private Button btnClose;

    // dipanggil otomatis setelah FXML di-load
    @FXML
    private void initialize() {
        // kalau gambarnya tidak ada di FXML (fallback) kita bisa load manual:
        if (imgQR != null && imgQR.getImage() == null) {
            InputStream is = getClass().getResourceAsStream("/images/qris.png");
            if (is != null) {
                imgQR.setImage(new Image(is));
            }
        }
    }

    @FXML
    private void handleClose() {
        // tutup stage tempat tombol berada
        Stage s = (Stage) btnClose.getScene().getWindow();
        s.close();
    }

    /**
     * Helper static untuk menampilkan popup. Tidak melempar exception ke pemanggil,
     * tapi menampilkan alert jika gagal load.
     *
     * @param owner  stage pemilik (bisa didapat dari ((Stage) someNode.getScene().getWindow()))
     * @param amount total yang akan ditampilkan (mis. totalHarga)
     */
    public static void showPaymentPopup(Window owner, double amount) {
        try {
            FXMLLoader loader = new FXMLLoader(PopupPembayaranController.class.getResource("/fxml/popup_pembayaran.fxml"));
            Parent root = loader.load();

            PopupPembayaranController controller = loader.getController();
            // format rupiah sederhana (kamu bisa ganti formatting)
            controller.lblAmount.setText(String.format("Total: Rp %.0f", amount));

            Scene scene = new Scene(root);
            Stage popupStage = new Stage();
            popupStage.setScene(scene);
            popupStage.initOwner(owner);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Pembayaran - QRIS");
            popupStage.setResizable(false);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // jika gagal load FXML, tampilkan alert
            Alert a = new Alert(Alert.AlertType.ERROR, "Gagal membuka popup pembayaran:\n" + e.getMessage());
            a.showAndWait();
        }
    }
}
